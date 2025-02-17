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
package com.tc.cluster;

import com.tc.net.ClientID;
import com.tc.net.NodeID;
import com.tcclient.cluster.DsoClusterInternal;
import com.tcclient.cluster.DsoNode;
import com.tcclient.cluster.DsoNodeImpl;
import com.tcclient.cluster.DsoNodeInternal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class DsoClusterTopologyImpl implements DsoClusterTopology {
  private final ConcurrentMap<NodeID, DsoNodeInternal> nodes          = new ConcurrentHashMap<NodeID, DsoNodeInternal>();

  private final ReentrantReadWriteLock                 nodesLock      = new ReentrantReadWriteLock();
  private final ReentrantReadWriteLock.ReadLock        nodesReadLock  = nodesLock.readLock();
  private final ReentrantReadWriteLock.WriteLock       nodesWriteLock = nodesLock.writeLock();

  private final DsoClusterInternal                     dsoCluster;

  public DsoClusterTopologyImpl(DsoClusterInternal dsoCluster) {
    this.dsoCluster = dsoCluster;
  }

  Collection<DsoNodeInternal> getInternalNodes() {
    nodesReadLock.lock();
    try {
      return Collections.unmodifiableCollection(new ArrayList<DsoNodeInternal>(nodes.values()));
    } finally {
      nodesReadLock.unlock();
    }
  }

  DsoNodeInternal getInternalNode(NodeID nodeId) {
    nodesReadLock.lock();
    try {
      return nodes.get(nodeId);
    } finally {
      nodesReadLock.unlock();
    }
  }

  @Override
  public Collection<DsoNode> getNodes() {
    nodesReadLock.lock();
    try {
      return Collections.unmodifiableCollection(new ArrayList<DsoNode>(nodes.values()));
    } finally {
      nodesReadLock.unlock();
    }
  }

  boolean containsDsoNode(final NodeID nodeId) {
    nodesReadLock.lock();
    try {
      return nodes.containsKey(nodeId);
    } finally {
      nodesReadLock.unlock();
    }
  }

  DsoNodeInternal getAndRegisterDsoNode(final ClientID nodeId) {
    nodesReadLock.lock();
    try {
      DsoNodeInternal node = nodes.get(nodeId);
      if (node != null) { return node; }
    } finally {
      nodesReadLock.unlock();
    }

    return registerDsoNode(nodeId);
  }

  DsoNodeInternal getAndRemoveDsoNode(final NodeID nodeId) {
    nodesWriteLock.lock();
    try {
      DsoNodeInternal node = nodes.remove(nodeId);
      return node;
    } finally {
      nodesWriteLock.unlock();
    }
  }

  DsoNodeInternal registerDsoNode(final ClientID nodeId) {
    return registerDsoNodeBase(nodeId, false);
  }

  DsoNodeInternal registerThisDsoNode(final ClientID nodeId) {
    return registerDsoNodeBase(nodeId, true);
  }

  DsoNodeInternal updateOnRejoin(final ClientID thisNodeId, final NodeID[] clusterMembers) {
    nodesWriteLock.lock();
    try {
      for (NodeID otherNode : clusterMembers) {
        if (!thisNodeId.equals(otherNode)) {
          registerDsoNodeBase((ClientID) otherNode, false);
        }
      }
      return registerDsoNodeBase(thisNodeId, true);
    } finally {
      nodesWriteLock.unlock();
    }
  }

  private DsoNodeInternal registerDsoNodeBase(final ClientID clientId, boolean isLocalNode) {
    final DsoNodeInternal node = new DsoNodeImpl(clientId.toString(), clientId.toLong(), isLocalNode, dsoCluster);

    nodesWriteLock.lock();
    try {
      DsoNodeInternal old = nodes.putIfAbsent(clientId, node);
      if (old != null) {
        return old;
      } else {
        return node;
      }
    } finally {
      nodesWriteLock.unlock();
    }
  }

  void cleanup() {
    nodesWriteLock.lock();
    try {
      nodes.clear();
    } finally {
      nodesWriteLock.unlock();
    }
  }

}