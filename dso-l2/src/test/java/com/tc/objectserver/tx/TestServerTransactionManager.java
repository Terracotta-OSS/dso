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

import com.tc.exception.ImplementMe;
import com.tc.net.NodeID;
import com.tc.object.ObjectID;
import com.tc.object.tx.ServerTransactionID;
import com.tc.object.tx.TransactionID;
import com.tc.objectserver.api.ObjectInstanceMonitor;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.objectserver.managedobject.ApplyTransactionInfo;
import com.tc.util.concurrent.NoExceptionLinkedQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestServerTransactionManager implements ServerTransactionManager {

  public final NoExceptionLinkedQueue skipCalls = new NoExceptionLinkedQueue();

  public TestServerTransactionManager() {
    //
  }

  public final NoExceptionLinkedQueue shutdownClientCalls = new NoExceptionLinkedQueue();
  public final ArrayList              incomingTxnContexts = new ArrayList();
  public final List                   incomingTxns        = new ArrayList();

  @Override
  public void shutdownNode(NodeID deadClient) {
    this.shutdownClientCalls.put(deadClient);
  }

  @Override
  public void addWaitingForAcknowledgement(NodeID waiter, TransactionID requestID, NodeID waitee) {
    throw new ImplementMe();

  }

  @Override
  public boolean isWaiting(NodeID waiter, TransactionID requestID) {
    throw new ImplementMe();
  }

  @Override
  public void acknowledgement(NodeID waiter, TransactionID requestID, NodeID waitee) {
    throw new ImplementMe();
  }

  @Override
  public void broadcasted(NodeID waiter, TransactionID requestID) {
    // NOP
  }

  @Override
  public void skipApplyAndCommit(ServerTransaction txn) {
    this.skipCalls.put(txn);
  }

  @Override
  public void addTransactionListener(ServerTransactionListener listener) {
    // NOP
  }

  @Override
  public void removeTransactionListener(ServerTransactionListener listener) {
    // NOP
  }

  @Override
  public void apply(ServerTransaction txn, Map objects, ApplyTransactionInfo includeIDs,
                    ObjectInstanceMonitor instanceMonitor) {
    // NOP
  }

  @Override
  public void incomingTransactions(NodeID nodeID, Map<ServerTransactionID, ServerTransaction> txns) {
    this.incomingTxnContexts.add(new Object[] { nodeID, txns });
    this.incomingTxns.addAll(txns.values());
  }

  @Override
  public void transactionsRelayed(NodeID node, Set serverTxnIDs) {
    throw new ImplementMe();
  }

  @Override
  public void cleanup(Set<ObjectID> deletedObjects) {
    throw new ImplementMe();
  }

  @Override
  public void commit(Collection<ManagedObject> objects,
                     Map<String, ObjectID> newRoots, Collection<ServerTransactionID> appliedServerTransactionIDs) {
    // NOP
  }

  @Override
  public void setResentTransactionIDs(NodeID source, Collection transactionIDs) {
    // NOP
  }

  @Override
  public void start(Set cids) {
    // NOP
  }

  @Override
  public void goToActiveMode() {
    throw new ImplementMe();
  }

  @Override
  public void callBackOnTxnsInSystemCompletion(TxnsInSystemCompletionListener l) {
    throw new ImplementMe();
  }

  @Override
  public void nodeConnected(NodeID nodeID) {
    throw new ImplementMe();
  }

  @Override
  public int getTotalPendingTransactionsCount() {
    throw new ImplementMe();
  }

  @Override
  public void objectsSynched(NodeID node, ServerTransactionID tid) {
    throw new ImplementMe();
  }

  @Override
  public void callBackOnResentTxnsInSystemCompletion(TxnsInSystemCompletionListener l) {
    throw new ImplementMe();
  }

  @Override
  public long getTotalNumOfActiveTransactions() {
    throw new ImplementMe();
  }

  @Override
  public void processingMetaDataCompleted(NodeID sourceID, TransactionID txnID) {
    throw new ImplementMe();
  }

  @Override
  public void processMetaData(ServerTransaction txn, ApplyTransactionInfo applyInfo) {
    //
  }

  @Override
  public void callbackOnLowWaterMarkInSystemCompletion(Runnable r) {
    //
  }

  @Override
  public void pauseTransactions() {
    //
  }

  @Override
  public void unPauseTransactions() {
    //
  }

  @Override
  public void loadApplyChangeResults(ServerTransaction txn, ApplyTransactionInfo applyInfo) {
    //
  }

  @Override
  public void waitForTransactionRelay(final ServerTransactionID serverTransactionID) {

  }

  @Override
  public void waitForTransactionCommit(final ServerTransactionID serverTransactionID) {

  }
}
