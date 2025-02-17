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
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.net.ClientID;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.object.msg.SyncWriteTransactionReceivedMessage;
import com.tc.object.net.DSOChannelManager;
import com.tc.object.net.NoSuchChannelException;
import com.tc.objectserver.context.SyncWriteTransactionReceivedContext;

/**
 * This class is responsible for acking back to the clients when it receives a sync write transaction
 */
public class SyncWriteTransactionReceivedHandler extends AbstractEventHandler {
  private final DSOChannelManager channelManager;
  private final static TCLogger   logger = TCLogging.getLogger(SyncWriteTransactionReceivedHandler.class.getName());

  public SyncWriteTransactionReceivedHandler(DSOChannelManager channelManager) {
    this.channelManager = channelManager;
  }

  @Override
  public void handleEvent(final EventContext context) {
    // send the message to the client
    SyncWriteTransactionReceivedContext syncCxt = (SyncWriteTransactionReceivedContext) context;
    ClientID cid = syncCxt.getClientID();
    long batchId = syncCxt.getBatchID();

    MessageChannel channel;
    try {
      channel = this.channelManager.getActiveChannel(cid);
    } catch (NoSuchChannelException e) {
      // Dont do anything
      logger.info("Cannot find channel for client " + cid + ". It might already be dead");
      return;
    }
    SyncWriteTransactionReceivedMessage message = (SyncWriteTransactionReceivedMessage) channel
        .createMessage(TCMessageType.SYNC_WRITE_TRANSACTION_RECEIVED_MESSAGE);
    message.initialize(batchId, syncCxt.getSyncTransactions());

    message.send();
  }
}
