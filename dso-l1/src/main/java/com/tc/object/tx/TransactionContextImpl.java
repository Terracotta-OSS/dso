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

import com.tc.object.locks.LockID;

import java.util.ArrayList;
import java.util.List;

public class TransactionContextImpl implements TransactionContext {
  private final TxnType lockTxType;
  private final TxnType effectiveTxType;
  private final LockID  lockID;
  private final List    lockIDs;

  public TransactionContextImpl(final LockID lockID, final TxnType lockTxType, final TxnType effectiveTxType) {
    this.lockTxType = lockTxType;
    this.effectiveTxType = effectiveTxType;
    this.lockID = lockID;
    this.lockIDs = new ArrayList();
    lockIDs.add(lockID);
  }
  
  // assume lockIDs contains lockID
  public TransactionContextImpl(final LockID lockID, final TxnType lockTxType, final TxnType effectiveTxType, final List lockIDs) {
    this.lockTxType = lockTxType;
    this.effectiveTxType = effectiveTxType;
    this.lockID = lockID;
    this.lockIDs = lockIDs;    
  }
  
  @Override
  public TxnType getLockType() {
    return lockTxType;
  }

  @Override
  public TxnType getEffectiveType() {
    return effectiveTxType;
  }
  
  @Override
  public LockID getLockID() {
    return lockID;
  }

  @Override
  public List getAllLockIDs() {
    return lockIDs;
  }

  @Override
  public void removeLock(LockID id) {
    lockIDs.remove(id);
  }
}