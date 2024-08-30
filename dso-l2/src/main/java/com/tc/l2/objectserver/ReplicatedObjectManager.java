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

import com.tc.l2.msg.GCResultMessage;
import com.tc.net.NodeID;
import com.tc.net.groups.GroupException;
import com.tc.objectserver.tx.TransactionBatchContext;

public interface ReplicatedObjectManager {

  /**
   * This method is used to sync up all ObjectIDs from the remote ObjectManagers. It is synchronous and after when it
   * returns nobody is allowed to join the cluster with exisiting objects.
   */
  public void sync();

  public void relayTransactions(final TransactionBatchContext transactionBatchContext);

  public void query(NodeID nodeID) throws GroupException;
  
  public void clear(NodeID nodeID);

  public void handleGCResult(GCResultMessage message);

}