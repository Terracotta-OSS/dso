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
package com.tc.util.io;

import com.tc.net.core.SecurityInfo;
import com.tc.util.Assert;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

import junit.framework.TestCase;

import static java.lang.Thread.sleep;

public class ServerURLTest extends TestCase {

  public void testServerURLTimeout() throws Exception {
    ServerSocket server;
    try {
      server = new ServerSocket(0);
      Thread t = new Thread(() -> {
        try {
          Socket socket = server.accept();
          BufferedWriter response = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
          response.write("connected to server");
          sleep(1500); //default timeout for the ServerURL for this test 1000ms so it will timeout after 1000ms.
          response.write("\n");
          response.flush();
          socket.close();
        } catch (Exception e) {
          throw new RuntimeException(e);
        } finally {
          try {
            server.close();
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        }
      });
      t.start();
      SecurityInfo securityInfo = new SecurityInfo();
      ServerURL serverURL = new ServerURL(server.getInetAddress()
          .getHostAddress(), server.getLocalPort(), "/groupidmap", 1000, securityInfo);
      serverURL.openStream();
      Assert.fail("Connection should have timed out");
    } catch (SocketTimeoutException e) {
      //pass
    }
  }
}