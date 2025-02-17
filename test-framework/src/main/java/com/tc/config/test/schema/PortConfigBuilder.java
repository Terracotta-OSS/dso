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
package com.tc.config.test.schema;

public class PortConfigBuilder extends BaseConfigBuilder {
  public static enum PortType {
    JMXPORT, TSAPORT, GROUPPORT, MANAGEMENTPORT;
  }

  private static final String   BIND            = "bind";
  private static final String   TSA_PORT        = "tsa-port";
  private static final String   JMX_PORT        = "jmx-port";
  private static final String   GROUP_PORT      = "tsa-group-port";
  private static final String   MANAGEMENT_PORT = "management-port";

  private static final String[] ALL_PROPERTIES  = concat(new Object[] { BIND, TSA_PORT, JMX_PORT, GROUP_PORT,
      MANAGEMENT_PORT                          });

  private String                bindAddress;
  private int                   bindPort;
  private String                portType;

  public PortConfigBuilder(PortType portType) {
    super(7, ALL_PROPERTIES);
    setPort(portType);
  }

  PortConfigBuilder(int indent, PortType portType) {
    super(indent, ALL_PROPERTIES);
    setPort(portType);
  }

  private void setPort(PortType portType) {
    switch (portType) {
      case JMXPORT:
        this.portType = JMX_PORT;
        break;
      case TSAPORT:
        this.portType = TSA_PORT;
        break;
      case GROUPPORT:
        this.portType = GROUP_PORT;
        break;
      case MANAGEMENTPORT:
        this.portType = MANAGEMENT_PORT;
        break;
      default:
        throw new RuntimeException("invalid port type " + portType);
    }
  }

  public void setBindAddress(String bindAddress) {
    this.bindAddress = bindAddress;
  }

  public void setBindPort(int bindPort) {
    setProperty(this.portType, bindPort);
    this.bindPort = bindPort;
  }

  @Override
  public String toString() {
    String out = "";
    out += indent() + "<" + this.portType;

    if (this.bindAddress != null) {
      out += " bind=" + "\"" + this.bindAddress + "\"";
    }

    out += ">" + this.bindPort + "</" + this.portType + ">";

    return out;
  }
}
