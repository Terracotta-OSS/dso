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
package com.tc.cluster;

import com.tcclient.cluster.DsoNode;

import java.util.Collection;

/**
 * Provides access to the topology of the cluster, viewed from the current node.
 * <p>
 * This only takes terracotta client nodes into account, TSA server nodes are not included in this topology view.
 * 
 * @since 3.0.0
 */
public interface DsoClusterTopology {
  /**
   * Returns a collection that contains a snapshot of the nodes that are part of the cluster at the time of the method
   * call.
   *
   * @return the snapshot of the nodes in the cluster
   */
  public Collection<DsoNode> getNodes();
}