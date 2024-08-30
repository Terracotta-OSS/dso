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
package com.tc.objectserver.metadata;

import com.tc.object.tx.ServerTransactionID;
import com.tc.objectserver.tx.ServerTransactionManager;
import com.terracottatech.search.ProcessingContext;

public class MetaDataProcessingContext implements ProcessingContext {

  private static final int               NOT_SET   = -1;

  private final ServerTransactionID      txnID;
  private final ServerTransactionManager txnManager;
  private int                            processed = 0;
  private int                            expected  = NOT_SET;

  public MetaDataProcessingContext(ServerTransactionID txnID, ServerTransactionManager txnManager) {
    this.txnID = txnID;
    this.txnManager = txnManager;
  }

  @Override
  public synchronized void processed() {
    if (isCountSet()) {
      if (processed + 1 > expected) {
        //
        throw new AssertionError("Exceeded expected count (" + expected + ") for " + txnID);
      }
    }

    processed++;
    attemptFinish();
  }

  public synchronized void setExpectedCount(int count) {
    if (count < 0) throw new AssertionError("invalid count (" + count + ") for " + txnID);

    if (isCountSet()) { throw new AssertionError("expected already set to " + expected + " for " + txnID); }

    this.expected = count;
    attemptFinish();
  }

  private boolean isCountSet() {
    return expected != NOT_SET;
  }

  private void attemptFinish() {
    if (isCountSet() && expected == processed) {
      txnManager.processingMetaDataCompleted(txnID.getSourceID(), txnID.getClientTransactionID());
    }
  }
}
