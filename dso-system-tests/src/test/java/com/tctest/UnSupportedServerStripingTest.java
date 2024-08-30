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

import com.tc.object.BaseDSOTestCase;
import com.tc.test.process.ExternalDsoServer;
import com.tc.util.Grep;
import com.tc.util.TcConfigBuilder;

import java.io.File;
import java.util.List;

public class UnSupportedServerStripingTest extends BaseDSOTestCase {

  /**
   * Test scenario when DSO server in open source mode, used with server striping
   */
  public void testStripingInOpensource() throws Exception {
    File workDir = new File(getTempDirectory(), "test1");
    workDir.mkdirs();

    TcConfigBuilder configBuilder = new TcConfigBuilder("/com/tctest/tc-2-server-groups-config.xml");
    configBuilder.randomizePorts();

    ExternalDsoServer server = new ExternalDsoServer(workDir, configBuilder.newInputStream(), "server1");

    server.startAndWait(30);
    assertFalse("Expected the server to fail due to unsupported feature", server.isRunning());

    List<CharSequence> result = Grep.grep("'server striping' capability is not supported in Terracotta Open Source Version",
                                          server.getServerLog());
    System.out.println("Output found: " + result);
    assertTrue(result.size() > 0);
  }
}
