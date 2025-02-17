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
package com.tc.objectserver.handler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.ArgumentCaptor;

import com.tc.async.api.EventContext;
import com.tc.async.api.Sink;
import com.tc.async.api.Stage;
import com.tc.net.ClientID;
import com.tc.object.dna.api.MetaDataReader;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.object.locks.LockID;
import com.tc.object.locks.Notify;
import com.tc.object.locks.NotifyImpl;
import com.tc.object.locks.StringLockID;
import com.tc.object.locks.ThreadID;
import com.tc.object.tx.TransactionID;
import com.tc.object.tx.TxnBatchID;
import com.tc.object.tx.TxnType;
import com.tc.objectserver.api.GarbageCollectionManager;
import com.tc.objectserver.api.ServerMapEvictionManager;
import com.tc.objectserver.api.Transaction;
import com.tc.objectserver.api.TransactionProvider;
import com.tc.objectserver.context.ApplyTransactionContext;
import com.tc.objectserver.context.BroadcastChangeContext;
import com.tc.objectserver.core.api.ServerConfigurationContext;
import com.tc.objectserver.core.impl.TestServerConfigurationContext;
import com.tc.objectserver.event.ClientChannelMonitor;
import com.tc.objectserver.event.ServerEventBuffer;
import com.tc.objectserver.gtx.ServerGlobalTransactionManager;
import com.tc.objectserver.impl.ObjectInstanceMonitorImpl;
import com.tc.objectserver.locks.LockManager;
import com.tc.objectserver.locks.NotifiedWaiters;
import com.tc.objectserver.locks.ServerLock;
import com.tc.objectserver.tx.ServerTransaction;
import com.tc.objectserver.tx.ServerTransactionImpl;
import com.tc.objectserver.tx.ServerTransactionManager;
import com.tc.objectserver.tx.TransactionalObjectManager;
import com.tc.objectserver.tx.TxnObjectGrouping;
import com.tc.util.SequenceID;
import com.tc.util.concurrent.Runners;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;
import org.mockito.ArgumentMatcher;

public class ApplyTransactionChangeHandlerTest extends TestCase {

  private ApplyTransactionChangeHandler  handler;
  private LockManager                    lockManager;
  private Sink                           broadcastSink;
  private ArgumentCaptor<NotifiedWaiters> notifiedWaitersArgumentCaptor;
  private ServerEventBuffer               serverEventBuffer;
  private ClientChannelMonitor            clientChannelMonitor;

  @Override
  public void setUp() throws Exception {
    this.lockManager = mock(LockManager.class);
    this.serverEventBuffer = mock(ServerEventBuffer.class);
    this.clientChannelMonitor = mock(ClientChannelMonitor.class);
    this.notifiedWaitersArgumentCaptor = ArgumentCaptor.forClass(NotifiedWaiters.class);
    TransactionProvider persistenceTransactionProvider = mock(TransactionProvider.class);
    Transaction persistenceTransaction = mock(Transaction.class);
    when(persistenceTransactionProvider.newTransaction()).thenReturn(persistenceTransaction);

    this.handler = new ApplyTransactionChangeHandler(new ObjectInstanceMonitorImpl(),
        mock(ServerGlobalTransactionManager.class),mock(ServerMapEvictionManager.class),
        persistenceTransactionProvider, Runners.newSingleThreadScheduledTaskRunner(),
        serverEventBuffer, clientChannelMonitor);

    this.broadcastSink = mock(Sink.class);
    Stage broadcastStage = mock(Stage.class);
    when(broadcastStage.getSink()).thenReturn(broadcastSink);
    TestServerConfigurationContext context = new TestServerConfigurationContext();
    context.transactionManager = mock(ServerTransactionManager.class);
    context.txnObjectManager = mock(TransactionalObjectManager.class);
    context.addStage(ServerConfigurationContext.BROADCAST_CHANGES_STAGE, broadcastStage);
    context.addStage(ServerConfigurationContext.COMMIT_CHANGES_STAGE, mock(Stage.class));
    context.garbageCollectionManager = mock(GarbageCollectionManager.class);
    context.lockManager = this.lockManager;

    this.handler.initializeContext(context);
  }

  public void testLockManagerNotifyOnNoApply() throws Exception {
    ServerTransaction tx = createServerTransaction();
    TxnObjectGrouping grouping = new TxnObjectGrouping(tx.getServerTransactionID());
    this.handler.handleEvent(new ApplyTransactionContext(tx, grouping, false, Collections.EMPTY_SET));
    verifyNotifies(tx);
  }

  public void testLockManagerNotifyOnApply() throws Exception {
    ServerTransaction tx = createServerTransaction();
    TxnObjectGrouping grouping = new TxnObjectGrouping(tx.getServerTransactionID());
    this.handler.handleEvent(new ApplyTransactionContext(tx, grouping, true, Collections.EMPTY_SET));
    verifyNotifies(tx);
  }

  private void verifyNotifies(ServerTransaction tx) {
    verify(lockManager, times(tx.getNumApplicationTxn())).notify(any(LockID.class), any(ClientID.class), any(ThreadID.class),
        any(ServerLock.NotifyAction.class), any(NotifiedWaiters.class));
    verify(broadcastSink, atLeastOnce()).add(any(EventContext.class));
    for (Notify notify : (Collection<Notify>) tx.getNotifies()) {
      verify(lockManager).notify(eq(notify.getLockID()), eq((ClientID)tx.getSourceID()), eq(notify.getThreadID()),
          eq(notify.getIsAll() ? ServerLock.NotifyAction.ALL : ServerLock.NotifyAction.ONE),
          notifiedWaitersArgumentCaptor.capture());
    }

    verify(broadcastSink).add(argThat(new BroadcastNotifiedWaiterMatcher(notifiedWaitersArgumentCaptor.getValue())));
  }

  private static class BroadcastNotifiedWaiterMatcher implements ArgumentMatcher<BroadcastChangeContext> {
    private final NotifiedWaiters notifiedWaiters;

    private BroadcastNotifiedWaiterMatcher(final NotifiedWaiters notifiedWaiters) {
      this.notifiedWaiters = notifiedWaiters;
    }

    @Override
    public boolean matches(final BroadcastChangeContext o) {
      if (notifiedWaiters == null) {
        return ((BroadcastChangeContext)o).getNewlyPendingWaiters() == null;
      } else {
        return notifiedWaiters.equals(((BroadcastChangeContext)o).getNewlyPendingWaiters());
      }
    }
  }

  private static ServerTransaction createServerTransaction() throws Exception {
    final ClientID cid = new ClientID(1);
    LockID[] lockIDs = { new StringLockID("1") };

    List<Notify> notifies = new LinkedList<Notify>();
    for (int i = 0; i < 10; i++) {
      notifies.add(new NotifyImpl(new StringLockID("" + i), new ThreadID(i), i % 2 == 0));
    }

    ServerTransaction txn = new ServerTransactionImpl(new TxnBatchID(1), new TransactionID(1), new SequenceID(1),
        lockIDs, cid, Collections.emptyList(), null,
                                                      Collections.emptyMap(), TxnType.NORMAL, notifies,
        new MetaDataReader[0], 1, new long[0]);

    txn.setGlobalTransactionID(GlobalTransactionID.NULL_ID);

    return txn;
  }
}
