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
import com.tc.net.NodeID;
import com.tc.objectserver.tx.ServerTransaction;

public class TransactionLookupContext implements EventContext {

  private final ServerTransaction txn;
  private final boolean           initiateApply;

  public TransactionLookupContext(ServerTransaction txn, boolean initiateApply) {
    this.txn = txn;
    this.initiateApply = initiateApply;
  }

  @Override
  public String toString() {
    return "TransactionLookupContext [ " + txn + " initiateApply = " + initiateApply + " ]";
  }

  public ServerTransaction getTransaction() {
    return txn;
  }

  public boolean initiateApply() {
    return initiateApply;
  }

  public NodeID getSourceID() {
    return txn.getSourceID();
  }

}
