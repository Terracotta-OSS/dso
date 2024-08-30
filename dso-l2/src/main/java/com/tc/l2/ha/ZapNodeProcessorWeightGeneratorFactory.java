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

import com.tc.object.net.DSOChannelManager;
import com.tc.objectserver.tx.ServerTransactionManager;
import com.tc.objectserver.tx.TransactionBatchManager;

public class ZapNodeProcessorWeightGeneratorFactory extends WeightGeneratorFactory {
  public ZapNodeProcessorWeightGeneratorFactory(final DSOChannelManager channelManager,
                                                final TransactionBatchManager transactionBatchManager,
                                                final ServerTransactionManager serverTransactionManager,
                                                final String host, int port) {
    super();

    add(new ChannelWeightGenerator(channelManager));
    add(new LastTxnTimeWeightGenerator(transactionBatchManager));
    add(new TxnCountWeightGenerator(serverTransactionManager));
    add(new ServerIdentifierWeightGenerator(host, port));
    // add a random generator to break tie
    add(RANDOM_WEIGHT_GENERATOR);

  }
}
