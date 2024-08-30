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
package com.tc.l2.objectserver;

import com.tc.l2.context.ManagedObjectSyncContext;
import com.tc.net.NodeID;

import java.util.Collection;
import java.util.Set;

public interface L2ObjectStateManager {

  /**
   * @return the number of L2s present in the cluster for which the object state is tracked. Note that the object state
   *         is not tracked for the local node.
   */
  public int getL2Count();

  public void removeL2(NodeID nodeID);

  public boolean addL2(NodeID nodeID);

  public ManagedObjectSyncContext getSomeObjectsToSyncContext(NodeID nodeID, int count);

  public void close(ManagedObjectSyncContext mosc);

  public Collection getL2ObjectStates();

  public void registerForL2ObjectStateChangeEvents(L2ObjectStateListener listener);

  public void initiateSync(NodeID nodeID, Runnable syncRunnable);

  public void syncMore(NodeID nodeID);

  public void ackSync(NodeID nodeID);

}