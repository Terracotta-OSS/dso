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
package com.tc.l2.ha;

import com.tc.l2.ha.WeightGeneratorFactory.WeightGenerator;
import com.tc.object.msg.CommitTransactionMessage;
import com.tc.objectserver.tx.TransactionBatchManager;
import com.tc.util.Assert;

import java.util.concurrent.atomic.AtomicLong;

public class LastTxnTimeWeightGenerator implements WeightGenerator, TransactionBatchListener {
  private final AtomicLong lastTxnTime = new AtomicLong(Long.MIN_VALUE);

  public LastTxnTimeWeightGenerator(TransactionBatchManager transactionBatchManager) {
    Assert.assertNotNull(transactionBatchManager);
    transactionBatchManager.registerForBatchTransaction(this);
  }

  /*
   * return (weight-generation-time - last-batch-transaction-time) return 0 if none txn yet.
   * negative weight, closest one win.
   */
  @Override
  public long getWeight() {
    long last = lastTxnTime.get();
    return (last == Long.MIN_VALUE) ? last : last - System.nanoTime();
  }

  @Override
  public void notifyTransactionBatchAdded(CommitTransactionMessage ctm) {
    lastTxnTime.set(System.nanoTime());
  }
}
