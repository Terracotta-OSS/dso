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


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.tc.exception.ImplementMe;
import com.tc.exception.TCRuntimeException;
import com.tc.net.ClientID;
import com.tc.net.NodeID;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.object.ObjectID;
import com.tc.object.ServerMapRequestType;
import com.tc.object.dna.api.MetaDataReader;
import com.tc.object.dna.impl.ObjectStringSerializer;
import com.tc.object.gtx.GlobalTransactionIDAlreadySetException;
import com.tc.object.locks.LockID;
import com.tc.object.locks.TestLockManager;
import com.tc.object.net.ChannelStats;
import com.tc.object.tx.ServerTransactionID;
import com.tc.object.tx.TransactionID;
import com.tc.object.tx.TxnBatchID;
import com.tc.object.tx.TxnType;
import com.tc.objectserver.api.ObjectInstanceMonitor;
import com.tc.objectserver.context.TransactionLookupContext;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.objectserver.gtx.TestGlobalTransactionManager;
import com.tc.objectserver.impl.ObjectInstanceMonitorImpl;
import com.tc.objectserver.impl.TestObjectManager;
import com.tc.objectserver.l1.api.TestClientStateManager;
import com.tc.objectserver.l1.impl.TransactionAcknowledgeAction;
import com.tc.objectserver.managedobject.ApplyTransactionInfo;
import com.tc.objectserver.metadata.NullMetaDataManager;
import com.tc.objectserver.mgmt.ObjectStatsRecorder;
import com.tc.stats.counter.Counter;
import com.tc.stats.counter.CounterImpl;
import com.tc.util.ObjectIDSet;
import com.tc.util.SequenceID;
import com.tc.util.concurrent.NoExceptionLinkedQueue;

import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.Callable;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class ServerTransactionManagerImplTest extends TestCase {

  private ServerTransactionManagerImpl       transactionManager;
  private TestTransactionAcknowledgeAction   transactionAcknowledgeAction;
  private TestClientStateManager             clientStateManager;
  private TestLockManager                    lockManager;
  private TestObjectManager                  objectManager;
  private Counter                            transactionRateCounter;
  private TestChannelStats                   channelStats;
  private TestGlobalTransactionManager       gtxm;
  private ObjectInstanceMonitor              imo;
  private ResentTransactionSequencer         resentTransactionSequencer;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    this.transactionAcknowledgeAction = new TestTransactionAcknowledgeAction();
    this.clientStateManager = new TestClientStateManager();
    this.lockManager = new TestLockManager();
    this.objectManager = new TestObjectManager();
    this.transactionRateCounter = new CounterImpl();
    this.channelStats = new TestChannelStats();
    this.gtxm = new TestGlobalTransactionManager();
    this.imo = new ObjectInstanceMonitorImpl();
    this.resentTransactionSequencer = mock(ResentTransactionSequencer.class);
    newTransactionManager();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  private void newTransactionManager() {
    this.transactionManager = new ServerTransactionManagerImpl(this.gtxm, this.lockManager,
                                                               this.clientStateManager, this.objectManager,
                                                               new TestTransactionalObjectManager(), this.transactionAcknowledgeAction,
                                                               this.transactionRateCounter, this.channelStats,
                                                               new ServerTransactionManagerConfig(),
                                                               new ObjectStatsRecorder(), new NullMetaDataManager(),
                                                               resentTransactionSequencer);
    this.transactionManager.goToActiveMode();
    this.transactionManager.start(Collections.EMPTY_SET);
  }

  public void testCallbackOnResentTxnComplete() throws Exception {
    TxnsInSystemCompletionListener listener = mock(TxnsInSystemCompletionListener.class);
    transactionManager.callBackOnResentTxnsInSystemCompletion(listener);
    verify(resentTransactionSequencer).callBackOnResentTxnsInSystemCompletion(listener);
  }

  public void testRootCreatedEvent() {
    Map<String, ObjectID> roots = new HashMap<String, ObjectID>();
    roots.put("root", new ObjectID(1));

    // first test w/o any listeners attached
    this.transactionManager.commit(Collections.EMPTY_SET, roots, Collections.EMPTY_LIST);

    // add a listener
    Listener listener = new Listener();
    this.transactionManager.addRootListener(listener);
    roots.clear();
    roots.put("root2", new ObjectID(2));

    this.transactionManager.commit(Collections.EMPTY_SET, roots, Collections.EMPTY_LIST);
    assertEquals(1, listener.rootsCreated.size());
    Root root = (Root) listener.rootsCreated.remove(0);
    assertEquals("root2", root.name);
    assertEquals(new ObjectID(2), root.id);

    // add another listener
    Listener listener2 = new Listener();
    this.transactionManager.addRootListener(listener2);
    roots.clear();
    roots.put("root3", new ObjectID(3));

    this.transactionManager.commit(Collections.EMPTY_SET, roots, Collections.EMPTY_LIST);
    assertEquals(1, listener.rootsCreated.size());
    root = (Root) listener.rootsCreated.remove(0);
    assertEquals("root3", root.name);
    assertEquals(new ObjectID(3), root.id);
    root = (Root) listener2.rootsCreated.remove(0);
    assertEquals("root3", root.name);
    assertEquals(new ObjectID(3), root.id);

    // add a listener that throws an exception
    this.transactionManager.addRootListener(new ServerTransactionManagerEventListener() {
      @Override
      public void rootCreated(String name, ObjectID id) {
        throw new RuntimeException("This exception is supposed to be here");
      }
    });
    this.transactionManager.commit(Collections.EMPTY_SET, roots, Collections.EMPTY_LIST);
  }

  public void testAddAndRemoveTransactionListeners() throws Exception {
    TestServerTransactionListener l1 = new TestServerTransactionListener();
    TestServerTransactionListener l2 = new TestServerTransactionListener();
    this.transactionManager.addTransactionListener(l1);
    this.transactionManager.addTransactionListener(l2);

    Map<ServerTransactionID, ServerTransaction> txns = new HashMap<ServerTransactionID, ServerTransaction>();

    ClientID cid1 = new ClientID(1);
    List dnas = Collections.unmodifiableList(new LinkedList());
    ObjectStringSerializer serializer = null;
    Map newRoots = Collections.unmodifiableMap(new HashMap());
    TxnType txnType = TxnType.NORMAL;

    for (int i = 0; i < 10; i++) {
      TransactionID tid1 = new TransactionID(i);
      SequenceID sequenceID = new SequenceID(i);
      LockID[] lockIDs = new LockID[0];
      ServerTransaction tx = newServerTransactionImpl(new TxnBatchID(1), tid1, sequenceID, lockIDs, cid1, dnas,
                                                      serializer, newRoots, txnType, new LinkedList(), 1);
      txns.put(tx.getServerTransactionID(), tx);
    }
    doStages(cid1, txns, false);

    // check for events
    Object o[] = (Object[]) l1.incomingContext.take();
    assertNotNull(o);
    o = (Object[]) l2.incomingContext.take();
    assertNotNull(o);

    for (int i = 0; i < 10; i++) {
      ServerTransactionID tid1 = (ServerTransactionID) l1.appliedContext.take();
      ServerTransactionID tid2 = (ServerTransactionID) l2.appliedContext.take();
      assertEquals(tid1, tid2);
      // System.err.println("tid1 = " + tid1 + " tid2 = " + tid2 + " tids = " + tids);
      assertTrue(txns.containsKey(tid1));
      tid1 = (ServerTransactionID) l1.completedContext.take();
      tid2 = (ServerTransactionID) l2.completedContext.take();
      assertEquals(tid1, tid2);
      assertTrue(txns.containsKey(tid1));
    }

    // No more events
    o = (Object[]) l1.incomingContext.poll(2000);
    assertNull(o);
    o = (Object[]) l2.incomingContext.poll(2000);
    assertNull(o);
    ServerTransactionID tid = (ServerTransactionID) l1.appliedContext.poll(2000);
    assertNull(tid);
    tid = (ServerTransactionID) l2.appliedContext.poll(2000);
    assertNull(tid);
    tid = (ServerTransactionID) l1.completedContext.poll(2000);
    assertNull(tid);
    tid = (ServerTransactionID) l2.completedContext.poll(2000);
    assertNull(tid);

    // unregister one
    this.transactionManager.removeTransactionListener(l2);

    // more txn
    txns.clear();
    for (int i = 10; i < 20; i++) {
      TransactionID tid1 = new TransactionID(i);
      SequenceID sequenceID = new SequenceID(i);
      LockID[] lockIDs = new LockID[0];
      ServerTransaction tx = newServerTransactionImpl(new TxnBatchID(2), tid1, sequenceID, lockIDs, cid1, dnas,
                                                      serializer, newRoots, txnType, new LinkedList(), 1);
      txns.put(tx.getServerTransactionID(), tx);
    }
    doStages(cid1, txns, false);

    // Events to only l1
    o = (Object[]) l1.incomingContext.take();
    assertNotNull(o);
    o = (Object[]) l2.incomingContext.poll(2000);
    assertNull(o);

    for (int i = 0; i < 10; i++) {
      ServerTransactionID tid1 = (ServerTransactionID) l1.appliedContext.take();
      ServerTransactionID tid2 = (ServerTransactionID) l2.appliedContext.poll(1000);
      assertNotNull(tid1);
      assertNull(tid2);
      assertTrue(txns.containsKey(tid1));
      tid1 = (ServerTransactionID) l1.completedContext.take();
      tid2 = (ServerTransactionID) l2.completedContext.poll(1000);
      assertNotNull(tid1);
      assertNull(tid2);
      assertTrue(txns.containsKey(tid1));
    }
  }

  /**
   * A transaction is broadcasted to another client, the orginating client disconnects and then the broadcasted client
   * disconnects. This test was written to illustrate a scenario where when multiple clients were disconnecting, were
   * acks are being waited for, a concurrent modification exception was thrown.
   */
  public void test2ClientsDisconnectAtTheSameTime() throws Exception {
    ClientID cid1 = new ClientID(1);
    TransactionID tid1 = new TransactionID(1);
    TransactionID tid2 = new TransactionID(2);
    TransactionID tid3 = new TransactionID(3);
    ClientID cid2 = new ClientID(2);
    ClientID cid3 = new ClientID(3);
    ClientID cid4 = new ClientID(4);
    ClientID cid5 = new ClientID(5);

    LockID[] lockIDs = new LockID[0];
    List dnas = Collections.unmodifiableList(new LinkedList());
    ObjectStringSerializer serializer = null;
    Map newRoots = Collections.unmodifiableMap(new HashMap());
    TxnType txnType = TxnType.NORMAL;
    SequenceID sequenceID = new SequenceID(1);
    ServerTransaction tx1 = newServerTransactionImpl(new TxnBatchID(1), tid1, sequenceID, lockIDs, cid1, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);

    Map<ServerTransactionID, ServerTransaction> txns = new HashMap<ServerTransactionID, ServerTransaction>();
    txns.put(tx1.getServerTransactionID(), tx1);
    this.transactionManager.incomingTransactions(cid1, txns);
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid1, cid2);
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid1, cid3);
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid1, cid4);
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid1, cid5);
    doStages(cid1, txns, true);

    // Adding a few more transactions to that Transaction Records are created for everybody
    txns.clear();
    ServerTransaction tx2 = newServerTransactionImpl(new TxnBatchID(2), tid2, sequenceID, lockIDs, cid2, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);
    txns.put(tx2.getServerTransactionID(), tx2);
    this.transactionManager.incomingTransactions(cid2, txns);

    this.transactionManager.acknowledgement(cid2, tid2, cid3);
    doStages(cid2, txns, true);

    txns.clear();
    ServerTransaction tx3 = newServerTransactionImpl(new TxnBatchID(2), tid3, sequenceID, lockIDs, cid3, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);
    txns.put(tx3.getServerTransactionID(), tx3);
    this.transactionManager.incomingTransactions(cid3, txns);

    this.transactionManager.acknowledgement(cid3, tid3, cid4);
    this.transactionManager.acknowledgement(cid3, tid3, cid2);
    doStages(cid2, txns, true);

    assertTrue(this.transactionManager.isWaiting(cid1, tid1));

    this.transactionManager.acknowledgement(cid1, tid1, cid3);
    assertTrue(this.transactionManager.isWaiting(cid1, tid1));
    this.transactionManager.acknowledgement(cid1, tid1, cid4);
    assertTrue(this.transactionManager.isWaiting(cid1, tid1));
    this.transactionManager.acknowledgement(cid1, tid1, cid5);
    assertTrue(this.transactionManager.isWaiting(cid1, tid1));

    // Client 1 disconnects
    this.transactionManager.shutdownNode(cid1);

    // Still waiting for tx1
    assertTrue(this.transactionManager.isWaiting(cid1, tid1));

    // Client 2 disconnects now
    // Concurrent Modification exception used to be thrown here.
    this.transactionManager.shutdownNode(cid2);

    // Not waiting for tx1 anymore
    assertFalse(this.transactionManager.isWaiting(cid1, tid1));

    // Client 3 disconnects now
    // Concurrent Modification exception used to be thrown here.
    this.transactionManager.shutdownNode(cid2);




  }

  public void test1ClientDisconnectWithWaiteeAsSameClient() throws Exception {
    ClientID cid1 = new ClientID(1);
    TransactionID tid1 = new TransactionID(1);

    LockID[] lockIDs = new LockID[0];
    List dnas = Collections.unmodifiableList(new LinkedList());
    ObjectStringSerializer serializer = null;
    Map newRoots = Collections.unmodifiableMap(new HashMap());
    TxnType txnType = TxnType.NORMAL;
    SequenceID sequenceID = new SequenceID(1);
    ServerTransaction tx1 = newServerTransactionImpl(new TxnBatchID(1), tid1, sequenceID, lockIDs, cid1, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);

    Map<ServerTransactionID, ServerTransaction> txns = new HashMap<ServerTransactionID, ServerTransaction>();

    txns.put(tx1.getServerTransactionID(), tx1);
    this.transactionManager.incomingTransactions(cid1, txns);
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid1, cid1);
    doStages(cid1, txns, true);

    assertTrue(this.transactionManager.isWaiting(cid1, tid1));

    // Client 1 disconnects
    this.transactionManager.shutdownNode(cid1);

    // Still waiting for tx1
    assertFalse(this.transactionManager.isWaiting(cid1, tid1));
  }

  public void testPauseUnpauseTransactions() throws Exception {
    final ClientID clientID1 = new ClientID(1);
    final TransactionID tid1 = new TransactionID(1);
    LockID[] lockIDs = new LockID[0];
    List dnas = Collections.unmodifiableList(new LinkedList());
    ObjectStringSerializer serializer = null;
    Map newRoots = Collections.unmodifiableMap(new HashMap());
    TxnType txnType = TxnType.NORMAL;
    SequenceID sequenceID = new SequenceID(1);
    ServerTransaction tx1 = newServerTransactionImpl(new TxnBatchID(1), tid1, sequenceID, lockIDs, clientID1, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);

    final Map<ServerTransactionID, ServerTransaction> txns = new HashMap<ServerTransactionID, ServerTransaction>();
    txns.put(tx1.getServerTransactionID(), tx1);

    ExecutorService executorService = Executors.newFixedThreadPool(1);

    Callable addIncomingTxnRunnable = new Callable() {
      @Override
      public Object call() throws Exception {
        transactionManager.incomingTransactions(clientID1, txns);
        transactionManager.transactionsRelayed(clientID1, txns.keySet());
        return true;
      }
    };

    transactionManager.pauseTransactions();
    Future addIncomingTxnFuture =  executorService.submit(addIncomingTxnRunnable);

    assertFalse(this.transactionManager.isWaiting(clientID1, tid1));
    assertTrue(this.transactionAcknowledgeAction.clientID == null && this.transactionAcknowledgeAction.txID == null);

    transactionManager.unPauseTransactions();

    addIncomingTxnFuture.get();
    doStages(clientID1, txns);
    assertTrue(this.transactionAcknowledgeAction.clientID == clientID1 && this.transactionAcknowledgeAction.txID == tid1);

    executorService.shutdown();
    executorService.awaitTermination(2, TimeUnit.MINUTES);
  }

  /**
   * This test attempts to ensure the deadlock conditions described in TAB-7386 are resolved.
   */
  public void testUnpauseDuringShutdownNode() throws Exception {
    /*
     * This test needs to observe monitor locks to work -- get the ThreadMXBean through which
     * that observation is done.
     */
    final ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
    final ClientID client1 = new ClientID(1);
    final ClientID client2 = new ClientID(2);

    /*
     * Open a transaction for Client 1.
     */
    TransactionID firstTransactionId = new TransactionID(1);

    final ServerTransaction client1Transaction =
        newServerTransactionImpl(new TxnBatchID(1), firstTransactionId, new SequenceID(1), new LockID[0], client1,
            Collections.emptyList(), null, Collections.emptyMap(), TxnType.NORMAL, new LinkedList(), 1);

    final HashMap<ServerTransactionID, ServerTransaction> client1TransactionBatch =
        new HashMap<ServerTransactionID, ServerTransaction>();
    client1TransactionBatch.put(client1Transaction.getServerTransactionID(), client1Transaction);
    transactionManager.incomingTransactions(client1, client1TransactionBatch);

    transactionManager.transactionsRelayed(client1, client1TransactionBatch.keySet());
    transactionManager.apply(client1Transaction, Collections.emptyMap(), new ApplyTransactionInfo(), imo);
    transactionManager.commit(Collections.<ManagedObject>emptySet(), Collections.<String, ObjectID>emptyMap(),
        Collections.singleton(client1Transaction.getServerTransactionID()));

    /*
     * Add Client 2 as a waiter for the client 1 transaction.
     */
    TransactionID secondTransactionId = new TransactionID(2);
    ServerTransaction client2Transaction =
        newServerTransactionImpl(new TxnBatchID(2), secondTransactionId, new SequenceID(2), new LockID[0], client2,
            Collections.emptyList(), null, Collections.emptyMap(), TxnType.NORMAL, new LinkedList(), 1);

    final HashMap<ServerTransactionID, ServerTransaction> client2TransactionBatch =
        new HashMap<ServerTransactionID, ServerTransaction>();
    client2TransactionBatch.put(client2Transaction.getServerTransactionID(), client2Transaction);
    transactionManager.incomingTransactions(client2, client2TransactionBatch);
    transactionManager.addWaitingForAcknowledgement(client2, secondTransactionId, client1);

    transactionManager.transactionsRelayed(client2, client2TransactionBatch.keySet());
    transactionManager.apply(client2Transaction, Collections.emptyMap(), new ApplyTransactionInfo(), imo);
    transactionManager.commit(Collections.<ManagedObject>emptySet(), Collections.<String, ObjectID>emptyMap(),
        Collections.singleton(client2Transaction.getServerTransactionID()));
    transactionManager.broadcasted(client2Transaction.getSourceID(), client2Transaction.getTransactionID());


    final Map<Thread, Throwable> uncaughtExceptions = Collections.synchronizedMap(new LinkedHashMap<Thread, Throwable>());
    Thread.UncaughtExceptionHandler exceptionHandler = new Thread.UncaughtExceptionHandler() {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        uncaughtExceptions.put(t, e);
      }
    };

    /*
     * Create and start a thread for the "third" client.  This thread must be observable
     * by the thread created for the "first" client to leave.  Start of the third client thread
     * is interlocked with arrival of the first client thread in the 'onCompletion' callback
     * established below.
     */
    final CyclicBarrier barrier = new CyclicBarrier(2);
    final Thread thirdClientThread = new Thread(new Runnable() {
      @Override
      public void run() {
        System.out.format("[%s] Awaiting barrier alignment%n", Thread.currentThread());
        try {
          barrier.await();
        } catch (InterruptedException e) {
          throw new AssertionError("barrier interrupted");
        } catch (BrokenBarrierException e) {
          throw new AssertionError("barrier broken");
        }

        /*
         * At this point, the first client thread is within the onCompletion callback (via
         * ServerTransactionManagerImpl.shutdownNode).  We can now enter
         * ServerTransactionManagerImpl.incomingTransactions.
         */
        ClientID client3 = new ClientID(3);
        ServerTransaction client3Transaction =
            newServerTransactionImpl(new TxnBatchID(3), new TransactionID(3), new SequenceID(3), new LockID[0], client3,
                Collections.emptyList(), null, Collections.emptyMap(), TxnType.NORMAL, new LinkedList(), 1);

        HashMap<ServerTransactionID, ServerTransaction> transactionBatch =
            new HashMap<ServerTransactionID, ServerTransaction>();
        transactionBatch.put(client3Transaction.getServerTransactionID(), client3Transaction);
        System.out.format("[%s] Invoking ServerTransactionManager.incomingTransactions(client3, ...)%n", Thread.currentThread());
        transactionManager.incomingTransactions(client3, transactionBatch);
      }
    }, "ServerTransactionManagerImplTest.testUnpauseDuringShutdownNode:client3");
    thirdClientThread.setDaemon(true);
    thirdClientThread.setUncaughtExceptionHandler(exceptionHandler);
    thirdClientThread.start();

    /*
     * Pause transaction processing "for backup".
     */
    transactionManager.pauseTransactions();

    /*
     * Set a transaction completion callback that performs an unpause "timed" to
     * occur while 'incomingTransactions' is running.
     */
    transactionManager.callBackOnTxnsInSystemCompletion(new TxnsInSystemCompletionListener() {
      @Override
      public void onCompletion() {
        System.out.format("[%s] In callBackOnTxnsInSystemCompletion - awaiting barrier alignment%n", Thread.currentThread());
        try {
          barrier.await();
        } catch (InterruptedException e) {
          throw new AssertionError("barrier interrupted");
        } catch (BrokenBarrierException e) {
          throw new AssertionError("barrier broken");
        }

        /*
         * Before the fix for TAB-7386, this 'onCompletion' callback is entered without holding
         * the monitor lock on ServerTransactionManagerImpl; after the fix, the monitor lock is
         * already held.  If we're holding the lock, there's no way for the client 3 thread to
         * attain the lock so don't wait for the lock here.
         */
        if (!Thread.holdsLock(transactionManager)) {
          /*
           * Now await arrival of the client 3 thread _inside_ the
           * ServerTransactionManagerImpl.incomingTransactions method.  This is indicated by that thread holding
           * the monitor lock on ServerTransactionManagerImpl.
           */
          System.out.format("[%s] In callBackOnTxnsInSystemCompletion - awaiting ServerTransactionManagerImpl lock%n", Thread.currentThread());
          locked:
          while (true) {
            ThreadInfo[] threadInfo = threadMXBean.getThreadInfo(new long[] { thirdClientThread.getId() },  true, false);
            MonitorInfo[] lockedMonitors = threadInfo[0].getLockedMonitors();
            for (MonitorInfo lockedMonitor : lockedMonitors) {
              System.out.format("[%s] lockedMonitor[%s]=%s%n", Thread.currentThread(), lockedMonitor, thirdClientThread);
              if (ServerTransactionManagerImpl.class.getName().equals(lockedMonitor.getClassName())) {
                break locked;
              }
            }
            try {
              TimeUnit.MILLISECONDS.sleep(10L);
            } catch (InterruptedException e) {
              throw new AssertionError("lock poll interrupted");
            }
          }
        }

        System.out.format("[%s] In callBackOnTxnsInSystemCompletion - unpausing transactions%n", Thread.currentThread());
        transactionManager.unPauseTransactions();
      }
    });

    /*
     * In another background thread, call shutdownNode for client 1; this ultimately reaches the
     * barrier while holding the ServerTransactionManagerImpl.transactionAccounts lock.
     */
    Thread firstClientThread = new Thread(new Runnable() {
      @Override
      public void run() {
        System.out.format("[%s] Invoking ServerTransactionManager.shutdownNode(client1)%n", Thread.currentThread());
        transactionManager.broadcasted(client1Transaction.getSourceID(), client1Transaction.getTransactionID());
        transactionManager.shutdownNode(client1);
      }
    }, "ServerTransactionManagerImplTest.testUnpauseDuringShutdownNode:client1");
    firstClientThread.setDaemon(true);
    firstClientThread.setUncaughtExceptionHandler(exceptionHandler);
    firstClientThread.start();

    TimeUnit.MILLISECONDS.timedJoin(firstClientThread, 500L);
    TimeUnit.MILLISECONDS.timedJoin(thirdClientThread, 500L);

    if (!uncaughtExceptions.isEmpty()) {
      for (Map.Entry<Thread, Throwable> entry : uncaughtExceptions.entrySet()) {
        System.out.format("ServerTransactionManagerImplTest.testUnpauseDuringShutdownNode failed: [%s] %s%n", entry.getKey(), entry.getValue());
        entry.getValue().printStackTrace(System.out);
      }
      throw new AssertionError(uncaughtExceptions.values().iterator().next());
    }

    long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
    if (deadlockedThreads != null) {
      System.out.format("%nDeadlocked threads:%n");
      ThreadInfo[] deadlockedThreadInfo = threadMXBean.getThreadInfo(deadlockedThreads, true, true);
      for (ThreadInfo threadInfo : deadlockedThreadInfo) {
        System.out.format("%s", threadInfo);
      }
      throw new AssertionError("Deadlock detected among the following threadIds: " + Arrays.toString(deadlockedThreads));
    }

    assertFalse(firstClientThread.isAlive());
    assertFalse(thirdClientThread.isAlive());
  }

  public void tests() throws Exception {
    ClientID cid1 = new ClientID(1);
    TransactionID tid1 = new TransactionID(1);
    TransactionID tid2 = new TransactionID(2);
    TransactionID tid3 = new TransactionID(3);
    TransactionID tid4 = new TransactionID(4);
    TransactionID tid5 = new TransactionID(5);
    TransactionID tid6 = new TransactionID(6);

    ClientID cid2 = new ClientID(2);
    ClientID cid3 = new ClientID(3);

    LockID[] lockIDs = new LockID[0];
    List dnas = Collections.unmodifiableList(new LinkedList());
    ObjectStringSerializer serializer = null;
    Map newRoots = Collections.unmodifiableMap(new HashMap());
    TxnType txnType = TxnType.NORMAL;
    SequenceID sequenceID = new SequenceID(1);
    ServerTransaction tx1 = newServerTransactionImpl(new TxnBatchID(1), tid1, sequenceID, lockIDs, cid1, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);

    // Test with one waiter
    Map<ServerTransactionID, ServerTransaction> txns = new HashMap<ServerTransactionID, ServerTransaction>();
    txns.put(tx1.getServerTransactionID(), tx1);
    this.transactionManager.incomingTransactions(cid1, txns);
    transactionManager.transactionsRelayed(cid1, txns.keySet());
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid1, cid2);
    assertTrue(this.transactionManager.isWaiting(cid1, tid1));
    assertTrue(this.transactionAcknowledgeAction.clientID == null && this.transactionAcknowledgeAction.txID == null);
    this.transactionManager.acknowledgement(cid1, tid1, cid2);
    assertTrue(this.transactionAcknowledgeAction.clientID == null && this.transactionAcknowledgeAction.txID == null);
    doStages(cid1, txns);
    assertTrue(this.transactionAcknowledgeAction.clientID == cid1 && this.transactionAcknowledgeAction.txID == tid1);
    assertFalse(this.transactionManager.isWaiting(cid1, tid1));

    // Test with 2 waiters
    this.transactionAcknowledgeAction.clear();
    this.gtxm.clear();
    txns.clear();
    sequenceID = new SequenceID(2);
    ServerTransaction tx2 = newServerTransactionImpl(new TxnBatchID(2), tid2, sequenceID, lockIDs, cid1, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);
    txns.put(tx2.getServerTransactionID(), tx2);
    this.transactionManager.incomingTransactions(cid1, txns);
    transactionManager.transactionsRelayed(cid1, txns.keySet());
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid2, cid2);
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid2, cid3);
    assertTrue(this.transactionAcknowledgeAction.clientID == null && this.transactionAcknowledgeAction.txID == null);
    assertTrue(this.transactionManager.isWaiting(cid1, tid2));
    this.transactionManager.acknowledgement(cid1, tid2, cid2);
    assertTrue(this.transactionAcknowledgeAction.clientID == null && this.transactionAcknowledgeAction.txID == null);
    assertTrue(this.transactionManager.isWaiting(cid1, tid2));
    this.transactionManager.acknowledgement(cid1, tid2, cid3);
    assertTrue(this.transactionAcknowledgeAction.clientID == null && this.transactionAcknowledgeAction.txID == null);
    doStages(cid1, txns);
    assertTrue(this.transactionAcknowledgeAction.clientID == cid1 && this.transactionAcknowledgeAction.txID == tid2);
    assertFalse(this.transactionManager.isWaiting(cid1, tid2));

    // Test shutdown client with 2 waiters
    this.transactionAcknowledgeAction.clear();
    this.gtxm.clear();
    txns.clear();
    sequenceID = new SequenceID(3);
    ServerTransaction tx3 = newServerTransactionImpl(new TxnBatchID(3), tid3, sequenceID, lockIDs, cid1, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);
    txns.put(tx3.getServerTransactionID(), tx3);
    this.transactionManager.incomingTransactions(cid1, txns);
    transactionManager.transactionsRelayed(cid1, txns.keySet());
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid3, cid2);
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid3, cid3);
    assertTrue(this.transactionAcknowledgeAction.clientID == null && this.transactionAcknowledgeAction.txID == null);
    assertTrue(this.transactionManager.isWaiting(cid1, tid3));
    this.transactionManager.shutdownNode(cid3);
    assertEquals(cid3, this.clientStateManager.shutdownClient);
    assertTrue(this.transactionManager.isWaiting(cid1, tid3));
    this.transactionManager.acknowledgement(cid1, tid3, cid2);
    doStages(cid1, txns);
    assertTrue(this.transactionAcknowledgeAction.clientID == cid1 && this.transactionAcknowledgeAction.txID == tid3);
    assertFalse(this.transactionManager.isWaiting(cid1, tid3));

    // Test shutdown client that no one is waiting for
    this.transactionAcknowledgeAction.clear();
    this.gtxm.clear();
    txns.clear();
    this.clientStateManager.shutdownClient = null;

    sequenceID = new SequenceID(4);
    ServerTransaction tx4 = newServerTransactionImpl(new TxnBatchID(4), tid4, sequenceID, lockIDs, cid1, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);
    txns.put(tx4.getServerTransactionID(), tx4);
    this.transactionManager.incomingTransactions(cid1, txns);
    transactionManager.transactionsRelayed(cid1, txns.keySet());
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid4, cid2);
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid4, cid3);
    this.transactionManager.shutdownNode(cid1);
    assertTrue(this.transactionAcknowledgeAction.clientID == null && this.transactionAcknowledgeAction.txID == null);
    // It should still be waiting, since we only do cleans ups on completion of all transactions.
    assertNull(this.clientStateManager.shutdownClient);
    assertTrue(this.transactionManager.isWaiting(cid1, tid4));

    // adding new transactions should throw an error
    boolean failed = false;
    try {
      this.transactionManager.incomingTransactions(cid1, txns);
      failed = true;
    } catch (Throwable t) {
      // failed as expected.
    }
    if (failed) {
      //
      throw new Exception("Calling incomingTransaction after client shutdown didnt throw an error as excepted!!! ;(");
    }
    this.transactionManager.acknowledgement(cid1, tid4, cid2);
    assertTrue(this.transactionManager.isWaiting(cid1, tid4));
    this.transactionManager.acknowledgement(cid1, tid4, cid3);
    assertFalse(this.transactionManager.isWaiting(cid1, tid4));

    // shutdown is not called yet since apply commit and broadcast need to complete.
    assertNull(this.clientStateManager.shutdownClient);
    List serverTids = new ArrayList();
    serverTids.add(new ServerTransactionID(cid1, tid4));
    this.transactionManager.commit(Collections.EMPTY_SET, Collections.EMPTY_MAP, serverTids);
    assertNull(this.clientStateManager.shutdownClient);
    this.transactionManager.broadcasted(cid1, tid4);
    assertEquals(cid1, this.clientStateManager.shutdownClient);

    // Test with 2 waiters on different tx's
    this.transactionAcknowledgeAction.clear();
    this.gtxm.clear();
    txns.clear();
    sequenceID = new SequenceID(5);
    ServerTransaction tx5 = newServerTransactionImpl(new TxnBatchID(5), tid5, sequenceID, lockIDs, cid1, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);
    sequenceID = new SequenceID(6);
    ServerTransaction tx6 = newServerTransactionImpl(new TxnBatchID(5), tid6, sequenceID, lockIDs, cid1, dnas,
                                                     serializer, newRoots, txnType, new LinkedList(), 1);
    txns.put(tx5.getServerTransactionID(), tx5);
    txns.put(tx6.getServerTransactionID(), tx6);

    this.transactionManager.incomingTransactions(cid1, txns);
    transactionManager.transactionsRelayed(cid1, txns.keySet());
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid5, cid2);
    this.transactionManager.addWaitingForAcknowledgement(cid1, tid6, cid2);

    assertTrue(this.transactionAcknowledgeAction.clientID == null && this.transactionAcknowledgeAction.txID == null);
    assertTrue(this.transactionManager.isWaiting(cid1, tid5));
    assertTrue(this.transactionManager.isWaiting(cid1, tid6));

    this.transactionManager.acknowledgement(cid1, tid5, cid2);
    assertFalse(this.transactionManager.isWaiting(cid1, tid5));
    assertTrue(this.transactionManager.isWaiting(cid1, tid6));
    doStages(cid1, txns);
    assertTrue(this.transactionAcknowledgeAction.clientID == cid1 && this.transactionAcknowledgeAction.txID == tid5);

  }

  private ServerTransaction newServerTransactionImpl(TxnBatchID txnBatchID, TransactionID tid, SequenceID sequenceID,
                                                     LockID[] lockIDs, ClientID cid, List dnas,
                                                     ObjectStringSerializer serializer, Map newRoots, TxnType txnType,
                                                     Collection notifies, int numAppTxns) {
    ServerTransaction txn = new ServerTransactionImpl(txnBatchID, tid, sequenceID, lockIDs, cid, dnas, serializer,
                                                      newRoots, txnType, notifies, new MetaDataReader[0],
                                                      numAppTxns, new long[0]);
    try {
      txn.setGlobalTransactionID(this.gtxm.getOrCreateGlobalTransactionID(txn.getServerTransactionID()));
    } catch (GlobalTransactionIDAlreadySetException e) {
      throw new AssertionError(e);
    }
    return txn;
  }

  private void doStages(ClientID cid1, Map txns) {
    doStages(cid1, txns, true);
  }

  private void doStages(ClientID cid1, Map<ServerTransactionID, ServerTransaction> txns, boolean skipIncoming) {

    // process stage
    if (!skipIncoming) {
      this.transactionManager.incomingTransactions(cid1, txns);
      transactionManager.transactionsRelayed(cid1, txns.keySet());
    }

    for (ServerTransaction tx : txns.values()) {
      // apply stage
      this.transactionManager.apply(tx, Collections.EMPTY_MAP, new ApplyTransactionInfo(), this.imo);

      // commit stage
      Set committedIDs = new HashSet();
      committedIDs.add(tx.getServerTransactionID());
      this.transactionManager.commit(Collections.EMPTY_SET, Collections.EMPTY_MAP, committedIDs);

      // broadcast stage
      this.transactionManager.broadcasted(tx.getSourceID(), tx.getTransactionID());
    }
  }

  private static final class TestChannelStats implements ChannelStats {

    public BlockingQueue<NodeID> notifyTransactionContexts = new LinkedBlockingQueue<NodeID>();

    @Override
    public Counter getCounter(MessageChannel channel, String name) {
      throw new ImplementMe();
    }

    @Override
    public void notifyTransaction(NodeID nodeID, int numTxns) {
      try {
        this.notifyTransactionContexts.put(nodeID);
      } catch (InterruptedException e) {
        throw new TCRuntimeException(e);
      }
    }

    @Override
    public void notifyReadOperations(MessageChannel channel, int numObjectsRequested) {
      throw new ImplementMe();
    }

    @Override
    public void notifyTransactionAckedFrom(NodeID nodeID) {
      // NOP
    }

    @Override
    public void notifyTransactionBroadcastedTo(NodeID nodeID) {
      // NOP
    }

    @Override
    public void notifyServerMapRequest(ServerMapRequestType type, MessageChannel channel, int numRequests) {
      // NOP
    }

  }

  private static class Root {
    final String   name;
    final ObjectID id;

    Root(String name, ObjectID id) {
      this.name = name;
      this.id = id;
    }
  }

  private static class Listener implements ServerTransactionManagerEventListener {
    final List rootsCreated = new ArrayList();

    @Override
    public void rootCreated(String name, ObjectID id) {
      this.rootsCreated.add(new Root(name, id));
    }
  }

  private static class TestServerTransactionListener extends AbstractServerTransactionListener {

    NoExceptionLinkedQueue incomingContext  = new NoExceptionLinkedQueue();
    NoExceptionLinkedQueue appliedContext   = new NoExceptionLinkedQueue();
    NoExceptionLinkedQueue completedContext = new NoExceptionLinkedQueue();

    @Override
    public void incomingTransactions(NodeID source, Set serverTxnIDs) {
      this.incomingContext.put(new Object[] { source, serverTxnIDs });
    }

    @Override
    public void transactionApplied(ServerTransactionID stxID, ObjectIDSet newObjectsCreated) {
      this.appliedContext.put(stxID);
    }

    @Override
    public void transactionCompleted(ServerTransactionID stxID) {
      this.completedContext.put(stxID);
    }
  }

  public static class TestTransactionAcknowledgeAction implements TransactionAcknowledgeAction {
    public NodeID        clientID;
    public TransactionID txID;

    @Override
    public void acknowledgeTransaction(ServerTransactionID stxID) {
      this.txID = stxID.getClientTransactionID();
      this.clientID = stxID.getSourceID();
    }

    public void clear() {
      this.txID = null;
      this.clientID = null;
    }

  }

  private class TestTransactionalObjectManager extends NullTransactionalObjectManager {
    @Override
    public void addTransactions(Collection<ServerTransaction> txns) {
      Set<TransactionLookupContext> txnContexts = new HashSet<TransactionLookupContext>();
      for (ServerTransaction txn : txns) {
        txnContexts.add(new TransactionLookupContext(txn, false));
        transactionManager.processMetaData(txn, new ApplyTransactionInfo());
      }
    }
  }
}
