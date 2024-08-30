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
package com.tc.management.remote.protocol.terracotta;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.generic.GenericConnectorServer;

public class ServerProvider implements JMXConnectorServerProvider {

  public JMXConnectorServer newJMXConnectorServer(final JMXServiceURL jmxServiceURL, final Map initialEnvironment,
                                                  final MBeanServer mBeanServer) throws IOException {
    if (!jmxServiceURL.getProtocol().equals("terracotta")) {
      MalformedURLException exception = new MalformedURLException("Protocol not terracotta: "
                                                                  + jmxServiceURL.getProtocol());
      throw exception;
    }
    Map terracottaEnvironment = initialEnvironment != null ? new HashMap(initialEnvironment) : new HashMap();
    terracottaEnvironment.put(GenericConnectorServer.MESSAGE_CONNECTION_SERVER,
                              new TunnelingMessageConnectionServer(jmxServiceURL));
    return new GenericConnectorServer(terracottaEnvironment, mBeanServer);
  }

}
