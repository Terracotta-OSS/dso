/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
import com.tc.invalidation.Invalidations;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.net.ClientID;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.object.msg.InvalidateObjectsMessage;
import com.tc.object.net.DSOChannelManager;
import com.tc.object.net.NoSuchChannelException;
import com.tc.objectserver.context.InvalidateObjectsForClientContext;
import com.tc.objectserver.l1.api.InvalidateObjectManager;

public class InvalidateObjectsHandler extends AbstractEventHandler {

  private static final TCLogger         logger = TCLogging.getLogger(InvalidateObjectsHandler.class);
  private final InvalidateObjectManager invalidateObjMgr;
  private final DSOChannelManager       channelManager;

  public InvalidateObjectsHandler(InvalidateObjectManager invalidateObjMgr, DSOChannelManager channelManager) {
    this.invalidateObjMgr = invalidateObjMgr;
    this.channelManager = channelManager;
  }

  @Override
  public void handleEvent(EventContext context) {
    InvalidateObjectsForClientContext invalidateContext = (InvalidateObjectsForClientContext) context;
    ClientID clientID = invalidateContext.getClientID();
    Invalidations invalidations = invalidateObjMgr.getObjectsIDsToInvalidate(clientID);

    final MessageChannel channel = getActiveChannel(clientID);
    if (channel == null) { return; }

    final InvalidateObjectsMessage message = (InvalidateObjectsMessage) channel
        .createMessage(TCMessageType.INVALIDATE_OBJECTS_MESSAGE);

    message.initialize(invalidations.asMap());
    message.send();
  }

  private MessageChannel getActiveChannel(final ClientID clientID) {
    try {
      return this.channelManager.getActiveChannel(clientID);
    } catch (final NoSuchChannelException e) {
      logger.warn("Client " + clientID + " disconnect before sending Message to invalidate Objects.");
      return null;
    }
  }

}
