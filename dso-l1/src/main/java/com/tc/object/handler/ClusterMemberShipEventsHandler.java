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
import com.tc.async.api.EventContext;
import com.tc.async.api.EventHandlerException;
import com.tc.net.ClientID;
import com.tc.object.msg.ClusterMembershipMessage;
import com.tcclient.cluster.DsoClusterInternalEventsGun;

public class ClusterMemberShipEventsHandler extends AbstractEventHandler {

  private final DsoClusterInternalEventsGun dsoClusterEventsGun;

  public ClusterMemberShipEventsHandler(final DsoClusterInternalEventsGun dsoClusterEventsGun) {
    this.dsoClusterEventsGun = dsoClusterEventsGun;
  }

  @Override
  public void handleEvent(EventContext context) throws EventHandlerException {
    if (context instanceof ClusterMembershipMessage) {
      handleClusterMembershipMessage((ClusterMembershipMessage) context);
    } else {
      throw new AssertionError("unknown event type: " + context.getClass().getName());
    }
  }

  private void handleClusterMembershipMessage(final ClusterMembershipMessage cmm) throws EventHandlerException {
    if (cmm.getProductId().isInternal()) {
      // don't fire events for internal products.
      return;
    }
    if (cmm.isNodeConnectedEvent()) {
      dsoClusterEventsGun.fireNodeJoined((ClientID)cmm.getNodeId());
    } else if (cmm.isNodeDisconnectedEvent()) {
      dsoClusterEventsGun.fireNodeLeft((ClientID)cmm.getNodeId());
    } else {
      throw new EventHandlerException("Unknown event type: " + cmm);
    }
  }

}
