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
import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventContext;
import com.tc.logging.TCLogger;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.object.ObjectID;
import com.tc.object.msg.RequestRootMessage;
import com.tc.object.msg.RequestRootResponseMessage;
import com.tc.object.net.DSOChannelManager;
import com.tc.object.net.NoSuchChannelException;
import com.tc.objectserver.api.ObjectManager;
import com.tc.objectserver.core.api.ServerConfigurationContext;

/**
 * @author steve
 */
public class RequestRootHandler extends AbstractEventHandler {
  private ObjectManager     objectManager;
  private DSOChannelManager channelManager;
  private TCLogger          logger;

  @Override
  public void handleEvent(EventContext context) {
    RequestRootMessage rrm = (RequestRootMessage) context;
    ObjectID rootID = objectManager.lookupRootID(rrm.getRootName());
    try {
      MessageChannel channel = channelManager.getActiveChannel(rrm.getSourceNodeID());

      RequestRootResponseMessage rrrm = (RequestRootResponseMessage) channel
          .createMessage(TCMessageType.REQUEST_ROOT_RESPONSE_MESSAGE);
      rrrm.initialize(rrm.getRootName(), rootID);
      rrrm.send();
    } catch (NoSuchChannelException e) {
      logger.info("Failed to send root request response because channel:" + rrm.getSourceNodeID() + " is disconnected.");
      return;
    }
  }

  @Override
  public void initialize(ConfigurationContext context) {
    super.initialize(context);
    ServerConfigurationContext oscc = (ServerConfigurationContext) context;

    objectManager = oscc.getObjectManager();
    this.channelManager = oscc.getChannelManager();
    this.logger = oscc.getLogger(this.getClass());
  }
}