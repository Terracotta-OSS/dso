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
package com.tc.platform;

import static org.mockito.AdditionalMatchers.or;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.tc.abortable.AbortableOperationManager;
import com.tc.object.ClientObjectManager;
import com.tc.object.ClientShutdownManager;
import com.tc.object.DistributedObjectClient;
import com.tc.object.RemoteSearchRequestManager;
import com.tc.object.ServerEventListenerManager;
import com.tc.object.handshakemanager.ClientHandshakeManager;
import com.tc.object.locks.ClientLockManager;
import com.tc.object.locks.LockID;
import com.tc.object.locks.LockIdFactory;
import com.tc.object.locks.LockLevel;
import com.tc.object.locks.LongLockID;
import com.tc.object.tx.ClientTransactionManager;
import com.tc.platform.rejoin.RejoinManagerInternal;
import com.tc.util.UUID;
import com.tc.util.concurrent.TaskRunner;
import com.tcclient.cluster.DsoClusterInternal;

import java.util.concurrent.TimeUnit;

public class PlatformServiceImplTest {

  @Test
  public void testUnlockOnTxBeginException() throws Exception {
    ClientObjectManager clientObjectManager = mock(ClientObjectManager.class);
    ClientTransactionManager clientTransactionManager = mock(ClientTransactionManager.class);
    ClientShutdownManager clientShutdownManager = mock(ClientShutdownManager.class);
    ClientLockManager clientLockManager = mock(ClientLockManager.class);
    RemoteSearchRequestManager remoteSearchRequestManager = mock(RemoteSearchRequestManager.class);
    DistributedObjectClient distributedObjectClient = mock(DistributedObjectClient.class);
    LockIdFactory lockIdFactory = mock(LockIdFactory.class);
    DsoClusterInternal dsoCluster = mock(DsoClusterInternal.class);
    AbortableOperationManager abortableOperationManager = mock(AbortableOperationManager.class);
    UUID uuid = UUID.getUUID();
    ServerEventListenerManager serverEventListenerManager = mock(ServerEventListenerManager.class);
    RejoinManagerInternal rejoinManager = mock(RejoinManagerInternal.class);
    TaskRunner taskRunner = mock(TaskRunner.class);
    ClientHandshakeManager clientHandshakeManager = mock(ClientHandshakeManager.class);

    when(lockIdFactory.generateLockIdentifier(or(any(LockID.class), anyLong()))).thenReturn(new LongLockID(42));

    PlatformService ps = new PlatformServiceImpl(clientObjectManager, clientTransactionManager, clientShutdownManager,
                                                 clientLockManager, remoteSearchRequestManager,
                                                 distributedObjectClient, lockIdFactory, dsoCluster,
                                                 abortableOperationManager, uuid, serverEventListenerManager,
                                                 rejoinManager, taskRunner, clientHandshakeManager);

    doThrow(SomeException.class).when(clientTransactionManager).begin(any(LockID.class), any(LockLevel.class),
                                                                      anyBoolean());
    when(clientLockManager.tryLock(any(LockID.class), any(LockLevel.class))).thenReturn(Boolean.TRUE);
    when(clientLockManager.tryLock(any(LockID.class), any(LockLevel.class), any(Long.TYPE))).thenReturn(Boolean.TRUE);

    InOrder inOrder = inOrder(clientLockManager);
    try {
      ps.beginAtomicTransaction(new LongLockID(1), LockLevel.WRITE);
      throw new AssertionError();
    } catch (RuntimeException e) {
      verifyException(e);
    }

    inOrder.verify(clientLockManager, Mockito.calls(1)).lock(any(LockID.class), any(LockLevel.class));
    inOrder.verify(clientLockManager, Mockito.calls(1)).unlock(any(LockID.class), any(LockLevel.class));

    inOrder = inOrder(clientLockManager);
    try {
      ps.beginLock(new LongLockID(1), LockLevel.WRITE);
      throw new AssertionError();
    } catch (RuntimeException e) {
      verifyException(e);
    }

    inOrder.verify(clientLockManager, Mockito.calls(1)).lock(any(LockID.class), any(LockLevel.class));
    inOrder.verify(clientLockManager, Mockito.calls(1)).unlock(any(LockID.class), any(LockLevel.class));

    inOrder = inOrder(clientLockManager);
    try {
      ps.beginLockInterruptibly(1L, LockLevel.WRITE);
      throw new AssertionError();
    } catch (RuntimeException e) {
      verifyException(e);
    }

    inOrder.verify(clientLockManager, Mockito.calls(1)).lock(any(LockID.class), any(LockLevel.class));
    inOrder.verify(clientLockManager, Mockito.calls(1)).unlock(any(LockID.class), any(LockLevel.class));

    inOrder = inOrder(clientLockManager);
    try {
      ps.tryBeginLock(1L, LockLevel.WRITE);
      throw new AssertionError();
    } catch (RuntimeException e) {
      verifyException(e);
    }

    inOrder.verify(clientLockManager, Mockito.calls(1)).lock(any(LockID.class), any(LockLevel.class));
    inOrder.verify(clientLockManager, Mockito.calls(1)).unlock(any(LockID.class), any(LockLevel.class));

    inOrder = inOrder(clientLockManager);
    try {
      ps.tryBeginLock(1L, LockLevel.WRITE, 1L, TimeUnit.SECONDS);
      throw new AssertionError();
    } catch (RuntimeException e) {
      verifyException(e);
    }

    inOrder.verify(clientLockManager, Mockito.calls(1)).lock(any(LockID.class), any(LockLevel.class));
    inOrder.verify(clientLockManager, Mockito.calls(1)).unlock(any(LockID.class), any(LockLevel.class));

  }

  private static void verifyException(RuntimeException e) throws AssertionError {
    if (e.getClass() != SomeException.class) { throw new AssertionError(e.getCause().getClass()); }
  }

  private class SomeException extends RuntimeException {
    //
  }
}
