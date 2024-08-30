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

public interface ServerTransactionListener {
  
  public void incomingTransactions(NodeID source, Set serverTxnIDs);
  
  public void transactionApplied(ServerTransactionID stxID, ObjectIDSet newObjectsCreated);
  
  public void transactionCompleted(ServerTransactionID stxID);

  public void addResentServerTransactionIDs(Collection stxIDs);

  public void clearAllTransactionsFor(NodeID deadNode);

  public void transactionManagerStarted(Set cids);
  
}
