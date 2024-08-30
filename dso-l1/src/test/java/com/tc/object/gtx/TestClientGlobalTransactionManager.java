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
package com.tc.object.gtx;

import com.tc.exception.ImplementMe;
import com.tc.net.NodeID;
import com.tc.object.locks.LockFlushCallback;
import com.tc.object.locks.LockID;
import com.tc.object.tx.TransactionID;
import com.tc.util.concurrent.NoExceptionLinkedQueue;

import java.util.Collection;

public class TestClientGlobalTransactionManager implements ClientGlobalTransactionManager {

  public final NoExceptionLinkedQueue flushCalls = new NoExceptionLinkedQueue();
  public Collection                   transactionSequenceIDs;

  @Override
  public void cleanup() {
    throw new ImplementMe();
  }

  @Override
  public void setLowWatermark(GlobalTransactionID lowWatermark, NodeID nodeID) {
    throw new ImplementMe();
  }

  @Override
  public void flush(LockID lockID) {
    flushCalls.put(lockID);
  }

  @Override
  public GlobalTransactionID getLowGlobalTransactionIDWatermark() {
    throw new ImplementMe();
  }

  @Override
  public int size() {
    throw new ImplementMe();
  }

  @Override
  public boolean asyncFlush(LockID lockID, LockFlushCallback callback) {
    return true;
  }

  @Override
  public boolean startApply(NodeID clientID, TransactionID transactionID, GlobalTransactionID globalTransactionID,
                            NodeID remoteGroupID) {
    throw new ImplementMe();
  }

  @Override
  public void waitForServerToReceiveTxnsForThisLock(LockID lock) {
    throw new ImplementMe();
  }
}
