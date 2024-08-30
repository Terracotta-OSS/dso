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
package com.tc.config.schema.setup.sources;

import com.tc.util.Assert;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import junit.framework.TestCase;

public class URLConfigurationSourceTest extends TestCase {

  private URLConfigurationSource configSrc;
  private ServerSocket           server;

  @Override
  public void setUp() throws Exception {
    this.server = new ServerSocket(0);
    Thread t = new Thread() {
      @Override
      public void run() {
        try {
          Socket socket = server.accept();
          BufferedWriter response = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
          response.write("connected to server");
          sleep(5000);
          response.write("\n");
          response.flush();
          socket.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    t.start();
    this.configSrc = new URLConfigurationSource("http://" + server.getInetAddress().getHostAddress() + ":"
                                                + server.getLocalPort());
  }

  public void testGetInputStreamFailure() throws Exception {
    try {
      configSrc.getInputStream(1);
      throw Assert.failure("Connection should have timed out");
    } catch (SocketTimeoutException e) {
      // pass
    }
  }

  @Override
  public void tearDown() throws Exception {
    server.close();
  }
}
