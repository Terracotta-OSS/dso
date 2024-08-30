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
package com.tc.objectserver.l1.impl;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.net.NodeID;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.object.msg.AcknowledgeTransactionMessage;
import com.tc.object.msg.BatchTransactionAcknowledgeMessage;
import com.tc.object.net.DSOChannelManager;
import com.tc.object.net.NoSuchChannelException;
import com.tc.object.tx.ServerTransactionID;
import com.tc.object.tx.TxnBatchID;
import com.tc.objectserver.tx.NoSuchBatchException;
import com.tc.objectserver.tx.TransactionBatchManager;

/**
 * @author steve
 */
public class TransactionAcknowledgeActionImpl implements TransactionAcknowledgeAction {
  private final DSOChannelManager       channelManager;
  private final TCLogger                logger = TCLogging.getLogger(TransactionAcknowledgeActionImpl.class);
  private final TransactionBatchManager transactionBatchManager;

  public TransactionAcknowledgeActionImpl(DSOChannelManager channelManager,
                                          TransactionBatchManager transactionBatchManager) {
    this.channelManager = channelManager;
    this.transactionBatchManager = transactionBatchManager;
  }

  @Override
  public void acknowledgeTransaction(ServerTransactionID stxID) {
    try {
      NodeID nodeID = stxID.getSourceID();
      MessageChannel channel = channelManager.getActiveChannel(nodeID);

      // send ack
      AcknowledgeTransactionMessage m = (AcknowledgeTransactionMessage) channel
          .createMessage(TCMessageType.ACKNOWLEDGE_TRANSACTION_MESSAGE);
      m.initialize(stxID.getSourceID(), stxID.getClientTransactionID());
      m.send();

      // send batch ack if necessary
      try {
        if (transactionBatchManager.batchComponentComplete(nodeID, stxID.getClientTransactionID())) {
          BatchTransactionAcknowledgeMessage msg = channelManager.newBatchTransactionAcknowledgeMessage(nodeID);
          // We always send null batch ID since its never used - clean up
          msg.initialize(TxnBatchID.NULL_BATCH_ID);
          msg.send();
        }
      } catch (NoSuchBatchException e) {
        throw new AssertionError(e);
      }

    } catch (NoSuchChannelException e) {
      logger.info("An attempt was made to send a commit ack but the client seems to have gone away:" + stxID);
      return;
    }
  }
}