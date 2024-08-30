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
import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventContext;
import com.tc.l2.ha.L2HAZapNodeRequestProcessor;
import com.tc.l2.msg.RelayedCommitTransactionMessage;
import com.tc.l2.msg.RelayedCommitTransactionMessageFactory;
import com.tc.l2.objectserver.L2ObjectState;
import com.tc.l2.objectserver.L2ObjectStateManager;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.net.NodeID;
import com.tc.net.groups.GroupManager;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.object.tx.ServerTransactionID;
import com.tc.objectserver.core.api.ServerConfigurationContext;
import com.tc.objectserver.gtx.ServerGlobalTransactionManager;
import com.tc.objectserver.tx.ServerTransactionManager;
import com.tc.objectserver.tx.TransactionBatchContext;
import com.tc.util.sequence.SequenceGenerator;

import java.util.Collection;
import java.util.Iterator;

public class TransactionRelayHandler extends AbstractEventHandler {
  private static final TCLogger                logger = TCLogging.getLogger(TransactionRelayHandler.class);

  private final L2ObjectStateManager           l2ObjectStateMgr;
  private final SequenceGenerator              sequenceGenerator;

  private GroupManager                         groupManager;

  private ServerTransactionManager             transactionManager;

  private final ServerGlobalTransactionManager gtxm;

  public TransactionRelayHandler(final L2ObjectStateManager objectStateManager, final SequenceGenerator generator,
                                 final ServerGlobalTransactionManager gtxm) {
    this.l2ObjectStateMgr = objectStateManager;
    this.sequenceGenerator = generator;
    this.gtxm = gtxm;
  }

  @Override
  public void handleEvent(final EventContext context) {
    final TransactionBatchContext tbc = (TransactionBatchContext) context;
    final GlobalTransactionID lowWaterMark = this.gtxm.getLowGlobalTransactionIDWatermark();
    final Collection states = this.l2ObjectStateMgr.getL2ObjectStates();
    for (final Iterator i = states.iterator(); i.hasNext();) {
      final L2ObjectState state = (L2ObjectState) i.next();
      final NodeID nodeID = state.getNodeID();
      sendCommitTransactionMessage(nodeID, tbc, lowWaterMark);
    }
    this.transactionManager.transactionsRelayed(tbc.getSourceNodeID(), tbc.getTransactionIDs());
  }

  private void sendCommitTransactionMessage(final NodeID nodeID, final TransactionBatchContext tbc,
                                            final GlobalTransactionID lowWaterMark) {
    addWaitForNotification(nodeID, tbc);
    try {
      final RelayedCommitTransactionMessage msg = RelayedCommitTransactionMessageFactory
          .createRelayedCommitTransactionMessage(tbc.getSourceNodeID(), tbc.getBackingBuffers(), tbc.getTransactions(),
                                                 this.sequenceGenerator.getNextSequence(nodeID), lowWaterMark, tbc
                                                     .getSerializer());
      this.groupManager.sendTo(nodeID, msg);
    } catch (final Exception e) {
      reconsileWaitForNotification(nodeID, tbc);
      logger.error("Removing " + nodeID + " from group because of Exception :", e);
      this.groupManager.zapNode(nodeID, L2HAZapNodeRequestProcessor.COMMUNICATION_ERROR,
                                "Error relaying commit transaction message"
                                    + L2HAZapNodeRequestProcessor.getErrorString(e));
    }
  }

  private void reconsileWaitForNotification(final NodeID waitee, final TransactionBatchContext tbc) {
    // TODO::avoid this loop and thus N lookups in transactionManager
    for (final ServerTransactionID stxnID : tbc.getTransactionIDs()) {
      this.transactionManager.acknowledgement(tbc.getSourceNodeID(), stxnID.getClientTransactionID(), waitee);
    }
  }

  private void addWaitForNotification(final NodeID waitee, final TransactionBatchContext tbc) {
    // TODO::avoid this loop and thus N lookups in transactionManager
    for (final ServerTransactionID stxnID : tbc.getTransactionIDs()) {
      this.transactionManager.addWaitingForAcknowledgement(tbc.getSourceNodeID(), stxnID.getClientTransactionID(),
                                                           waitee);
    }
  }

  @Override
  public void initialize(final ConfigurationContext context) {
    super.initialize(context);
    final ServerConfigurationContext oscc = (ServerConfigurationContext) context;
    this.groupManager = oscc.getL2Coordinator().getGroupManager();
    this.transactionManager = oscc.getTransactionManager();
  }
}
