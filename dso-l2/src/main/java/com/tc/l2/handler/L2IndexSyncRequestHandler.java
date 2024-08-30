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
package com.tc.l2.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.l2.context.SyncIndexesRequest;
import com.tc.l2.msg.IndexSyncAckMessage;
import com.tc.l2.objectserver.L2IndexStateManager;

public class L2IndexSyncRequestHandler extends AbstractEventHandler {

  private final L2IndexStateManager l2IndexStateManager;

  public L2IndexSyncRequestHandler(L2IndexStateManager l2IndexStateManager) {
    this.l2IndexStateManager = l2IndexStateManager;
  }

  @Override
  public void handleEvent(EventContext context) {
    if (context instanceof SyncIndexesRequest) {
      SyncIndexesRequest request = (SyncIndexesRequest) context;
      l2IndexStateManager.initiateIndexSync(request.getNodeID());
    } else if (context instanceof IndexSyncAckMessage) {
      IndexSyncAckMessage ack = (IndexSyncAckMessage) context;
      l2IndexStateManager.receivedAck(ack.messageFrom(), ack.getAmount());
    } else {
      throw new AssertionError("unexpected context: " + context);
    }
  }

}
