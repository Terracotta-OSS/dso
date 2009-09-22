/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.object.tx;

import com.tc.logging.DumpHandler;
import com.tc.net.NodeID;
import com.tc.object.ClientIDProvider;
import com.tc.object.ObjectID;
import com.tc.object.TCObject;
import com.tc.object.dmi.DmiDescriptor;
import com.tc.object.locks.LockID;
import com.tc.object.locks.LockLevel;
import com.tc.object.locks.ThreadID;
import com.tc.object.session.SessionID;
import com.tc.text.PrettyPrintable;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ThreadLocal based transaction manager interface. Changes go through here to the transaction for the current thread.
 */
public interface ClientTransactionManager extends DumpHandler, PrettyPrintable {

  /**
   * Begin a thread local transaction
   * 
   * @param lock Lock name
   * @param lockLevel Lock level
   * @return If begun
   */
  public void begin(LockID lock, LockLevel lockLevel);

  /**
   * Commit a thread local current transaction
   * 
   * @param lockName Lock name
   * @throws UnlockedSharedObjectException If a shared object is being accessed from outside a shared transaction
   */
  public void commit(LockID lock) throws UnlockedSharedObjectException;

  /**
   * When transactions come in from the L2 we use this method to apply them. We will have to get a bit fancier because
   * we can't apply any changes while we are in any transaction. The first version will not allow apply to happen while
   * ANY txn is in progress. This is probably not acceptable. We will probably figure something out with the lock
   * manager where we can acquire a read lock if a field is accessed in a transaction
   * 
   * @param txType Transaction type
   * @param lockIDs Locks involved in the transaction
   * @param objectChanges Collection of DNA indicating changes
   * @param newRoots Map of new roots, Root name -> ObjectID
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
   * Record change in literal value in current transaction
   * 
   * @param source TCObject for literal value
   * @param newValue New value
   * @param oldValue Old value
   */
  public void literalValueChanged(TCObject source, Object newValue, Object oldValue);

  /**
   * Record field change in current transaction
   * 
   * @param source TCObject for field
   * @param classname Class name
   * @param fieldname Field name
   * @param newValue New object
   * @param index Into array if field is an array
   */
  public void fieldChanged(TCObject source, String classname, String fieldname, Object newValue, int index);

  /**
   * Record a logical method invocation
   * 
   * @param source TCObject for object
   * @param method Method constant from SerializationUtil
   * @param methodName Method name
   * @param parameters Parameter values in call
   */
  public void logicalInvoke(TCObject source, int method, String methodName, Object[] parameters);

  /**
   * Record notify() or notifyAll() call on object in current transaction
   * 
   * @param lockName Lock name
   * @param all True if notifyAll()
   * @param object Object notify called on
   * @throws UnlockedSharedObjectException If shared object accessed outside lock
   */
  public void notify(LockID lock, ThreadID thread, boolean all) throws UnlockedSharedObjectException;

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
   * Check whether current transaction has write access
   * 
   * @param context The object context
   * @throws com.tc.object.util.ReadOnlyException If in read-only transaction
   */
  public void checkWriteAccess(Object context);

  /**
   * Add reference to tco in current transaction
   * 
   * @param tco TCObject
   */
  public void addReference(TCObject tco);

  /**
   * Get channel provider for this txn manager
   * 
   * @return Provider
   */
  public ClientIDProvider getClientIDProvider();

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
   * Check whether object creation is in progress
   */
  public boolean isObjectCreationInProgress();
  
  /**
   * Record an array change in the current transaction
   * 
   * @param src The TCObject for the array
   * @param startPos Start index in the array
   * @param array The new partial array or value
   * @param length Partial array length
   */
  public void arrayChanged(TCObject src, int startPos, Object array, int length);

  /**
   * Add distributed method call descriptor to current transaction
   * 
   * @param d Descriptor
   */
  public void addDmiDescriptor(DmiDescriptor d);

  /**
   * Check if lockID is on top of the transaction stack
   * 
   * @param lockName
   */
  public boolean isLockOnTopStack(LockID lock);

  /**
   * @returns the current open transaction for the calling thread, null if no open transaction
   */
  public ClientTransaction getCurrentTransaction();
}
