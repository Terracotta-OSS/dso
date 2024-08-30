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

public interface TransactionFilter {

  public void addTransactionBatch(TransactionBatchContext transactionBatchContext);

  /**
   * The Filter returns true if the node can be disconnected immediately and the rest of the managers can be notified of
   * disconnect immediately. If not the filter calls back at a later time when it deems good.
   */
  public boolean shutdownNode(NodeID nodeID);

  public void notifyServerHighWaterMark(NodeID nodeID, long serverHighWaterMark);

}
