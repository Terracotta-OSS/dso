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
package com.tc.net.groups;


public class Node {

  private final String host;
  private final int    port;
  private final int    groupPort;
  private final int    hashCode;

  public Node(final String host, final int port) {
    this(host, port, 0);
  }

  public Node(final String host, final int port, final int groupPort) {
    checkArgs(host, port);
    this.host = host.trim();
    this.port = port;
    this.groupPort = groupPort;
    this.hashCode = (host + "-" + port).hashCode();
  }

  public String getHost() {
    return host;
  }

  public int getPort() {
    return port;
  }

  public int getGroupPort() {
    return groupPort;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Node) {
      Node that = (Node) obj;
      return this.port == that.port && this.host.equals(that.host);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  private static void checkArgs(final String host, final int port) throws IllegalArgumentException {
    if (host == null || host.trim().length() == 0) { throw new IllegalArgumentException("Invalid host name: " + host); }
    if (port < 0) { throw new IllegalArgumentException("Invalid port number: " + port); }
  }

  @Override
  public String toString() {
    return "Node{host=" + host + ",port=" + port + "}";
  }

  public String getServerNodeName() {
    String prefix;
    prefix = host.contains(":") ? ("[" + host + "]") : host;
    return (prefix + ":" + port);
  }

}
