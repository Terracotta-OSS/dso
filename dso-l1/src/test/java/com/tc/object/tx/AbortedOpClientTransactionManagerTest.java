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

import com.tc.abortable.AbortableOperationManager;
import com.tc.abortable.AbortableOperationManagerImpl;
import com.tc.abortable.AbortedOperationException;
import com.tc.net.protocol.tcm.TestChannelIDProvider;
import com.tc.object.ClientIDProviderImpl;
import com.tc.object.LogicalOperation;
import com.tc.object.TestClientObjectManager;
import com.tc.object.locks.LockID;
import com.tc.object.locks.LockLevel;
import com.tc.object.locks.MockClientLockManager;
import com.tc.object.locks.StringLockID;
import com.tc.stats.counter.sampled.SampledCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;
import junit.framework.TestCase;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbortedOpClientTransactionManagerTest extends TestCase {
  TestClientTransactionFactory clientTxnFactory;
  TestRemoteTransactionManager rmtTxnMgr;
  TestClientObjectManager      objMgr;
  ClientTransactionManagerImpl clientTxnMgr;
  AtomicReference<Throwable>   error = new AtomicReference<Throwable>(null);
  AbortableOperationManager    abortableOperationManager;

  @Override
  public void setUp() throws Exception {
    clientTxnFactory = new TestClientTransactionFactory();
    rmtTxnMgr = new TestRemoteTransactionManager();
    objMgr = new TestClientObjectManager();
    abortableOperationManager = new AbortableOperationManagerImpl();

    clientTxnMgr = new ClientTransactionManagerImpl(new ClientIDProviderImpl(new TestChannelIDProvider()), objMgr,
                                                    clientTxnFactory, new MockClientLockManager(), rmtTxnMgr,
                                                    SampledCounter.NULL_SAMPLED_COUNTER, null,
                                                    abortableOperationManager);
  }

  @Override
  public void tearDown() throws Exception {
    if (error.get() != null) { throw new RuntimeException(error.get()); }
  }

  public void test() throws Exception {
    clientTxnMgr.begin(new StringLockID("test1"), LockLevel.WRITE, false);
    abortableOperationManager.begin();

    try {
      clientTxnMgr.begin(new StringLockID("test2"), LockLevel.WRITE, false);

      // change1
      clientTxnFactory.clientTransactions.get(0).logicalInvoke(null, LogicalOperation.ADD, null, null);
      Assert.assertEquals(1, clientTxnFactory.clientTransactions.size());

      abortableOperationManager.abort(Thread.currentThread());

      boolean expected = false;
      try {
        clientTxnMgr.commit(new StringLockID("test2"), LockLevel.WRITE, false, null);
      } catch (AbortedOperationException e) {
        expected = true;
      }

      Assert.assertTrue(expected);
    } finally {
      abortableOperationManager.finish();
    }
    Assert.assertEquals(2, clientTxnFactory.clientTransactions.size());

    // change 2
    clientTxnFactory.clientTransactions.get(1).addNotify(null);

    verify(clientTxnFactory.clientTransactions.get(0), times(1))
      .logicalInvoke(any(), any(LogicalOperation.class), any(), any());
    verify(clientTxnFactory.clientTransactions.get(1), never())
      .logicalInvoke(any(), any(LogicalOperation.class), any(), any());

    verify(clientTxnFactory.clientTransactions.get(0), never()).addNotify(any());
    verify(clientTxnFactory.clientTransactions.get(1), times(1)).addNotify(any());
    // change2
    clientTxnMgr.commit(new StringLockID("test1"), LockLevel.WRITE, false, null);

    Assert.assertEquals(clientTxnFactory.clientTransactions.get(1), rmtTxnMgr.getTransaction());
  }

  private static class TestClientTransactionFactory implements ClientTransactionFactory {
    private final List<ClientTransaction> clientTransactions = new ArrayList<ClientTransaction>();

    @Override
    public ClientTransaction newNullInstance(LockID id, TxnType type) {
      return null;
    }

    @Override
    public ClientTransaction newInstance(int session) {
      ClientTransaction clientTransaction = mock(ClientTransaction.class);
      when(clientTransaction.hasChangesOrNotifies()).thenReturn(true);

      clientTransactions.add(clientTransaction);
      return clientTransaction;
    }
  }

}
