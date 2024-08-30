/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.terracotta.test.util.TestBaseUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * @author Ludovic Orban
 */
public class MavenArtifactFinder {

  private static final Logger LOG = LoggerFactory.getLogger(MavenArtifactFinder.class);

  public static String findArtifactLocation(String gid, String aid, String ver, final String classifier, final String type) {
    String m2Root =  System.getProperty("localRepository") != null ? System.getProperty("localRepository") : System.getProperty("user.home") + "/.m2/repository".replace('/', File.separatorChar);
    if (System.getProperty("maven.repo.local") != null) {
      m2Root = System.getProperty("maven.repo.local");
      LOG.info("Found maven.repo.local defined as a system property! Using m2root=" + m2Root);
    }

    String artifactDir = m2Root + ("/" + gid.replace('.', '/') + "/" + aid + "/").replace('/', File.separatorChar) + ver;
    LOG.info("Looking for artifact file in path " + artifactDir);

    List<String> files = Arrays.asList(new File(artifactDir).list(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        String end = "";
        if (classifier != null) {
          end += "-" + classifier;
        }
        String ext = type == null ? "jar" : type;
        end += "." + ext;

        return name.endsWith(end) && !name.endsWith("-sources." + ext) && !name.endsWith("-tests." + ext);
      }
    }));
    if (files.isEmpty()) {
      throw new AssertionError("No corresponding artifact file found in [" + artifactDir + "]");
    }
    Collections.sort(files);

    // always take the last one of the sorted list, it should be the latest version
    String artifactPath = artifactDir + File.separator + files.get(files.size() - 1);
    LOG.info("Found artifact file at " + artifactPath);
    return artifactPath;
  }

  public static String figureCurrentArtifactMavenVersion() throws IOException {
    String jar = TestBaseUtil.jarFor(MavenArtifactFinder.class);
    if (jar == null) {
      throw new AssertionError("Cannot find JAR for class: " + MavenArtifactFinder.class);
    }

    if (jar.endsWith(".jar")) {
      LOG.info("Guessing version from pom.properties in JAR: " + jar);
      JarFile jarFile = new JarFile(jar);
      ZipEntry entry = jarFile.getEntry("META-INF/maven/org.terracotta.test/test-framework/pom.properties");
      if (entry == null) {
        throw new AssertionError("cannot find entry [META-INF/maven/org.terracotta.test/test-framework/pom.properties] in JAR file");
      }
      InputStream inputStream = jarFile.getInputStream(entry);
      Properties properties;
      try {
        properties = new Properties();
        properties.load(inputStream);
      } finally {
        IOUtils.closeQuietly(inputStream);
      }
      return properties.getProperty("version");
    } else {
      // running from IDE? try to get the version from the pom file
      try {
        File folder = new File(jar).getAbsoluteFile();
        File pomFile = new File(folder, "pom.xml");

        for (int i = 0; i < 10; i++) {
          if (pomFile.exists()) {
            break;
          }
          folder = folder.getParentFile();
          pomFile = new File(folder, "pom.xml");
        }
        if (!pomFile.exists()) {
          throw new AssertionError("cannot find pom file to guess version");
        }
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document doc = dBuilder.parse(pomFile);
        Map<String, String> versionMap = getVersionsFromPom(doc);
        String version = versionMap.get("version");
        return version != null ? version : versionMap.get("parent/version");
      } catch (Exception e) {
        e.printStackTrace();
      }
      throw new AssertionError("cannot guess version");
    }
  }

  private static Map<String, String> getVersionsFromPom(Document doc) {
    HashMap<String, String> result = new HashMap<>();
    List<Node> nodeList = new ArrayList<>();
    gatherTextNodes(doc.getDocumentElement(), nodeList);
    NodeList childNodes = doc.getDocumentElement().getChildNodes();
    nodeList.stream().forEach(node -> {
      if ("version".equals(node.getNodeName())) {
        result.put(buildNodePath(node), node.getTextContent());
      }
    });

    return result;
  }

  private static void gatherTextNodes(Node node, List<Node> nodeList) {
    if (node.getNodeType() == Node.ELEMENT_NODE) {
      nodeList.add(node);
    }
    NodeList childNodes = node.getChildNodes();
    for (int i = 0; i < childNodes.getLength(); i++) {
      gatherTextNodes(childNodes.item(i), nodeList);
    }
  }

  private static String buildNodePath(Node node) {
    List<String> nodeList = new ArrayList<>();
    while (node != null) {
      nodeList.add(node.getNodeName());
      node = node.getParentNode();
      if (node.getNodeName().equals("project")) {
        break;
      }
    }
    Collections.reverse(nodeList);
    return nodeList.stream().collect(Collectors.joining("/"));
  }
}
