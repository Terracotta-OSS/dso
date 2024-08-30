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
package com.tc.objectserver.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.object.msg.CompletedTransactionLowWaterMarkMessage;
import com.tc.object.tx.ServerTransactionID;
import com.tc.objectserver.context.ServerTransactionCompleteContext;
import com.tc.objectserver.gtx.ServerGlobalTransactionManager;

public class TransactionLowWaterMarkHandler extends AbstractEventHandler {

  private final ServerGlobalTransactionManager gtxm;

  public TransactionLowWaterMarkHandler(ServerGlobalTransactionManager gtxm) {
    this.gtxm = gtxm;
  }

  @Override
  public void handleEvent(EventContext context) {
    if (context instanceof CompletedTransactionLowWaterMarkMessage) {
      CompletedTransactionLowWaterMarkMessage mdg = (CompletedTransactionLowWaterMarkMessage) context;
      ServerTransactionID sid = new ServerTransactionID(mdg.getSourceNodeID(), mdg.getLowWaterMark());
      gtxm.clearCommitedTransactionsBelowLowWaterMark(sid);
    } else if (context instanceof ServerTransactionCompleteContext) {
      gtxm.clearCommittedTransaction(((ServerTransactionCompleteContext) context).getServerTransactionID());
    } else {
      throw new IllegalArgumentException("Unknown context " + context);
    }
  }

}
