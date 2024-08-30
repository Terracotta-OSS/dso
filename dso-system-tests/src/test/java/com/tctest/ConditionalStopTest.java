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
package com.tctest;

import com.tc.admin.TCStop;
import com.tc.object.BaseDSOTestCase;
import com.tc.server.util.ServerStat;
import com.tc.test.process.ExternalDsoServer;
import com.tc.util.Assert;
import com.tc.util.TcConfigBuilder;
import com.tc.util.concurrent.ThreadUtil;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.tc.lang.ServerExitStatus.EXITCODE_RESTART_IN_SAFE_MODE_REQUEST;
import static com.tc.lang.ServerExitStatus.EXITCODE_RESTART_REQUEST;
import static com.tc.lang.ServerExitStatus.EXITCODE_STOP_REQUEST;

public class ConditionalStopTest extends BaseDSOTestCase {
  private static final String SERVER_NAME_2      = "server-2";
  private static final String SERVER_NAME_1      = "server-1";
  private TcConfigBuilder     configBuilder;
  private ExternalDsoServer   server_1, server_2;
  private int                 managementPort_1, managementPort_2;
  private final long          SHUTDOWN_WAIT_TIME = TimeUnit.NANOSECONDS.convert(120, TimeUnit.SECONDS);

  @Override
  protected boolean cleanTempDir() {
    return true;
  }

  @Override
  protected void setUp() throws Exception {
    configBuilder = new TcConfigBuilder("/com/tc/tc-force-stop-test.xml");
    configBuilder.randomizePorts();

    managementPort_1 = configBuilder.getManagementPort(0);
    managementPort_2 = configBuilder.getManagementPort(1);

    server_1 = createServer(SERVER_NAME_1);
    server_2 = createServer(SERVER_NAME_2);
    server_1.start();
    System.out.println("server1 started");
    waitTillBecomeActive(managementPort_1);
    System.out.println("server1 became active");
    server_2.start();
    System.out.println("server2 started");
    waitTillBecomePassiveStandBy(managementPort_2);
    System.out.println("server2 became passive");

  }

  public void testConditionalStop() throws Exception {
    runTest(true, false, false, EXITCODE_STOP_REQUEST);
  }

  public void testConditionalRestart() throws Exception {
    runTest(true, true, false, EXITCODE_RESTART_REQUEST);
  }

  public void testConditionalRestartInSafeMode() throws Exception {
    runTest(true, false, true, EXITCODE_RESTART_IN_SAFE_MODE_REQUEST);
  }

  public void testRestart() throws Exception {
    runTest(false, true, false, EXITCODE_RESTART_REQUEST);
  }

  public void testRestartInSafeMode() throws Exception {
    runTest(false, false, true, EXITCODE_RESTART_IN_SAFE_MODE_REQUEST);
  }

  public void testConditionalActiveStopFailure() {
    try {
      stop(managementPort_1, false, true, false, false);
    } catch (Exception expected) {
      assertContains("not stopping the server", expected.getMessage());
      assertTrue(server_1.isRunning());
    }
  }

  public void testConditionalPassiveStopFailure() {
    try {
      stop(managementPort_2, true, false, false, false);
    } catch (Exception expected) {
      assertContains("not stopping the server", expected.getMessage());
      assertTrue(server_2.isRunning());
    }
  }

  private void runTest(boolean conditional,
                       boolean restart,
                       boolean restartInSafeMode,
                       int exitStatus) throws InterruptedException {
    AtomicInteger activeExitStatus = new AtomicInteger();
    AtomicInteger passiveExitStatus = new AtomicInteger();
    Thread activeStop = new Thread(() -> {
      try {
        stop(managementPort_1, conditional, false, restart, restartInSafeMode);
        activeExitStatus.set(server_1.waitForExit());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    Thread passiveStop = new Thread(() -> {
      try {
        stop(managementPort_2, false, conditional, restart, restartInSafeMode);
        passiveExitStatus.set(server_2.waitForExit());
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });

    activeStop.start();
    passiveStop.start();

    activeStop.join();
    passiveStop.join();

    Assert.assertEquals(exitStatus, activeExitStatus.get());
    Assert.assertEquals(exitStatus, passiveExitStatus.get());
  }

  private void stop(int jmxPort, boolean stopIfActive, boolean stopIfPassive, boolean restart, boolean restartInSafeMode) throws Exception {
    TCStop.restStop("localhost", jmxPort, null, null, false, false, false, stopIfActive, stopIfPassive, restart, restartInSafeMode);
    waitUntilShutdown(jmxPort);
  }

  private void waitUntilShutdown(int jmxPort) throws Exception {
    long start = System.nanoTime();
    long timeout = start + SHUTDOWN_WAIT_TIME;
    while (isRunning(jmxPort)) {
      Thread.sleep(1000);
      if (System.nanoTime() > timeout) {
        System.out.println("Server was shutdown but still up after " + SHUTDOWN_WAIT_TIME);
        break;
      }
    }
  }

  private boolean isRunning(int jmxPort) {
    Socket socket = null;
    try {
      socket = new Socket("localhost", jmxPort);
      if (!socket.isConnected()) throw new AssertionError();
      return true;
    } catch (IOException e) {
      return false;
    } finally {
      if (socket != null) {
        try {
          socket.close();
        } catch (IOException ioe) {
          // ignore
        }
      }
    }
  }

  private boolean isActive(int managementPort) {
    try {
      ServerStat stats = ServerStat.getStats("localhost", managementPort, null, null, false, true);
      return "ACTIVE-COORDINATOR".equals(stats.getState());
    } catch (Exception e) {
      return false;
    }
  }

  private boolean isPassiveStandBy(int managementPort) {
    try {
      ServerStat stats = ServerStat.getStats("localhost", managementPort, null, null, false, true);
      return "PASSIVE-STANDBY".equals(stats.getState());
    } catch (Exception e) {
      return false;
    }
  }

  private void waitTillBecomeActive(int managementPort) {
    while (true) {
      if (isActive(managementPort)) break;
      ThreadUtil.reallySleep(1000);
    }
  }

  private void waitTillBecomePassiveStandBy(int managementPort) {
    while (true) {
      if (isPassiveStandBy(managementPort)) break;
      ThreadUtil.reallySleep(1000);
    }
  }

  private ExternalDsoServer createServer(final String serverName) throws IOException {
    return new ExternalDsoServer(getWorkDir(serverName), configBuilder.newInputStream(), serverName);
  }

  @Override
  protected void tearDown() throws Exception {
    System.err.println("in tearDown");
    if (server_1 != null && server_1.isRunning()) server_1.stop();
    if (server_2 != null && server_2.isRunning()) server_2.stop();
  }

  private File getWorkDir(final String subDir) throws IOException {
    File workDir = new File(getTempDirectory(), subDir);
    workDir.mkdirs();
    return workDir;
  }

}
