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
package com.tc.object.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.net.NodeID;
import com.tc.object.msg.SyncWriteTransactionReceivedMessage;
import com.tc.object.tx.RemoteTransactionManager;
import com.tc.object.tx.TxnBatchID;

public class ReceiveSyncWriteTransactionAckHandler extends AbstractEventHandler {
  private final RemoteTransactionManager remoteTransactionManager;

  public ReceiveSyncWriteTransactionAckHandler(RemoteTransactionManager remoteTransactionManager) {
    this.remoteTransactionManager = remoteTransactionManager;
  }

  @Override
  public void handleEvent(EventContext context) {
    SyncWriteTransactionReceivedMessage msg = (SyncWriteTransactionReceivedMessage) context;
    TxnBatchID batchID = new TxnBatchID(msg.getBatchID());
    NodeID nid = msg.getSourceNodeID();
    remoteTransactionManager.batchReceived(batchID, msg.getSyncTxnSet(), nid);
  }
}
