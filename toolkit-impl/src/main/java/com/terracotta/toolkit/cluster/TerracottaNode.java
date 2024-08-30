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
package com.terracotta.toolkit.cluster;

import org.terracotta.toolkit.cluster.ClusterNode;

import com.tcclient.cluster.DsoNode;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TerracottaNode implements ClusterNode {

  private final String id;
  private final String ip;
  private final String hostname;

  public TerracottaNode(DsoNode node) {
    this.id = node.getId();
    this.ip = node.getIp();
    this.hostname = node.getHostname();
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public InetAddress getAddress() {
    try {
      return InetAddress.getByAddress(hostname, InetAddress.getByName(ip).getAddress());
    } catch (UnknownHostException uhe) {
      // This should never occur, since the ip address passed here will always be of legal length.
      throw new RuntimeException(uhe);
    }
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof TerracottaNode) {
      TerracottaNode tcNodeImpl = (TerracottaNode) obj;
      return this.id.equals(tcNodeImpl.id);
    }
    return false;
  }

  @Override
  public String toString() {
    return "TerracottaNode [id=" + id + ", ip=" + ip + ", hostname=" + hostname + "]";
  }
}