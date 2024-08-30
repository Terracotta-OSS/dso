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
import com.tc.objectserver.core.api.ServerConfigurationContext;

public class PassThruTransactionFilter implements TransactionFilter, PostInit {

  private TransactionBatchManager transactionBatchManager;

  @Override
  public void initializeContext(final ConfigurationContext context) {
    final ServerConfigurationContext scc = (ServerConfigurationContext) context;
    this.transactionBatchManager = scc.getTransactionBatchManager();
  }

  @Override
  public void addTransactionBatch(final TransactionBatchContext transactionBatchContext) {
    this.transactionBatchManager.processTransactions(transactionBatchContext);
  }

  @Override
  public boolean shutdownNode(final NodeID nodeID) {
    return true;
  }

  @Override
  public void notifyServerHighWaterMark(final NodeID nodeID, final long serverHighWaterMark) {
    // NOP
  }
}