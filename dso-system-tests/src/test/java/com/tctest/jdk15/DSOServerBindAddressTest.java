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
package com.tctest.jdk15;

import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.config.schema.setup.L2ConfigurationSetupManager;
import com.tc.config.schema.setup.TestConfigurationSetupManagerFactory;
import com.tc.lang.StartupHelper;
import com.tc.lang.StartupHelper.StartupAction;
import com.tc.lang.TCThreadGroup;
import com.tc.lang.ThrowableHandlerImpl;
import com.tc.logging.TCLogging;
import com.tc.object.BaseDSOTestCase;
import com.tc.objectserver.impl.DistributedObjectServer;
import com.tc.objectserver.managedobject.ManagedObjectStateFactory;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;
import com.tc.server.TCServerImpl;
import com.tc.test.config.builder.ClusterManager;
import com.tc.util.Assert;
import com.tc.util.PortChooser;
import org.eclipse.jetty.util.resource.Resource;
import org.junit.experimental.categories.Category;
import org.terracotta.test.categories.CheckShorts;
import org.terracotta.test.util.WaitUtil;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URLConnection;
import java.util.concurrent.Callable;

/**
 * Test for DEV-1060
 *
 * @author Manoj
 */
@Category(CheckShorts.class)
public class DSOServerBindAddressTest extends BaseDSOTestCase {
  private final TCThreadGroup   group     = new TCThreadGroup(
                                                              new ThrowableHandlerImpl(TCLogging
                                                                  .getLogger(DistributedObjectServer.class)));
  private static final String[] bindAddrs = { "0.0.0.0", "127.0.0.1", localAddr() };
  private TCServerImpl          server;

  /*
   * https://github.com/eclipse/jetty.project/issues/1425
   *
   * Jetty's WebAppContext stop() method leaves open handles on stuff in WEB-INF/lib and since
   * this test runs servers inline, the 2nd attempt to run the server fails to clear out the
   * temp area under the target/DSOServerBindAddressTest/l2-data/jetty.
   *
   * Therefor, on Windows disable caching for the "jar" protocol and also Jetty's Resource class.
   */
  private static final boolean isWindows = System.getProperty("os.name").toLowerCase().contains("windows");

  static String localAddr() {
    try {
      return NetworkInterface.networkInterfaces()
        .filter(networkInterface -> {
          try {
            return networkInterface.isUp() && (networkInterface.getHardwareAddress() != null);
          } catch (SocketException e) {
            return false;
          }
        })
        .flatMap(NetworkInterface::inetAddresses)
        .filter(addr -> (addr instanceof Inet4Address) && !addr.isLoopbackAddress())
        .findFirst()
        .orElseThrow(() -> new AssertionError("No non-loopback addresses for this host")).getHostAddress();
    } catch (SocketException e) {
      throw new AssertionError(e);
    }
  }

  private class StartAction implements StartupAction {
    private final int    tsaPort;
    private final int    jmxPort;
    private final String bindAddr;
    private final int    tsaGroupPort;
    private final int    managementPort;

    public StartAction(String bindAddr, int tsaPort, int jmxPort, int tsaGroupPort, int mangementPort) {
      this.bindAddr = bindAddr;
      this.tsaPort = tsaPort;
      this.jmxPort = jmxPort;
      this.tsaGroupPort = tsaGroupPort;
      this.managementPort = mangementPort;
    }

    @Override
    public void execute() throws Throwable {
      TCPropertiesImpl.getProperties().setProperty(TCPropertiesConsts.L2_OBJECTMANAGER_DGC_INLINE_ENABLED, "false");
      TCPropertiesImpl.getProperties().setProperty(TCPropertiesConsts.L2_OFFHEAP_SKIP_JVMARG_CHECK, "true");
      server = new TCServerImpl(createL2Manager(bindAddr, tsaPort, jmxPort, tsaGroupPort, managementPort));
      server.start();
    }

  }

  public void testDSOServerAndJMXBindAddress() throws Exception {
    boolean jarProtocolUseCaches = URLConnection.getDefaultUseCaches("jar");

    if (isWindows) {
      URLConnection.setDefaultUseCaches("jar", false);
    }

    System.setProperty("com.tc.management.war", ClusterManager.findWarLocation("org.terracotta", "management-tsa-war",
      ClusterManager.guessMavenArtifactVersion()));
    PortChooser pc = new PortChooser();

    ManagedObjectStateFactory.disableSingleton(true);

    for (int i = 0; i < bindAddrs.length; i++) {
      String bind = bindAddrs[i];
      int tsaPort = pc.chooseRandomPort();
      int jmxPort = pc.chooseRandomPort();
      int tsaGroupPort = pc.chooseRandomPort();
      int managementPort = pc.chooseRandomPort();

      new StartupHelper(group, new StartAction(bind, tsaPort, jmxPort, tsaGroupPort, managementPort)).startUp();

      final DistributedObjectServer dsoServer = server.getDSOServer();
      WaitUtil.waitUntilCallableReturnsTrue(new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {
          try {
            dsoServer.getListenAddr();
            return true;
          } catch (IllegalStateException ise) {
            //
          }
          return false;
        }
      });

      if (i == 0) {
        Assert.eval(dsoServer.getListenAddr().isAnyLocalAddress());
      } else {
        assertEquals(dsoServer.getListenAddr().getHostAddress(), bind);
      }
      Assert.assertNotNull(dsoServer.getJMXConnServer());
      assertEquals(dsoServer.getJMXConnServer().getAddress().getHost(), bind);

      testSocketConnect(bind, new int[] { tsaPort, jmxPort, tsaGroupPort, managementPort }, true);

      server.stop();
      Thread.sleep(3000);
    }

    if (isWindows) {
      URLConnection.setDefaultUseCaches("jar", jarProtocolUseCaches);
    }
  }

  private void testSocketConnect(final String host, int[] ports, boolean testNegative) throws Exception {
    InetAddress addr = InetAddress.getByName(host);
    if (addr.isAnyLocalAddress()) {
      // should be able to connect on both localhost and local IP
      testSocketConnect("127.0.0.1", ports, false);
      testSocketConnect(localAddr(), ports, false);
    } else {
      // positive case
      for (final int port : ports) {
        WaitUtil.waitUntilCallableReturnsTrue(new Callable<Boolean>() {
          @Override
          public Boolean call() throws Exception {
            try {
              testSocket(host, port, false);
              return true;
            } catch (IOException e) {
              return false;
            }
          }
        });
      }

      if (testNegative) {
        // negative case
        for (int port : ports) {
          if (addr.isLoopbackAddress()) {
            testSocket(localAddr(), port, true);
          } else if (InetAddress.getByName(localAddr()).equals(addr)) {
            testSocket("127.0.0.1", port, true);
          } else {
            throw new AssertionError(addr);
          }
        }
      }
    }
  }

  private static void testSocket(String host, int port, boolean expectFailure) throws IOException {
    System.err.print("testing connect on " + host + ":" + port + " ");
    Socket s = null;
    try {
      s = new Socket(host, port);
      if (expectFailure) {
        System.err.println("[FAIL]");
        throw new AssertionError("should not connect");
      }
    } catch (IOException ioe) {
      if (!expectFailure) {
        System.err.println("[FAIL]");
        throw ioe;
      }
    } finally {
      closeQuietly(s);
    }

    System.err.println("[OK]");
  }

  private static void closeQuietly(Socket s) {
    if (s == null) return;
    try {
      s.close();
    } catch (IOException ioe) {
      // ignore
    }
  }

  public L2ConfigurationSetupManager createL2Manager(String bindAddress, int tsaPort, int jmxPort, int tsaGroupPort,
                                                     int managementPort)
    throws ConfigurationSetupException {
    TestConfigurationSetupManagerFactory factory = super.configFactory();

    factory.l2DSOConfig().getDataStorage().setSize("64m");
    factory.l2DSOConfig().getOffheap().setSize("64m");

    factory.l2DSOConfig().tsaPort().setIntValue(tsaPort);
    factory.l2DSOConfig().tsaPort().setBind(bindAddress);

    factory.l2CommonConfig().jmxPort().setIntValue(jmxPort);
    factory.l2CommonConfig().jmxPort().setBind(bindAddress);

    factory.l2DSOConfig().tsaGroupPort().setIntValue(tsaGroupPort);
    factory.l2DSOConfig().tsaGroupPort().setBind(bindAddress);

    factory.l2CommonConfig().managementPort().setIntValue(managementPort);
    factory.l2CommonConfig().managementPort().setBind(bindAddress);

    factory.l2DSOConfig().setJmxEnabled(true);

    return factory.getL2TVSConfigurationSetupManager();
  }
}
