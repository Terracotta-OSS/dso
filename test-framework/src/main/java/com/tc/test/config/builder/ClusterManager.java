/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tc.test.config.builder;

import com.tc.config.Loader;
import com.tc.test.TestConfigUtil;
import com.tc.test.process.ExternalDsoServer;
import com.tc.util.ProductInfo;
import com.tc.util.TcConfigBuilder;
import com.tc.util.concurrent.ThreadUtil;
import com.terracottatech.config.Server;
import com.terracottatech.config.TcConfigDocument;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.xmlbeans.XmlException;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.ContentResponse;
import org.eclipse.jetty.client.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.function.Predicate;

/**
 * @author Ludovic Orban
 */
public class ClusterManager {

  private static final Logger LOG = LoggerFactory.getLogger(ClusterManager.class);

  public static final String DEFAULT_MAX_DIRECT_MEMORY_SIZE = "3G";
  public static final String DEFAULT_MAX_DATA_SIZE = "512M";

  private final TcConfigBuilder tcConfigBuilder;
  private final File workingDir;
  private final String version;

  private final List<String> extraJvmArgs = new ArrayList<String>();
  private final Map<String, ExternalDsoServer> externalDsoServers = new TreeMap<String, ExternalDsoServer>();
  private final TcConfig tcConfig;
  private String maxDirectMemorySize = DEFAULT_MAX_DIRECT_MEMORY_SIZE;

  private final Map<String, String> systemProperties = new TreeMap<String, String>();

  public ClusterManager(Class<?> testClass, TcConfig tcConfig) throws IOException, XmlException {
    this(new File(TestConfigUtil.getTcBaseDirPath(), "temp" + File.separator + testClass.getSimpleName()), tcConfig, true);
  }

  public ClusterManager(Class<?> testClass, TcConfig tcConfig, boolean fillUpConfig) throws IOException, XmlException {
    this(new File(TestConfigUtil.getTcBaseDirPath(), "temp" + File.separator + testClass.getSimpleName()), tcConfig, fillUpConfig);
  }

  public ClusterManager(File workingDir, TcConfig tcConfig, boolean fillUpConfig) throws IOException, XmlException {
    if (fillUpConfig) {
      tcConfig.fillUpConfig();
    }

    XStream xstream = new XStream(new DomDriver());
    xstream.autodetectAnnotations(true);
    String xml = xstream.toXML(tcConfig);
    LOG.info("Starting cluster for config: " + xml);
    TcConfigDocument parsedDoc = new Loader().parse(xml);

    this.tcConfig = tcConfig;
    this.tcConfigBuilder = new TcConfigBuilder(parsedDoc);
    this.tcConfigBuilder.setClientLogs(new File(workingDir, "client-logs").getAbsolutePath());
    this.workingDir = workingDir;
    this.version = guessMavenArtifactVersion();
    LOG.info("Guessed version: " + this.version);
    if (this.version == null) {
      throw new IllegalStateException("cannot figure out version");
    }
  }

  public String getVersion() {
    return version;
  }

  public File getWorkingDir() {
    return workingDir;
  }

  public String getMaxDirectMemorySize() {
    return maxDirectMemorySize;
  }

  public void setMaxDirectMemorySize(String maxDirectMemorySize) {
    this.maxDirectMemorySize = maxDirectMemorySize;
  }

  public void addExtraJvmArg(String arg) {
    extraJvmArgs.add(arg);
  }

  public Map<String, String> getSystemProperties() {
    return systemProperties;
  }

  public void start() throws Exception {
    String war = findAgentWarLocation(version);
    workingDir.mkdirs();

    Server[] servers = tcConfigBuilder.getServers();
    for (Server server : servers) {
      String serverName = server.getName();

      File serverWorkingDir = server.getName() != null ? new File(workingDir, server.getName()) : workingDir;
      try {
        FileUtils.deleteDirectory(serverWorkingDir);
      } catch (IOException ioe) {
        // ignore
      }
      serverWorkingDir.mkdir();
      ExternalDsoServer externalDsoServer = new ExternalDsoServer(serverWorkingDir, tcConfigBuilder.newInputStream(), serverName);
      externalDsoServer.addJvmArg("-Dcom.tc.management.war=" + war);
      externalDsoServer.addJvmArg("-XX:MaxDirectMemorySize=" + maxDirectMemorySize);
      for (String extraJvmArg : extraJvmArgs) {
        externalDsoServer.addJvmArg(extraJvmArg);
      }

      for (Map.Entry<String, String> entry : systemProperties.entrySet()) {
        externalDsoServer.addJvmArg("-D" + entry.getKey() + "=" + entry.getValue());
      }

      externalDsoServer.startWithoutWait();

      externalDsoServers.put(serverName, externalDsoServer);
    }

    LOG.debug("Waiting for TSA agents to initialize...");
    for (Server server : servers) {
      waitUntilTsaAgentInitialized(server.getManagementPort().getIntValue());
      LOG.debug("{} initialized", server.getName());
    }
    System.out.println("All TSA agents started successfully");
  }

  public void crash(int groupIdx, int serverIdx) throws Exception {
    ExternalDsoServer externalDsoServer = externalDsoServers.get(nameOf(groupIdx, serverIdx));
    externalDsoServer.getServerProc().crash();
  }

  public void restart(int groupIdx, int serverIdx) throws Exception {
    ExternalDsoServer externalDsoServer = externalDsoServers.get(nameOf(groupIdx, serverIdx));
    if (externalDsoServer.getServerProc().isRunning()) {
      return;
    }
    externalDsoServer.getServerProc().startWithoutWait();
    waitUntilTsaAgentInitialized(externalDsoServer.getServerGroupPort());
  }

  public void stop() throws Exception {
    for (ExternalDsoServer externalDsoServer : externalDsoServers.values()) {
      try {
        externalDsoServer.stop();
      } catch (Exception e) {
        LOG.error("error stopping server", e);
      }
    }
    externalDsoServers.clear();
  }

  public void stopSecured(String username, String password) throws Exception {
    for (ExternalDsoServer externalDsoServer : externalDsoServers.values()) {
      try {
        externalDsoServer.stopSecured(username, password);
      } catch (Exception e) {
        LOG.error("error stopping server", e);
      }
    }
    externalDsoServers.clear();
  }

  private String nameOf(int groupIdx, int serverIdx) {
    return tcConfig.serverAt(groupIdx, serverIdx).getName();
  }

  // TODO: If we remove devmode, this method can be removed and just use ProductInfo directly

  private static final String detectEdition() {
    String edition = ProductInfo.OPENSOURCE;
    try {
      Class.forName("com.tc.util.ProductInfoEnterpriseBundle");
      edition = ProductInfo.ENTERPRISE;
    } catch (ClassNotFoundException e) {
      // ignore
    }
    return edition;
  }

  public static String findAgentWarLocation(String version) {
    String groupId = "org.terracotta";
    String artifactId = "management-tsa-war";
    if (ProductInfo.ENTERPRISE.equals(detectEdition())) {
      groupId = "com.terracottatech";
      artifactId = "ent-management-tsa-war";
    }
    return findWarLocation(groupId, artifactId, version);
  }

  public String versionOf(String artifact) {
    String jarLocation = null;
    String[] jars = System.getProperty("java.class.path").split(File.pathSeparator);
    for (String jar : jars) {
      String filename = new File(jar).getName();
      if (filename.startsWith(artifact + "-") && filename.endsWith(".jar")) {
        jarLocation = jar;
        break;
      }
    }
    if (jarLocation == null) {
      jars = System.getProperty("surefire.test.class.path").split(File.pathSeparator);
      for (String jar : jars) {
        String filename = new File(jar).getName();
        if (filename.startsWith(artifact + "-") && filename.endsWith(".jar")) {
          jarLocation = jar;
          break;
        }
      }
    }
    if (jarLocation == null) {
      return null;
    }
    return new File(jarLocation).getParentFile().getName();
  }

  public static String findWarLocation(String gid, String aid, String ver) {
    return MavenArtifactFinder.findArtifactLocation(gid, aid, ver, null, "war");
  }

  public static String guessMavenArtifactVersion() {
    try {
      return MavenArtifactFinder.figureCurrentArtifactMavenVersion();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void waitUntilTsaAgentInitialized(int port) throws Exception {
    if (tcConfig.isSecure()) {
      https_waitUntilTsaAgentInitialized(port);
    } else {
      http_waitUntilTsaAgentInitialized(port);
    }
  }

  public void allServersWaitUntilL1ThroughTsaAgentInitialized () throws Exception {
    Server[] servers = tcConfigBuilder.getServers();
    for (Server server : servers) {
      waitUntilL1ThroughTsaAgentInitialized(server.getTsaGroupPort().getIntValue());
      LOG.debug("{} initialized", server.getName());
    }
  }


  private void waitUntilL1ThroughTsaAgentInitialized(int port) throws Exception {
    if (tcConfig.isSecure()) {
     https_waitUntilL1ThroughTsaAgentInitialized(port);
    } else {
      http_waitUntilL1ThroughTsaAgentInitialized(port);
    }
  }

  private static void https_waitUntilTsaAgentInitialized(int port) throws Exception {
    HttpClient httpClient = new HttpClient();
    try {
      httpClient.start();
      waitForAgentInitialization(port, httpClient, ClusterManager::contentTypeIsJson);
    } finally {
      httpClient.stop();
    }
  }

  private static void https_waitUntilL1ThroughTsaAgentInitialized(int port) throws Exception {
    HttpClient httpClient = new HttpClient();
    try {
      httpClient.start();
      waitForAgentInitialization(port, httpClient, ClusterManager::responseContainsAgencyEhcache);
    } finally {
      httpClient.stop();
    }
  }

  private static void http_waitUntilTsaAgentInitialized(int port) {
    for (int i = 0; i < 30; i++) {
      try {
        URL url = new URL("http://localhost:" + port + "/tc-management-api/v2/agents");
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        IOUtils.copy(inputStream, new OutputStream() {
          @Override
          public void write(int b) throws IOException {
            // send to /dev/null
          }
        });
        inputStream.close();
        LOG.info("TSA agent listening on port {}", port);
        break;
      } catch (IOException ioe) {
        LOG.debug("Waiting for TSA agent to initialize on port {}... (#{})", port, i);
        ThreadUtil.reallySleep(1000L);
      }
    }
  }

  private static void http_waitUntilL1ThroughTsaAgentInitialized(int port) {
    for (int i = 0; i < 30; i++) {
      try {
        URL url = new URL("http://localhost:" + port + "/tc-management-api/v2/agents");
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
          Reader reader = new BufferedReader(new InputStreamReader(inputStream,
                  Charset.forName("UTF-8")));
          int n;
          while ((n = reader.read(buffer)) != -1) {
            writer.write(buffer, 0, n);
          }
        } finally {
          inputStream.close();
        }

        String result = writer.toString();
        boolean contains = result.contains("\"agencyOf\": \"Ehcache\"");
        inputStream.close();
        if(!contains) {
          LOG.info("TSA agent NOT listening on port, we try again {}", port);
          ThreadUtil.reallySleep(1000L);
          continue;
        }
        LOG.info("TSA agent aggregating L1 agent");
        break;
      } catch (IOException ioe) {
        LOG.debug("Waiting for TSA agent to initialize on port {}... (#{})", port, i);
        ThreadUtil.reallySleep(1000L);
      }
    }
  }

  private static void waitForAgentInitialization(int port, HttpClient httpClient, Predicate<ContentResponse> predicate) throws InterruptedException, TimeoutException, ExecutionException {
    for (int i = 0; i < 30; i++) {
      Request request = httpClient.newRequest("https://localhost:" + port + "/tc-management-api/v2/agents");
      ContentResponse send = request.send();
      boolean success = predicate.test(send);
      if (success) {
        LOG.info("TSA agent listening on port {}", port);
        break;
      }

      ThreadUtil.reallySleep(1000L);
      LOG.debug("Waiting for TSA agent to initialize on port {}... (#{})", port, i);
    }
  }

  private static boolean contentTypeIsJson(ContentResponse contentResponse) {
    Collection<String> contentTypes =  contentResponse.getHeaders().getValuesList("content-type");
    for (String contentType : contentTypes) {
      if (contentType.startsWith("application/json")) {
        return true;
      }
    }
    return false;
  }

  private static boolean responseContainsAgencyEhcache(ContentResponse contentResponse) {
    if(contentResponse.getContentAsString().contains("\"agencyOf\": \"Ehcache\"")) {
      return true;
    }
    return false;
  }

}
