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
package com.tc.l2.state;

import com.tc.async.api.Sink;
import com.tc.l2.L2DebugLogging;
import com.tc.l2.L2DebugLogging.LogLevel;
import com.tc.l2.context.StateChangedEvent;
import com.tc.l2.ha.L2HAZapNodeRequestProcessor;
import com.tc.l2.ha.WeightGeneratorFactory;
import com.tc.l2.msg.L2StateMessage;
import com.tc.l2.state.sbp.SBPResolver;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.management.TSAManagementEventPayload;
import com.tc.management.TerracottaRemoteManagement;
import com.tc.net.NodeID;
import com.tc.net.ServerID;
import com.tc.net.groups.GroupException;
import com.tc.net.groups.GroupManager;
import com.tc.net.groups.GroupMessage;
import com.tc.objectserver.persistence.ClusterStatePersistor;
import com.tc.operatorevent.TerracottaOperatorEventFactory;
import com.tc.operatorevent.TerracottaOperatorEventLogger;
import com.tc.operatorevent.TerracottaOperatorEventLogging;
import com.tc.util.Assert;
import com.tc.util.State;

import java.util.concurrent.CopyOnWriteArrayList;

public class StateManagerImpl implements StateManager {

  private static final TCLogger        logger              = TCLogging.getLogger(StateManagerImpl.class);

  private final TCLogger               consoleLogger;
  private final GroupManager           groupManager;
  private final ElectionManager        electionMgr;
  private final Sink                   stateChangeSink;
  private final WeightGeneratorFactory weightsFactory;

  private final CopyOnWriteArrayList<StateChangeListener> listeners           = new CopyOnWriteArrayList<StateChangeListener>();
  private final Object                 electionLock        = new Object();
  private final ClusterStatePersistor  clusterStatePersistor;
  private final SBPResolver            sbpResolver;

  private NodeID                       activeNode          = ServerID.NULL_ID;
  private volatile State               state               = START_STATE;
  private boolean                      electionInProgress  = false;
  TerracottaOperatorEventLogger        operatorEventLogger = TerracottaOperatorEventLogging.getEventLogger();

  public StateManagerImpl(TCLogger consoleLogger, GroupManager groupManager, Sink stateChangeSink,
                          StateManagerConfig stateManagerConfig, WeightGeneratorFactory weightFactory,
                          final ClusterStatePersistor clusterStatePersistor, SBPResolver sbpResolver) {
    this.consoleLogger = consoleLogger;
    this.groupManager = groupManager;
    this.stateChangeSink = stateChangeSink;
    this.weightsFactory = weightFactory;
    this.electionMgr = new ElectionManagerImpl(groupManager, stateManagerConfig);
    this.clusterStatePersistor = clusterStatePersistor;
    this.sbpResolver = sbpResolver;
  }

  @Override
  public State getCurrentState() {
    return this.state;
  }

  /*
   * XXX:: If ACTIVE went dead before any passive moved to STANDBY state, then the cluster is hung and there is no going
   * around it. If ACTIVE in persistent mode, it can come back and recover the cluster
   */
  @Override
  public void startElection() {
    debugInfo("Starting election");
    synchronized (electionLock) {
      if (electionInProgress) return;
      electionInProgress = true;
    }
    
    if(state == ACTIVE_COORDINATOR && sbpResolver.isEnabled()) {
      runElection();
    } else {
      try {
        State initial = clusterStatePersistor.getInitialState();
        // Went down as either PASSIVE_STANDBY or UNITIALIZED, either way we need to wait for the active to zap, just skip
        // the election and wait for a zap.
        if (initial != null && !initial.equals(ACTIVE_COORDINATOR)) {
          info("Skipping election and waiting for the active to zap since this this L2 did not go down as active.");
        } else if (state == START_STATE || state == PASSIVE_STANDBY) {
          runElection();
        } else {
          info("Ignoring Election request since not in right state");
        }
      } finally {
        synchronized (electionLock) {
          electionInProgress = false;
        }
      }
    }
  }

  private void runElection() {
    if(sbpResolver.isEnabled() && state == ACTIVE_COORDINATOR) {
      if(sbpResolver.resolveTiedElection(state)) {
        logger.info("Won the tie-breaking in " + ACTIVE_COORDINATOR + " state. So skipping further election.");
        return;
      } else {
        throw new AssertionError(ACTIVE_COORDINATOR + " losing tie breaking is not supported.");
      }
    }
    
    NodeID myNodeID = getLocalNodeID();
    NodeID winner = ServerID.NULL_ID;
    int count = 0;
    // Only new L2 if the DB was empty (no previous state) and the current state is START (as in before any elections
    // concluded)
    boolean isNew = state == START_STATE && clusterStatePersistor.getInitialState() == null;
    while (getActiveNodeID().isNull()) {
      if (++count > 1) {
        logger.info("Rerunning election since node " + winner + " never declared itself as ACTIVE !");
      }
      debugInfo("Running election - isNew: " + isNew);
      winner = electionMgr.runElection(myNodeID, isNew, weightsFactory);
      if (winner == myNodeID) {
        if(sbpResolver.isEnabled()) {
          if(sbpResolver.resolveTiedElection(state)) {
            logger.info(myNodeID + " won the election. Moving to active state.");
            moveToActiveState();
          } else {
            logger.info(myNodeID + " lost the election. Waiting for the active to come up");
            waitUntilActiveNodeIDNotNull();
          }
        } else {
          debugInfo("Won Election, moving to active state. myNodeID/winner=" + myNodeID);
          moveToActiveState();
        }
      } else {
        // Election is lost, but we wait for the active node to declare itself as winner. If this doesn't happen in a
        // finite time we restart the election. This is to prevent some weird cases where two nodes might end up
        // thinking the other one is the winner.
        // @see MNK-518
        debugInfo("Lost election, waiting for winner to declare as active, winner=" + winner);
        waitUntilActiveNodeIDNotNull();
      }
    }
  }
  
  private synchronized void waitUntilActiveNodeIDNotNull() {
    electionMgr.reset(null);
    long timeout = electionMgr.getElectionTime();
    while (activeNode.isNull() && timeout > 0) {
      long start = System.currentTimeMillis();
      try {
        wait(timeout);
      } catch (InterruptedException e) {
        logger.warn("Interrupted while waiting for ACTIVE to declare WON message ! ", e);
        break;
      }
      timeout = timeout - (System.currentTimeMillis() - start);
    }
    debugInfo("Wait for other active to declare as active over. Declared? activeNodeId.isNull() = "
              + activeNode.isNull() + ", activeNode=" + activeNode);
  }

  // should be called from synchronized code
  private void setActiveNodeID(NodeID nodeID) {
    this.activeNode = nodeID;
    notifyAll();
  }

  private NodeID getLocalNodeID() {
    return groupManager.getLocalNodeID();
  }

  @Override
  public void registerForStateChangeEvents(StateChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void fireStateChangedEvent(StateChangedEvent sce) {
    for (StateChangeListener listener : listeners) {
      listener.l2StateChanged(sce);
    }
  }

  private synchronized void moveToPassiveState(Enrollment winningEnrollment) {
    electionMgr.reset(winningEnrollment);
    if (state == START_STATE) {
      state = PASSIVE_UNINITIALIZED;
      info("Moved to " + state, true);
      fireStateChangedOperatorEvent();
      stateChangeSink.add(new StateChangedEvent(START_STATE, state));
    } else if (state == ACTIVE_COORDINATOR) {
      // TODO:: Support this later
      throw new AssertionError("Cant move to " + PASSIVE_UNINITIALIZED + " from " + ACTIVE_COORDINATOR
                               + " at least for now");
    } else {
      debugInfo("Move to passive state ignored - state=" + state + ", winningEnrollment: " + winningEnrollment);
    }
  }

  @Override
  public synchronized void moveToPassiveStandbyState() {
    if (state == ACTIVE_COORDINATOR) {
      // TODO:: Support this later
      throw new AssertionError("Cant move to " + PASSIVE_STANDBY + " from " + ACTIVE_COORDINATOR + " at least for now");
    } else if (state != PASSIVE_STANDBY) {
      stateChangeSink.add(new StateChangedEvent(state, PASSIVE_STANDBY));
      state = PASSIVE_STANDBY;
      info("Moved to " + state, true);
      fireStateChangedOperatorEvent();
    } else {
      info("Already in " + state);
    }
  }

  private synchronized void moveToActiveState() {
    if (state == START_STATE || state == PASSIVE_STANDBY) {
      // TODO :: If state == START_STATE publish cluster ID
      debugInfo("Moving to active state");
      StateChangedEvent event = new StateChangedEvent(state, ACTIVE_COORDINATOR);
      state = ACTIVE_COORDINATOR;
      setActiveNodeID(getLocalNodeID());
      info("Becoming " + state, true);
      fireStateChangedOperatorEvent();
      electionMgr.declareWinner(this.activeNode);
      stateChangeSink.add(event);
    } else {
      throw new AssertionError("Cant move to " + ACTIVE_COORDINATOR + " from " + state);
    }
  }

  @Override
  public synchronized NodeID getActiveNodeID() {
    return activeNode;
  }

  @Override
  public boolean isActiveCoordinator() {
    return (state == ACTIVE_COORDINATOR);
  }

  public boolean isPassiveUnitialized() {
    return (state == PASSIVE_UNINITIALIZED);
  }

  @Override
  public void moveNodeToPassiveStandby(NodeID nodeID) {
    Assert.assertTrue(isActiveCoordinator());
    logger.info("Requesting node " + nodeID + " to move to " + PASSIVE_STANDBY);
    GroupMessage msg = L2StateMessage.createMoveToPassiveStandbyMessage(EnrollmentFactory
        .createTrumpEnrollment(getLocalNodeID(), weightsFactory));
    try {
      this.groupManager.sendTo(nodeID, msg);
    } catch (GroupException e) {
      logger.error(e);
    }
  }

  @Override
  public void handleClusterStateMessage(L2StateMessage clusterMsg) {
    debugInfo("Received cluster state message: " + clusterMsg);
    try {
      switch (clusterMsg.getType()) {
        case L2StateMessage.START_ELECTION:
          handleStartElectionRequest(clusterMsg);
          break;
        case L2StateMessage.ABORT_ELECTION:
          handleElectionAbort(clusterMsg);
          break;
        case L2StateMessage.ELECTION_RESULT:
          handleElectionResultMessage(clusterMsg);
          break;
        case L2StateMessage.ELECTION_WON:
        case L2StateMessage.ELECTION_WON_ALREADY:
          handleElectionWonMessage(clusterMsg);
          break;
        case L2StateMessage.MOVE_TO_PASSIVE_STANDBY:
          handleMoveToPassiveStandbyMessage(clusterMsg);
          break;
        default:
          throw new AssertionError("This message shouldn't have been routed here : " + clusterMsg);
      }
    } catch (GroupException ge) {
      logger.error("Zapping Node : Caught Exception while handling Message : " + clusterMsg, ge);
      groupManager.zapNode(clusterMsg.messageFrom(), L2HAZapNodeRequestProcessor.COMMUNICATION_ERROR,
                           "Error handling Election Message " + L2HAZapNodeRequestProcessor.getErrorString(ge));
    }
  }

  private void handleMoveToPassiveStandbyMessage(L2StateMessage clusterMsg) {
    moveToPassiveStandbyState();
  }

  private synchronized void handleElectionWonMessage(L2StateMessage clusterMsg) {
    debugInfo("Received election_won or election_already_won msg: " + clusterMsg);
    Enrollment winningEnrollment = clusterMsg.getEnrollment();
    if (state == ACTIVE_COORDINATOR) {
      // Can't get Election Won from another node : Split brain
      String error = state + " Received Election Won Msg : " + clusterMsg
                     + ". A Terracotta server tried to join the mirror group as a second ACTIVE";
      logger.error(error);
      if (clusterMsg.getType() == L2StateMessage.ELECTION_WON_ALREADY) {
        sendNGResponse(clusterMsg.messageFrom(), clusterMsg);
      }
      groupManager.zapNode(winningEnrollment.getNodeID(), L2HAZapNodeRequestProcessor.SPLIT_BRAIN, error);
    } else if (activeNode.isNull() || activeNode.equals(winningEnrollment.getNodeID())
               || clusterMsg.getType() == L2StateMessage.ELECTION_WON) {
      // There is no active server for this node or the other node just detected a failure of ACTIVE server and ran an
      // election and is sending the results. This can happen if this node for some reason is not able to detect that
      // the active is down but the other node did. Go with the new active.
      setActiveNodeID(winningEnrollment.getNodeID());
      moveToPassiveState(winningEnrollment);
      if (clusterMsg.getType() == L2StateMessage.ELECTION_WON_ALREADY) {
        sendOKResponse(clusterMsg.messageFrom(), clusterMsg);
      }
    } else {
      // This is done to solve DEV-1532. Node sent ELECTION_WON_ALREADY message but our ACTIVE is intact.
      logger.warn("Conflicting Election Won  Msg : " + clusterMsg + " since I already have a ACTIVE Node : "
                  + activeNode + ". Sending NG response");
      // The reason we send a response for ELECTION_WON_ALREADY message is that if we don't agree we don't want the
      // other server to send us cluster state messages.
      sendNGResponse(clusterMsg.messageFrom(), clusterMsg);
    }
  }

  private synchronized void handleElectionResultMessage(L2StateMessage msg) throws GroupException {
    if (activeNode.equals(msg.getEnrollment().getNodeID())) {
      Assert.assertFalse(ServerID.NULL_ID.equals(activeNode));
      // This wouldn't normally happen, but we agree - so ack
      GroupMessage resultAgreed = L2StateMessage.createResultAgreedMessage(msg, msg.getEnrollment());
      logger.info("Agreed with Election Result from " + msg.messageFrom() + " : " + resultAgreed);
      groupManager.sendTo(msg.messageFrom(), resultAgreed);
    } else if (state == ACTIVE_COORDINATOR || !activeNode.isNull()
               || (msg.getEnrollment().isANewCandidate() && state != START_STATE)) {
      // Condition 1 :
      // Obviously an issue.
      // Condition 2 :
      // This shouldn't happen normally, but is possible when there is some weird network error where A sees B,
      // B sees A/C and C sees B and A is active and C is trying to run election
      // Force other node to rerun election so that we can abort
      // Condition 3 :
      // We don't want new L2s to win an election when there are old L2s in PASSIVE states.
      GroupMessage resultConflict = L2StateMessage.createResultConflictMessage(msg, EnrollmentFactory
          .createTrumpEnrollment(getLocalNodeID(), weightsFactory));
      warn("WARNING :: Active Node = " + activeNode + " , " + state
           + " received ELECTION_RESULT message from another node : " + msg + " : Forcing re-election "
           + resultConflict);
      groupManager.sendTo(msg.messageFrom(), resultConflict);
    } else {
      debugInfo("ElectionMgr handling election result msg: " + msg);
      electionMgr.handleElectionResultMessage(msg);
    }
  }

  private void handleElectionAbort(L2StateMessage clusterMsg) {
    if (state == ACTIVE_COORDINATOR) {
      // Cant get Abort back to ACTIVE, if so then there is a split brain
      String error = state + " Received Abort Election  Msg : Possible split brain detected ";
      logger.error(error);
      groupManager.zapNode(clusterMsg.messageFrom(), L2HAZapNodeRequestProcessor.SPLIT_BRAIN, error);
    } else {
      debugInfo("ElectionMgr handling election abort");
      electionMgr.handleElectionAbort(clusterMsg);
    }
  }

  private void handleStartElectionRequest(L2StateMessage msg) throws GroupException {
    if (state == ACTIVE_COORDINATOR) {
      // This is either a new L2 joining a cluster or a renegade L2. Force it to abort
      GroupMessage abortMsg = L2StateMessage.createAbortElectionMessage(msg, EnrollmentFactory
          .createTrumpEnrollment(getLocalNodeID(), weightsFactory));
      info("Forcing Abort Election for " + msg + " with " + abortMsg);
      groupManager.sendTo(msg.messageFrom(), abortMsg);
    } else {
      electionMgr.handleStartElectionRequest(msg);
    }
  }

  // notify new node
  @Override
  public void publishActiveState(NodeID nodeID) throws GroupException {
    debugInfo("Publishing active state to nodeId: " + nodeID);
    Assert.assertTrue(isActiveCoordinator());
    GroupMessage msg = L2StateMessage.createElectionWonAlreadyMessage(EnrollmentFactory
        .createTrumpEnrollment(getLocalNodeID(), weightsFactory));
    L2StateMessage response = (L2StateMessage) groupManager.sendToAndWaitForResponse(nodeID, msg);
    validateResponse(nodeID, response);
  }

  private void validateResponse(NodeID nodeID, L2StateMessage response) throws GroupException {
    if (response == null || response.getType() != L2StateMessage.RESULT_AGREED) {
      String error = "Recd wrong response from : " + nodeID + " : msg = " + response + " while publishing Active State";
      logger.error(error);
      // throwing this exception will initiate a zap elsewhere
      throw new GroupException(error);
    }
  }

  @Override
  public void startElectionIfNecessary(NodeID disconnectedNode) {
    Assert.assertFalse(disconnectedNode.equals(getLocalNodeID()));
    boolean elect = false;
    synchronized (this) {
      if (activeNode.equals(disconnectedNode)) {
        // ACTIVE Node is gone
        setActiveNodeID(ServerID.NULL_ID);
      }
      if (state != PASSIVE_UNINITIALIZED && state != ACTIVE_COORDINATOR && activeNode.isNull()) {
        elect = true;
      }
    }
    if (elect) {
      info("Starting Election to determine cluser wide ACTIVE L2");
      startElection();
    } else {
      debugInfo("Not starting election even though node left: " + disconnectedNode);
    }
  }

  private void sendOKResponse(NodeID fromNode, L2StateMessage msg) {
    try {
      groupManager.sendTo(fromNode, L2StateMessage.createResultAgreedMessage(msg, msg.getEnrollment()));
    } catch (GroupException e) {
      logger.error("Error handling message : " + msg, e);
    }
  }

  private void sendNGResponse(NodeID fromNode, L2StateMessage msg) {
    try {
      groupManager.sendTo(fromNode, L2StateMessage.createResultConflictMessage(msg, msg.getEnrollment()));
    } catch (GroupException e) {
      logger.error("Error handling message : " + msg, e);
    }
  }

  @Override
  public String toString() {
    return StateManagerImpl.class.getSimpleName() + ":" + this.state.toString();
  }

  private void fireStateChangedOperatorEvent() {
    TSAManagementEventPayload tsaManagementEventPayload = new TSAManagementEventPayload("TSA.L2.STATE_CHANGE");
    tsaManagementEventPayload.getAttributes().put("State", state.getName());
    TerracottaRemoteManagement.getRemoteManagementInstance().sendEvent(tsaManagementEventPayload.toManagementEvent());
    operatorEventLogger.fireOperatorEvent(TerracottaOperatorEventFactory.createClusterNodeStateChangedEvent(state
        .getName()));
  }

  private void info(String message) {
    info(message, false);
  }

  private void info(String message, boolean console) {
    logger.info(message);
    if (console) {
      consoleLogger.info(message);
    }
  }

  private void warn(String message) {
    warn(message, false);
  }

  private void warn(String message, boolean console) {
    logger.warn(message);
    if (console) {
      consoleLogger.warn(message);
    }
  }

  private static void debugInfo(String message) {
    L2DebugLogging.log(logger, LogLevel.INFO, message, null);
  }
}
