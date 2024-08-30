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
package com.tc.object.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventContext;
import com.tc.object.ClientConfigurationContext;
import com.tc.object.msg.BatchTransactionAcknowledgeMessage;
import com.tc.object.tx.ClientTransactionManager;

public class BatchTransactionAckHandler extends AbstractEventHandler {

  private ClientTransactionManager transactionManager;

  @Override
  public void handleEvent(EventContext context) {
    BatchTransactionAcknowledgeMessage msg = (BatchTransactionAcknowledgeMessage) context;
    transactionManager.receivedBatchAcknowledgement(msg.getBatchID(), msg.getSourceNodeID());
  }

  @Override
  public void initialize(ConfigurationContext context) {
    super.initialize(context);
    ClientConfigurationContext cc = (ClientConfigurationContext) context;
    transactionManager = cc.getTransactionManager();
  }

}
