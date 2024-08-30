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
package com.tc.test;

import org.hsqldb.HsqlProperties;
import org.hsqldb.Server;
import org.hsqldb.ServerConfiguration;

// Mainly copy from HsqlDBServer class of dso-spring-tests modules.
public class HSqlDBServer {
  private static final String DEFAULT_DB_NAME = "testdb";
  private static final int    DEFAULT_PORT    = 9001;

  private Server              server;

  public HSqlDBServer() {
    super();
  }

  public void start() throws Exception {
    HsqlProperties hsqlproperties1 = new HsqlProperties();
    HsqlProperties hsqlproperties2 = HsqlProperties.argArrayToProps(new String[] { "-database.0", "mem:testdb",
        "-dbname.0", DEFAULT_DB_NAME, "server.port", "" + DEFAULT_PORT }, "server");
    hsqlproperties1.addProperties(hsqlproperties2);
    ServerConfiguration.translateDefaultDatabaseProperty(hsqlproperties1);
    server = new Server();
    server.setProperties(hsqlproperties1);
    server.start();
  }

  public void stop() throws Exception {
    server.setNoSystemExit(true);
    server.stop();
  }

  public static void main(String[] args) {
    try {
      HSqlDBServer dbServer = new HSqlDBServer();
      dbServer.start();
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }
}
