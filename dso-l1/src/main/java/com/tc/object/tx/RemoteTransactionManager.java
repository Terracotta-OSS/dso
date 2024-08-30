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
package com.tc.object.tx;

import com.tc.abortable.AbortedOperationException;
import com.tc.net.NodeID;
import com.tc.object.handshakemanager.ClientHandshakeCallback;
import com.tc.object.locks.LockFlushCallback;
import com.tc.object.locks.LockID;
import com.tc.object.session.SessionID;
import com.tc.text.PrettyPrintable;

import java.util.Set;

/**
 * Client representation of the server transaction manager
 */
public interface RemoteTransactionManager extends ClientHandshakeCallback, PrettyPrintable {

  public void stop();

  /**
   * Blocks until all of the transactions within the given lock has been fully ACKed.
   */
  public void flush(LockID lockID) throws AbortedOperationException;

  public boolean asyncFlush(LockID lockID, LockFlushCallback callback);

  public void commit(ClientTransaction transaction) throws AbortedOperationException;

  public TransactionBuffer receivedAcknowledgement(SessionID sessionID, TransactionID txID, NodeID nodeID);

  public void receivedBatchAcknowledgement(TxnBatchID batchID, NodeID nodeID);
  
  public void throttleProcessing(boolean processing);

  public void waitForAllCurrentTransactionsToComplete() throws AbortedOperationException;

  public void waitForServerToReceiveTxnsForThisLock(LockID lock) throws AbortedOperationException;

  public void batchReceived(TxnBatchID batchId, Set<TransactionID> set, NodeID nid);

  /**
   * This will mark state as REJOIN_IN_PROGRESS and throw threads out which are waiting in TransactionSequencer and
   * LockAccounting
   */
  public void preCleanup();

  public void requestImmediateShutdown();

}