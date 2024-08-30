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
package com.tc.objectserver.context;

import com.tc.async.api.EventContext;
import com.tc.net.ClientID;
import com.tc.object.tx.TransactionID;

import java.util.Set;

public class SyncWriteTransactionReceivedContext implements EventContext {
  private final long               batchID;
  private final ClientID           cid;
  private final Set<TransactionID> txIdSet;

  public SyncWriteTransactionReceivedContext(long batchID, ClientID cid, Set<TransactionID> set) {
    this.batchID = batchID;
    this.cid = cid;
    this.txIdSet = set;
  }

  public long getBatchID() {
    return batchID;
  }

  public ClientID getClientID() {
    return cid;
  }

  public Set<TransactionID> getSyncTransactions() {
    return txIdSet;
  }
}
