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

/**
 * Creates ClientTransactions
 */
public class ClientTransactionFactoryImpl implements ClientTransactionFactory {

  public ClientTransactionFactoryImpl() {
    super();
  }

  @Override
  public ClientTransaction newInstance(int session) {
    return new ClientTransactionImpl(session);
  }

  @Override
  public ClientTransaction newNullInstance(final LockID id, final TxnType type) {
    ClientTransaction tc = new NullClientTransaction();
    tc.setTransactionContext(new TransactionContextImpl(id, type, type));
    return tc;
  }

}
