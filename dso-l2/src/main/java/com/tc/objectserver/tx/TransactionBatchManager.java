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
package com.tc.objectserver.tx;

import com.tc.l2.ha.TransactionBatchListener;
import com.tc.net.NodeID;
import com.tc.object.msg.CommitTransactionMessage;
import com.tc.object.tx.TransactionID;

public interface TransactionBatchManager {

  public void addTransactionBatch(CommitTransactionMessage ctm);

  public void defineBatch(NodeID node, int numTxns) throws BatchDefinedException;

  public boolean batchComponentComplete(NodeID committerID, TransactionID txnID) throws NoSuchBatchException;

  public void nodeConnected(NodeID nodeID);

  public void shutdownNode(NodeID nodeID);

  public void processTransactions(TransactionBatchContext batchContext);

  public void notifyServerHighWaterMark(NodeID nodeID, long serverHighWaterMark);
  
  public void registerForBatchTransaction(TransactionBatchListener listener);

}