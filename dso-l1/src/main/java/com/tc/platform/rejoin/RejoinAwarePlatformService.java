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
package com.tc.platform.rejoin;

import org.terracotta.toolkit.rejoin.RejoinException;

import com.tc.abortable.AbortableOperationManager;
import com.tc.abortable.AbortedOperationException;
import com.tc.cluster.DsoCluster;
import com.tc.exception.PlatformRejoinException;
import com.tc.logging.TCLogger;
import com.tc.management.TCManagementEvent;
import com.tc.net.GroupID;
import com.tc.object.LogicalOperation;
import com.tc.object.ObjectID;
import com.tc.object.ServerEventDestination;
import com.tc.object.TCObject;
import com.tc.object.locks.LockID;
import com.tc.object.locks.LockLevel;
import com.tc.object.metadata.MetaDataDescriptor;
import com.tc.object.tx.TransactionCompleteListener;
import com.tc.operatorevent.TerracottaOperatorEvent.EventLevel;
import com.tc.operatorevent.TerracottaOperatorEvent.EventSubsystem;
import com.tc.operatorevent.TerracottaOperatorEvent.EventType;
import com.tc.platform.PlatformService;
import com.tc.properties.TCProperties;
import com.tc.search.SearchQueryResults;
import com.tc.search.SearchRequestID;
import com.tc.server.ServerEventType;
import com.tc.util.VicariousThreadLocal;
import com.tc.util.concurrent.TaskRunner;
import com.tcclient.cluster.DsoNode;
import com.terracottatech.search.NVPair;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class RejoinAwarePlatformService implements PlatformService {
  // private static final TCLogger LOGGER = TCLogging.getLogger(RejoinAwarePlatformService.class);
  private final PlatformService             delegate;
  private final RejoinStateListener         rejoinState;
  private static final ThreadLocal<Integer> currentRejoinCount = new VicariousThreadLocal<Integer>() {
                                                                 @Override
                                                                 protected Integer initialValue() {
                                                                   return new Integer(0);
                                                                 }
                                                               };

  public RejoinAwarePlatformService(PlatformService delegate) {
    this.delegate = delegate;
    this.rejoinState = new RejoinStateListener();
    delegate.addRejoinLifecycleListener(rejoinState);
  }

  @Override
  public TCObject lookupExistingOrNull(Object obj) {
    return delegate.lookupExistingOrNull(obj);
  }

  @Override
  public LockID generateLockIdentifier(Object obj) {
    return delegate.generateLockIdentifier(obj);
  }

  @Override
  public long getLockAwardIDFor(LockID lock) {
    return delegate.getLockAwardIDFor(lock);
  }

  @Override
  public boolean isLockAwardValid(LockID lock, long awardID) {
    return delegate.isLockAwardValid(lock, awardID);
  }

  @Override
  public void pinLock(LockID lock, long awardID) {
    delegate.pinLock(lock, awardID);
  }

  @Override
  public void unpinLock(LockID lock, long awardID) {
    delegate.unpinLock(lock, awardID);
  }

  @Override
  public boolean isExplicitlyLocked() {
    return delegate.isExplicitlyLocked();
  }

  @Override
  public boolean isExplicitlyLocked(Object lockID, LockLevel level) {
    return delegate.isExplicitlyLocked(lockID, level);
  }

  @Override
  public void beginAtomicTransaction(LockID lock, LockLevel level) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      delegate.beginAtomicTransaction(lock, level);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void commitAtomicTransaction(LockID lock, LockLevel level) throws AbortedOperationException {
    try {
      delegate.commitAtomicTransaction(lock, level);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  private void assertRejoinNotInProgress() {
    rejoinState.assertRejoinNotInProgress();
  }

  @Override
  public boolean isLockedBeforeRejoin() {
    // already taken a lock && rejoin count has changed
    return isExplicitlyLocked() && (currentRejoinCount.get().longValue() != getRejoinCount());
  }

  @Override
  public boolean isLockedBeforeRejoin(Object lockID, LockLevel level) {
    // already taken a lock && rejoin count has changed
    return isExplicitlyLocked(lockID, level) && (currentRejoinCount.get().longValue() != getRejoinCount());
  }

  private void resetRejoinCountIfNecessary() {
    if (!isExplicitlyLocked()) {
      currentRejoinCount.set(getRejoinCount());
    }
  }

  private void assertNotLockedBeforeRejoin() {
    if (isLockedBeforeRejoin()) { throw new RejoinException("Lock is not valid after rejoin"); }
  }

  @Override
  public void registerServerEventListener(final ServerEventDestination destination, final Set<ServerEventType> listenTo) {
    delegate.registerServerEventListener(destination, listenTo);
  }

  @Override
  public void registerServerEventListener(final ServerEventDestination destination, final ServerEventType... listenTo) {
    delegate.registerServerEventListener(destination, listenTo);
  }

  @Override
  public void unregisterServerEventListener(final ServerEventDestination destination,
                                            final Set<ServerEventType> listenTo) {
    delegate.unregisterServerEventListener(destination, listenTo);
  }

  @Override
  public void unregisterServerEventListener(final ServerEventDestination destination, final ServerEventType... listenTo) {
    delegate.unregisterServerEventListener(destination, listenTo);
  }

  @Override
  public <T> T lookupRegisteredObjectByName(String name, Class<T> expectedType) {
    try {
      assertNotLockedBeforeRejoin();
      return delegate.lookupRegisteredObjectByName(name, expectedType);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public <T> T registerObjectByNameIfAbsent(String name, T object) {
    try {
      return delegate.registerObjectByNameIfAbsent(name, object);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void logicalInvoke(Object object, LogicalOperation method, Object[] params) {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      delegate.logicalInvoke(object, method, params);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void waitForAllCurrentTransactionsToComplete() throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      delegate.waitForAllCurrentTransactionsToComplete();
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public boolean isHeldByCurrentThread(Object lockID, LockLevel level) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      return delegate.isHeldByCurrentThread(lockID, level);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void beginLock(Object lockID, LockLevel level) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      resetRejoinCountIfNecessary();
      delegate.beginLock(lockID, level);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void beginLockInterruptibly(Object lockID, LockLevel level) throws InterruptedException,
      AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      resetRejoinCountIfNecessary();
      delegate.beginLockInterruptibly(lockID, level);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public boolean tryBeginLock(Object lockID, LockLevel level) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      resetRejoinCountIfNecessary();
      return delegate.tryBeginLock(lockID, level);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public boolean tryBeginLock(Object lockID, LockLevel level, long timeout, TimeUnit timeUnit)
      throws InterruptedException, AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      resetRejoinCountIfNecessary();
      return delegate.tryBeginLock(lockID, level, timeout, timeUnit);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void commitLock(Object lockID, LockLevel level) throws AbortedOperationException {
    // do not assert and throw rejoin exception when rejoin is in progress
    // copy current isLockedBeforeRejoin state because that will change after delegate.commitLock()
    boolean isLockedBeforeRejoin = isLockedBeforeRejoin(lockID, level);
    try {
      delegate.commitLock(lockID, level);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    } catch (IllegalMonitorStateException e) {
      // if we get IllegalMonitorStateException then we should convert it to RejoinException if locked before rejoin
      if (isLockedBeforeRejoin) { throw new RejoinException(e); }
      throw e;
    }
  }

  @Override
  public void lockIDWait(Object lockID, long timeout, TimeUnit timeUnit) throws InterruptedException,
      AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      delegate.lockIDWait(lockID, timeout, timeUnit);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void lockIDNotify(Object lockID) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      delegate.lockIDNotify(lockID);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void lockIDNotifyAll(Object lockID) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      delegate.lockIDNotifyAll(lockID);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public TCProperties getTCProperties() {
    assertRejoinNotInProgress();
    try {
      return delegate.getTCProperties();
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public Object lookupRoot(String name, GroupID gid) {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      return delegate.lookupRoot(name, gid);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public Object lookupOrCreateRoot(String name, Object object, GroupID gid) {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      return delegate.lookupOrCreateRoot(name, object, gid);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public TCObject lookupOrCreate(Object obj, GroupID gid) {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      return delegate.lookupOrCreate(obj, gid);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public Object lookupObject(ObjectID id) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      return delegate.lookupObject(id);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public GroupID[] getGroupIDs() {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      return delegate.getGroupIDs();
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public TCLogger getLogger(String loggerName) {
    assertRejoinNotInProgress();
    try {
      return delegate.getLogger(loggerName);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void addTransactionCompleteListener(TransactionCompleteListener listener) {
    assertRejoinNotInProgress();
    try {
      delegate.addTransactionCompleteListener(listener);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public MetaDataDescriptor createMetaDataDescriptor(String category) {
    assertRejoinNotInProgress();
    try {
      return delegate.createMetaDataDescriptor(category);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void fireOperatorEvent(EventLevel coreOperatorEventLevel, EventSubsystem coreEventSubsytem,
                                EventType eventType, String eventMessage) {
    assertRejoinNotInProgress();
    try {
      delegate.fireOperatorEvent(coreOperatorEventLevel, coreEventSubsytem, eventType, eventMessage);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public DsoNode getCurrentNode() {
    assertRejoinNotInProgress();
    try {
      return delegate.getCurrentNode();
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public DsoCluster getDsoCluster() {
    assertRejoinNotInProgress();
    try {
      return delegate.getDsoCluster();
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void registerBeforeShutdownHook(Runnable hook) {
    assertRejoinNotInProgress();
    try {
      delegate.registerBeforeShutdownHook(hook);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public String getUUID() {
    assertRejoinNotInProgress();
    try {
      return delegate.getUUID();
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public SearchQueryResults executeQuery(String cachename, List queryStack, boolean includeKeys, boolean includeValues,
                                         Set<String> attributeSet, List<NVPair> sortAttributes,
                                         List<NVPair> aggregators, int maxResults, int batchSize, int pageSize,
                                         boolean waitForTxn, SearchRequestID queryId) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      return delegate.executeQuery(cachename, queryStack, includeKeys, includeValues, attributeSet, sortAttributes,
                                   aggregators, maxResults, batchSize, pageSize, waitForTxn, queryId);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public SearchQueryResults executeQuery(String cachename, List queryStack, Set<String> attributeSet,
                                         Set<String> groupByAttributes, List<NVPair> sortAttributes,
                                         List<NVPair> aggregators, int maxResults, int batchSize, boolean waitForTxn,
                                         SearchRequestID queryId) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      return delegate.executeQuery(cachename, queryStack, attributeSet, groupByAttributes, sortAttributes, aggregators,
                                   maxResults, batchSize, waitForTxn, queryId);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void preFetchObject(ObjectID id) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      delegate.preFetchObject(id);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void verifyCapability(String capability) {
    assertRejoinNotInProgress();
    try {
      delegate.verifyCapability(capability);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public AbortableOperationManager getAbortableOperationManager() {
    assertRejoinNotInProgress();
    try {
      return delegate.getAbortableOperationManager();
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void throttlePutIfNecessary(final ObjectID object) throws AbortedOperationException {
    assertRejoinNotInProgress();
    try {
      assertNotLockedBeforeRejoin();
      delegate.throttlePutIfNecessary(object);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void addRejoinLifecycleListener(RejoinLifecycleListener listener) {
    try {
      delegate.addRejoinLifecycleListener(listener);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public void removeRejoinLifecycleListener(RejoinLifecycleListener listener) {
    try {
      delegate.removeRejoinLifecycleListener(listener);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  private static class RejoinStateListener implements RejoinLifecycleListener {
    private volatile boolean rejoinInProgress = false;

    @Override
    public void onRejoinStart() {
      rejoinInProgress = true;
    }

    @Override
    public void onRejoinComplete() {
      rejoinInProgress = false;
    }

    public void assertRejoinNotInProgress() throws RejoinException {
      if (rejoinInProgress) throw new RejoinException("Rejoin is in progress");
    }

  }

  @Override
  public int getRejoinCount() {
    return delegate.getRejoinCount();
  }

  @Override
  public boolean isRejoinInProgress() {
    return delegate.isRejoinInProgress();
  }

  @Override
  public void unregisterBeforeShutdownHook(Runnable hook) {
    assertRejoinNotInProgress();
    try {
      delegate.unregisterBeforeShutdownHook(hook);
    } catch (PlatformRejoinException e) {
      throw new RejoinException(e);
    }
  }

  @Override
  public TaskRunner getTaskRunner() {
    return delegate.getTaskRunner();
  }

  @Override
  public long getClientId() {
    return delegate.getClientId();
  }

  @Override
  public Object registerManagementService(Object service, ExecutorService executorService) {
    return delegate.registerManagementService(service, executorService);
  }

  @Override
  public void unregisterManagementService(Object serviceID) {
    delegate.unregisterManagementService(serviceID);
  }

  @Override
  public void sendEvent(TCManagementEvent event) {
    delegate.sendEvent(event);
  }
}
