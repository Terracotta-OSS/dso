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
package com.tcclient.cluster;

import com.tc.exception.TCRuntimeException;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class DsoNodeImpl implements DsoNodeInternal, Comparable {

  private final String             id;
  private final long               channelId;
  private final boolean            isLocalNode;

  private volatile DsoNodeMetaData metaData;
  private final DsoClusterInternal dsoCluster;

  public DsoNodeImpl(final String id, final long channelId, DsoClusterInternal dsoCluster) {
    this(id, channelId, false, dsoCluster);
  }

  public DsoNodeImpl(final String id, final long channelId, boolean isLocalNode, DsoClusterInternal dsoCluster) {
    this.id = id;
    this.channelId = channelId;
    this.isLocalNode = isLocalNode;
    this.dsoCluster = dsoCluster;
  }

  @Override
  public String getId() {
    return id;
  }

  @Override
  public long getChannelId() {
    return channelId;
  }

  @Override
  public String getIp() {
    return getOrRetrieveMetaData().getIp();
  }

  @Override
  public String getHostname() {
    return getOrRetrieveMetaData().getHostname();
  }

  private boolean isLocalNode() {
    return this.isLocalNode;
  }

  private DsoNodeMetaData resolveLocalIPAndHostname() {
    InetAddress addr;
    try {
      addr = InetAddress.getLocalHost();
    } catch (UnknownHostException e) {
      throw new TCRuntimeException(e);
    }
    return new DsoNodeMetaData(addr.getHostAddress(), addr.getHostName());
  }

  private DsoNodeMetaData getOrRetrieveMetaData() {
    if (metaData == null) {
      if (isLocalNode()) {
        metaData = resolveLocalIPAndHostname();
      } else {
        metaData = getOrRetrieveMetaData(dsoCluster);
      }
    }
    return metaData;
  }

  @Override
  public DsoNodeMetaData getOrRetrieveMetaData(DsoClusterInternal cluster) {
    if (metaData != null) { return metaData; }
    return cluster.retrieveMetaDataForDsoNode(this);
  }

  @Override
  public void setMetaData(final DsoNodeMetaData metaData) {
    this.metaData = metaData;
  }

  @Override
  public String toString() {
    return id.toString();
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }

  @Override
  public boolean equals(final Object obj) {
    if (this == obj) { return true; }
    if (null == obj) { return false; }
    if (getClass() != obj.getClass()) { return false; }
    DsoNodeImpl other = (DsoNodeImpl) obj;
    if (null == id) {
      return null == other.id;
    } else {
      return id.equals(other.id);
    }
  }

  @Override
  public int compareTo(final Object o) {
    return id.compareTo(((DsoNodeImpl) o).id);
  }
}
