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

import com.tc.net.NodeID;
import com.tc.object.tx.ServerTransactionID;
import com.tc.util.ObjectIDSet;

import java.util.Collection;
import java.util.Set;

public abstract class AbstractServerTransactionListener implements ServerTransactionListener {

  @Override
  public void addResentServerTransactionIDs(final Collection stxIDs) {
    // Override if you want
  }

  @Override
  public void clearAllTransactionsFor(final NodeID deadNode) {
    // Override if you want
  }

  @Override
  public void incomingTransactions(final NodeID source, final Set serverTxnIDs) {
    // Override if you want
  }

  @Override
  public void transactionApplied(final ServerTransactionID stxID, final ObjectIDSet newObjectsCreated) {
    // Override if you want
  }

  @Override
  public void transactionCompleted(final ServerTransactionID stxID) {
    // Override if you want
  }

  @Override
  public void transactionManagerStarted(final Set cids) {
    // Override if you want
  }
}
