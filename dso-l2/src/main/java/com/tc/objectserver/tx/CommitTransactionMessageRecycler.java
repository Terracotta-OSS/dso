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

import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.PostInit;
import com.tc.net.NodeID;
import com.tc.object.msg.MessageRecyclerImpl;
import com.tc.object.tx.ServerTransactionID;
import com.tc.objectserver.core.api.ServerConfigurationContext;
import com.tc.util.ObjectIDSet;

import java.util.Collection;
import java.util.Set;

public class CommitTransactionMessageRecycler extends MessageRecyclerImpl implements ServerTransactionListener,
    PostInit {

  @Override
  public void initializeContext(ConfigurationContext context) {
    ServerConfigurationContext scc = (ServerConfigurationContext) context;
    ServerTransactionManager transactionManager = scc.getTransactionManager();
    transactionManager.addTransactionListener(this);
  }

  @Override
  public void transactionCompleted(ServerTransactionID stxID) {
    recycle(stxID);
  }

  @Override
  public void transactionApplied(ServerTransactionID stxID, ObjectIDSet newObjectsCreated) {
    return;
  }

  @Override
  public void incomingTransactions(NodeID source, Set serverTxnIDs) {
    return;
  }

  @Override
  public void addResentServerTransactionIDs(Collection stxIDs) {
    return;
  }

  @Override
  public void clearAllTransactionsFor(NodeID deadNode) {
    return;
  }

  @Override
  public void transactionManagerStarted(Set cids) {
    return;
  }

}
