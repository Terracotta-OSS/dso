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
package com.tc.objectserver.gtx;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.object.gtx.GlobalTransactionManager;
import com.tc.objectserver.tx.ServerTransactionManager;
import com.tc.objectserver.tx.TxnsInSystemCompletionListener;

public class GlobalTransactionIDLowWaterMarkProvider implements GlobalTransactionManager {

  private static final TCLogger             logger              = TCLogging
                                                                    .getLogger(GlobalTransactionIDLowWaterMarkProvider.class);

  private final ServerTransactionManager    transactionManager;
  private final GlobalTransactionManager    gtxm;

  private volatile GlobalTransactionManager lwmProvider;

  private final GlobalTransactionManager    NULL_GLOBAL_TXN_MGR = new GlobalTransactionManager() {
                                                                  @Override
                                                                  public GlobalTransactionID getLowGlobalTransactionIDWatermark() {
                                                                    return GlobalTransactionID.NULL_ID;
                                                                  }
                                                                };

  public GlobalTransactionIDLowWaterMarkProvider(ServerTransactionManager transactionManager,
                                                 GlobalTransactionManager gtxm) {
    this.transactionManager = transactionManager;
    this.gtxm = gtxm;
    this.lwmProvider = NULL_GLOBAL_TXN_MGR;
  }

  public void goToActiveMode() {
    transactionManager.callBackOnResentTxnsInSystemCompletion(new TxnsInSystemCompletionListener() {
      @Override
      public void onCompletion() {
        switchLWMProvider();
      }
    });
  }

  @Override
  public GlobalTransactionID getLowGlobalTransactionIDWatermark() {
    return lwmProvider.getLowGlobalTransactionIDWatermark();
  }

  private void switchLWMProvider() {
    logger.info("Switching GlobalTransactionID Low Water mark provider since all resent transactions are applied");
    this.lwmProvider = gtxm;
  }
}
