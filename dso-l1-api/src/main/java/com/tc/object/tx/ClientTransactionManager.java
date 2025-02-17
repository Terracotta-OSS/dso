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
package com.tc.object.tx;

import com.tc.abortable.AbortedOperationException;
import com.tc.net.NodeID;
import com.tc.object.ClearableCallback;
import com.tc.object.LogicalOperation;
import com.tc.object.ObjectID;
import com.tc.object.TCObject;
import com.tc.object.dna.api.LogicalChangeID;
import com.tc.object.dna.api.LogicalChangeResult;
import com.tc.object.locks.LockID;
import com.tc.object.locks.LockLevel;
import com.tc.object.locks.Notify;
import com.tc.object.metadata.MetaDataDescriptorInternal;
import com.tc.object.session.SessionID;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ThreadLocal based transaction manager interface. Changes go through here to the transaction for the current thread.
 */
public interface ClientTransactionManager extends ClearableCallback {

  /**
   * Begin a thread local transaction
   * 
   * @param lock Lock name
   * @param lockLevel Lock level
   * @param atomic whether to commit the atomic transaction
   */
  public void begin(LockID lock, LockLevel lockLevel, boolean atomic);

  /**
   * Commit a thread local current transaction
   * 
   * @param lock lock id
   * @param lockLevel level
   * @param atomic whether to commit the atomic transaction
   * @param callable to call after the current transaction is committed
   * @throws UnlockedSharedObjectException If a shared object is being accessed from outside a shared transaction
   * @throws AbortedOperationException
   */
  public void commit(LockID lock, LockLevel lockLevel, boolean atomic, OnCommitCallable callable)
      throws UnlockedSharedObjectException,
      AbortedOperationException;

  /**
   * When transactions come in from the L2 we use this method to apply them. We will have to get a bit fancier because
   * we can't apply any changes while we are in any transaction. The first version will not allow apply to happen while
   * ANY txn is in progress. This is probably not acceptable. We will probably figure something out with the lock
   * manager where we can acquire a read lock if a field is accessed in a transaction
   * 
   * @param txType Transaction type
   * @param lockIDs Locks involved in the transaction
   * @param objectChanges Collection of DNA indicating changes
   * @param newRoots Map of new roots, Root name -&gt; ObjectID
   */
  public void apply(TxnType txType, List<LockID> lockIDs, Collection objectChanges, Map newRoots);

  /**
   * Add new managed object to current transaction
   * 
   * @param source TCObject
   */
  public void createObject(TCObject source);

  /**
   * Add new root to current transaction
   * 
   * @param name Root name
   * @param id Object identifier
   */
  public void createRoot(String name, ObjectID id);

  /**
   * Record a logical method invocation
   * 
   * @param source TCObject for object
   * @param method Method constant from SerializationUtil
   * @param parameters Parameter values in call
   */
  public void logicalInvoke(TCObject source, LogicalOperation method, Object[] parameters);

  /**
   * Record notify() or notifyAll() call on object in current transaction
   * 
   * @param notify Notify object generated by locking system
   * @throws UnlockedSharedObjectException If shared object accessed outside lock
   */
  public void notify(Notify notify) throws UnlockedSharedObjectException;

  /**
   * Record acknowledgment
   * 
   * @param sessionID Session identifier
   * @param requestID Transaction identifier
   * @param nodeID
   */
  public void receivedAcknowledgement(SessionID sessionID, TransactionID requestID, NodeID nodeID);

  /**
   * Record batch acknowledgment
   * 
   * @param batchID Transaction batch identifier
   * @param nodeID
   */
  public void receivedBatchAcknowledgement(TxnBatchID batchID, NodeID nodeID);

  /**
   * Enable transaction logging
   */
  public void enableTransactionLogging();

  /**
   * Disable transaction logging
   */
  public void disableTransactionLogging();

  /**
   * Check whether transaction logging is disabled
   * 
   * @return True if disabled, false if enabled
   */
  public boolean isTransactionLoggingDisabled();

  /**
   * Add meta data descriptor to current transaction
   * 
   * @param md Descriptor
   */
  public void addMetaDataDescriptor(TCObject tco, MetaDataDescriptorInternal md);

  /**
   * @return the current open transaction for the calling thread, null if no open transaction
   */
  public ClientTransaction getCurrentTransaction();

  /**
   * Used by BulkLoad to wait for all current transactions completed
   * 
   * @throws AbortedOperationException
   */
  public void waitForAllCurrentTransactionsToComplete() throws AbortedOperationException;
  
  public void receivedLogicalChangeResult(Map<LogicalChangeID, LogicalChangeResult> results);

  public boolean logicalInvokeWithResult(TCObject source, LogicalOperation method, Object[] parameters)
      throws AbortedOperationException;

}
