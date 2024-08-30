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

import com.tc.async.api.EventContext;
import com.tc.async.api.Sink;
import com.tc.async.api.StageManager;
import com.tc.objectserver.context.LookupEventContext;
import com.tc.objectserver.core.api.ServerConfigurationContext;


public class TransactionalStagesCoordinatorImpl implements TransactionalStageCoordinator {

  private Sink               lookupSink;
  private Sink               applySink;

  private final StageManager stageManager;

  public TransactionalStagesCoordinatorImpl(StageManager stageManager) {
    this.stageManager = stageManager;
  }

  public void lookUpSinks() {
    this.lookupSink = stageManager.getStage(ServerConfigurationContext.TRANSACTION_LOOKUP_STAGE).getSink();
    this.applySink = stageManager.getStage(ServerConfigurationContext.APPLY_CHANGES_STAGE).getSink();
  }

  @Override
  public void addToApplyStage(EventContext context) {
    applySink.add(context);
  }

  @Override
  public void initiateLookup() {
    lookupSink.addLossy(new LookupEventContext());
  }

}
