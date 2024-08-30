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

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalMatchers.and;
import static org.mockito.AdditionalMatchers.not;

import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

import com.tc.async.api.EventContext;
import com.tc.net.ClientID;
import com.tc.object.ObjectID;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.object.tx.ServerTransactionID;
import com.tc.object.tx.TransactionID;
import com.tc.objectserver.context.ApplyTransactionContext;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.objectserver.core.api.ServerConfigurationContext;
import com.tc.objectserver.event.ClientChannelMonitor;
import com.tc.objectserver.event.ServerEventBuffer;
import com.tc.objectserver.gtx.TestGlobalTransactionManager;
import com.tc.objectserver.impl.TestObjectManager;
import com.tc.objectserver.managedobject.ApplyTransactionInfo;
import com.tc.test.TCTestCase;
import com.tc.util.BitSetObjectIDSet;
import com.tc.util.ObjectIDSet;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class TransactionalObjectManagerTest extends TCTestCase {

  private TestObjectManager                      objectManager;
  private TestTransactionalStageCoordinator      coordinator;
  private TransactionalObjectManagerImpl txObjectManager;
  private TestGlobalTransactionManager gtxMgr;
  private ServerEventBuffer                 serverEventBuffer;
  private ClientChannelMonitor              clientChannelMonitor;

  @Override
  public void setUp() {
    this.objectManager = spy(new TestObjectManager());
    this.coordinator = spy(new TestTransactionalStageCoordinator());
    this.gtxMgr = new TestGlobalTransactionManager();
    this.txObjectManager = new TransactionalObjectManagerImpl(this.objectManager, gtxMgr, this.coordinator);
    ServerConfigurationContext scc = mock(ServerConfigurationContext.class);
    when(scc.getTransactionManager()).thenReturn(new TestServerTransactionManager());
    serverEventBuffer = mock(ServerEventBuffer.class);
    clientChannelMonitor = mock(ClientChannelMonitor.class);
  }

  public void testSimpleLookup() throws Exception {
    objectManager.addExistingObjectIDs(asCollectionOfObjectIDs(2L, 3L));
    txObjectManager.addTransactions(asList(createTransaction(1, asList(1L), asList(2L, 3L))));
    verify(coordinator).initiateLookup();

    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator).addToApplyStage(argThat(hasTransactionID(1)));

    ApplyTransactionInfo applyTransactionInfo = applyInfoWithTransactionID(1);
    txObjectManager.applyTransactionComplete(applyTransactionInfo);
    verify(applyTransactionInfo).addObjectsToBeReleased(anyCollection());
  }

  public void testOverlappedLookups() throws Exception {
    objectManager.addExistingObjectIDs(asCollectionOfObjectIDs(1L, 2L, 3L));
    txObjectManager.addTransactions(asList(createTransaction(1, Collections.EMPTY_SET, asList(1L, 2L))));

    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator).addToApplyStage(argThat(hasTransactionID(1)));

    txObjectManager.addTransactions(asList(createTransaction(2, Collections.EMPTY_SET, asList(2L, 3L))));
    verify(coordinator, never()).addToApplyStage(argThat(hasTransactionID(2)));

    ApplyTransactionInfo applyTransactionInfo = applyInfoWithTransactionID(1);
    txObjectManager.applyTransactionComplete(applyTransactionInfo);

    objectManager.releaseAll(applyTransactionInfo.getObjectsToRelease());

    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator).addToApplyStage(argThat(hasTransactionID(2)));
  }

  public void testProcessPendingInOrder() throws Exception {
    objectManager.addExistingObjectIDs(asCollectionOfObjectIDs(1L, 2L));
    txObjectManager.addTransactions(asList(createTransaction(1, Collections.EMPTY_SET, asList(1L))));
    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator).addToApplyStage(argThat(hasTransactionID(1)));

    // Finish the transaction but don't release Object1 yet, this will force later transactions to go pending on object1
    ApplyTransactionInfo applyTransactionInfo1 = applyInfoWithTransactionID(1);
    txObjectManager.applyTransactionComplete(applyTransactionInfo1);

    txObjectManager.addTransactions(asList(createTransaction(2, Collections.EMPTY_SET, asList(1L))));
    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator, never()).addToApplyStage(argThat(hasTransactionID(2)));

    // This transaction goes through because it does not use object1
    txObjectManager.addTransactions(asList(createTransaction(3, Collections.EMPTY_SET, asList(2L))));
    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator).addToApplyStage(argThat(hasTransactionID(3)));

    txObjectManager.addTransactions(asList(createTransaction(4, Collections.EMPTY_SET, asList(1L)),
        createTransaction(5, Collections.EMPTY_SET, asList(1L))));
    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator, never()).addToApplyStage(argThat(hasTransactionID(4)));
    verify(coordinator, never()).addToApplyStage(argThat(hasTransactionID(5)));

    // Release the object and verify that all unblocked transactions are run in order.
    objectManager.releaseAll(applyTransactionInfo1.getObjectsToRelease());

    InOrder inOrder = inOrder(coordinator);
    txObjectManager.lookupObjectsForTransactions();
    inOrder.verify(coordinator).addToApplyStage(argThat(hasTransactionID(2)));
    inOrder.verify(coordinator).addToApplyStage(argThat(hasTransactionID(4)));
    inOrder.verify(coordinator).addToApplyStage(argThat(hasTransactionID(5)));

    ApplyTransactionInfo applyTransactionInfo2 = applyInfoWithTransactionID(3);
    txObjectManager.applyTransactionComplete(applyTransactionInfo2);
    objectManager.releaseAll(applyTransactionInfo2.getObjectsToRelease());

    txObjectManager.applyTransactionComplete(applyInfoWithTransactionID(2));
    txObjectManager.applyTransactionComplete(applyInfoWithTransactionID(4));

    ApplyTransactionInfo applyTransactionInfo5 = applyInfoWithTransactionID(5);
    txObjectManager.applyTransactionComplete(applyTransactionInfo5);
    Collection<ManagedObject> mos = applyTransactionInfo5.getObjectsToRelease();
    assertTrue(mos.stream().anyMatch(mo -> mo.getID().equals(new ObjectID(1))));
  }

  public void testAlreadyCommittedTransaction() throws Exception {
    objectManager.addExistingObjectIDs(asCollectionOfObjectIDs(1L));
    gtxMgr.commit(new ServerTransactionID(new ClientID(0), new TransactionID(1)));
    txObjectManager.addTransactions(asList(createTransaction(1, Collections.EMPTY_SET, asList(1L))));
    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator).addToApplyStage((EventContext) and(argThat(hasTransactionID(1)), not(argThat(needsApply()))));

    ApplyTransactionInfo applyTransactionInfo = applyInfoWithTransactionID(1);
    txObjectManager.applyTransactionComplete(applyTransactionInfo);
    Collection<ManagedObject> mos = applyTransactionInfo.getObjectsToRelease();
    assertTrue(mos.stream().anyMatch(mo -> mo.getID().equals(new ObjectID(1))));
    objectManager.releaseAll(applyTransactionInfo.getObjectsToRelease());
  }

  public void testCheckoutBatching() throws Exception {
    objectManager.addExistingObjectIDs(asCollectionOfObjectIDs(1L, 3L));
    txObjectManager.addTransactions(asList(createTransaction(1, Collections.EMPTY_SET, asList(1L))));
    txObjectManager.addTransactions(asList(createTransaction(2, asList(2L), asList(1L))));
    txObjectManager.addTransactions(asList(createTransaction(3, Collections.EMPTY_SET, asList(1L, 3L))));
    txObjectManager.lookupObjectsForTransactions();

    InOrder inOrder = inOrder(coordinator);
    inOrder.verify(coordinator).addToApplyStage(argThat(hasTransactionID(1)));
    inOrder.verify(coordinator).addToApplyStage(argThat(hasTransactionID(2)));
    inOrder.verify(coordinator, never()).addToApplyStage(argThat(hasTransactionID(3)));

    ApplyTransactionInfo applyTransactionInfo1 = applyInfoWithTransactionID(1);
    txObjectManager.applyTransactionComplete(applyTransactionInfo1);
    verify(applyTransactionInfo1, never()).addObjectsToBeReleased(anyCollection());

    ApplyTransactionInfo applyTransactionInfo2 = applyInfoWithTransactionID(2);
    txObjectManager.applyTransactionComplete(applyTransactionInfo2);
    verify(applyTransactionInfo2).addObjectsToBeReleased((Collection<ManagedObject>) argThat(containsObjectWithID(new ObjectID(1))));
    objectManager.releaseAll(applyTransactionInfo2.getObjectsToRelease());

    txObjectManager.lookupObjectsForTransactions();

    verify(coordinator).addToApplyStage(argThat(hasTransactionID(3)));
  }

  public void testBlockedMergeCheckout() throws Exception {
    objectManager.addExistingObjectIDs(asCollectionOfObjectIDs(1L, 2L));
    txObjectManager.addTransactions(asList(createTransaction(1, Collections.EMPTY_SET, asList(1L))));
    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator).addToApplyStage(argThat(hasTransactionID(1)));

    ManagedObject o = objectManager.getObjectByID(new ObjectID(2L));
    txObjectManager.addTransactions(asList(createTransaction(2, asList(2L), asList(1L, 2L))));
    txObjectManager.lookupObjectsForTransactions();

    verify(coordinator, never()).addToApplyStage(argThat(hasTransactionID(2)));

    ApplyTransactionInfo applyTransactionInfo = applyInfoWithTransactionID(1);
    txObjectManager.applyTransactionComplete(applyTransactionInfo);
    Collection<ManagedObject> mos = applyTransactionInfo.getObjectsToRelease();
    assertTrue(mos.stream().anyMatch(mo -> mo.getID().equals(new ObjectID(1))));
    objectManager.releaseAll(applyTransactionInfo.getObjectsToRelease());

    // Another transaction on the newly released object can't go through because another earlier transaction
    // is waiting on that object.
    txObjectManager.addTransactions(asList(createTransaction(3, Collections.EMPTY_SET, asList(1L))));
    txObjectManager.lookupObjectsForTransactions();
    verify(coordinator, never()).addToApplyStage(argThat(hasTransactionID(3)));

    objectManager.release(o);
    txObjectManager.lookupObjectsForTransactions();
    InOrder inOrder = inOrder(coordinator);
    inOrder.verify(coordinator).addToApplyStage(argThat(hasTransactionID(2)));
    inOrder.verify(coordinator).addToApplyStage(argThat(hasTransactionID(3)));
  }

  public void testSkipApplyWithMissingNewObject() throws Exception {
    objectManager.addExistingObjectIDs(asCollectionOfObjectIDs(2L));
    ServerTransaction tx = createTransaction(0, asList(1L), asList(2L));
    gtxMgr.commit(tx.getServerTransactionID());

    txObjectManager.addTransactions(asList(tx));
    txObjectManager.lookupObjectsForTransactions();

    verify(objectManager, never()).createNewObjects((Set) argThat(containsObjectWithID(new ObjectID(1L))));
    verify(coordinator).addToApplyStage((EventContext) and(argThat(hasTransactionID(0)), argThat(hasIgnorableObject(new ObjectID(1L)))));
  }

  private static Collection<ObjectID> asCollectionOfObjectIDs(Long ... longs) {
    Set<ObjectID> oids = new BitSetObjectIDSet();
    for (long l : longs) {
      oids.add(new ObjectID(l));
    }
    return oids;
  }

  private ApplyTransactionInfo applyInfoWithTransactionID(long transactionID) {
    return spy(new ApplyTransactionInfo(true,
                                        new ServerTransactionID(new ClientID(0), new TransactionID(transactionID)),
                                        GlobalTransactionID.NULL_ID, true, false, serverEventBuffer,
                                        clientChannelMonitor));
  }

  private ArgumentMatcher<Collection<ManagedObject>> containsObjectWithID(final ObjectID id) {
    return new ArgumentMatcher<Collection<ManagedObject>>() {
      @Override
      public boolean matches(final Collection<ManagedObject> mos) {
        for (ManagedObject mo : mos) {
          if (id.equals(mo.getID())) {
            return true;
          }
        }
        return false;
      }
    };
  }

  private ArgumentMatcher<ApplyTransactionContext> hasIgnorableObject(final ObjectID oid) {
    return new ArgumentMatcher<ApplyTransactionContext>() {
      @Override
      public boolean matches(final ApplyTransactionContext cntx) {
        return cntx.getIgnoredObjects().contains(oid);
      }
    };
  }

  private ArgumentMatcher<ApplyTransactionContext> hasTransactionID(final long transactionID) {
    return new ArgumentMatcher<ApplyTransactionContext>() {
      @Override
      public boolean matches(final ApplyTransactionContext cntx) {
        return cntx.getTxn()
            .getServerTransactionID()
            .equals(new ServerTransactionID(new ClientID(0), new TransactionID(transactionID)));
      }
    };
  }

  private ArgumentMatcher<ApplyTransactionContext> needsApply() {
    return new ArgumentMatcher<ApplyTransactionContext>() {
      @Override
      public boolean matches(final ApplyTransactionContext cntx) {
        return cntx.needsApply();
      }
    };
  }

  private ServerTransaction createTransaction(long txId, Collection<Long> newObjects, Collection<Long> objects) {
    ServerTransaction transaction = mock(ServerTransaction.class);
    ObjectIDSet newObjectIDs = new BitSetObjectIDSet();
    for (long l : newObjects) {
      newObjectIDs.add(new ObjectID(l));
    }
    ObjectIDSet objectIDs = new BitSetObjectIDSet(newObjectIDs);
    for (long l : objects) {
      objectIDs.add(new ObjectID(l));
    }
    when(transaction.getServerTransactionID()).thenReturn(new ServerTransactionID(new ClientID(0), new TransactionID(txId)));
    when(transaction.getNewObjectIDs()).thenReturn(newObjectIDs);
    when(transaction.getObjectIDs()).thenReturn(objectIDs);
    return transaction;
  }
}
