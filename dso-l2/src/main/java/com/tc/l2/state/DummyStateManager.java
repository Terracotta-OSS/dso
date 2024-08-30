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
package com.tc.l2.state;

import com.tc.l2.context.StateChangedEvent;
import com.tc.l2.msg.L2StateMessage;
import com.tc.net.NodeID;
import com.tc.util.State;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class DummyStateManager implements StateManager {

  private final List<StateChangeListener> stateListeners = new CopyOnWriteArrayList<StateChangeListener>();
  private final NodeID                    localNodeID;

  public DummyStateManager(NodeID localNodeID) {
    this.localNodeID = localNodeID;
  }

  @Override
  public void fireStateChangedEvent(StateChangedEvent sce) {
    for (StateChangeListener element : stateListeners) {
      StateChangeListener listener = element;
      listener.l2StateChanged(sce);
    }
  }

  @Override
  public State getCurrentState() {
    return new State("NO_STATE");
  }

  @Override
  public boolean isActiveCoordinator() {
    return true;
  }

  public boolean isPassiveUnitialized() {
    return false;
  }

  @Override
  public void moveNodeToPassiveStandby(NodeID nodeID) {
    throw new UnsupportedOperationException();
  }


  @Override
  public void registerForStateChangeEvents(StateChangeListener listener) {
    stateListeners.add(listener);
  }

  @Override
  public void startElection() {
    // No need to start election, if we are here, we are active, notify it.
    fireStateChangedEvent(new StateChangedEvent(StateManager.PASSIVE_STANDBY, StateManager.ACTIVE_COORDINATOR));
  }

  @Override
  public void publishActiveState(NodeID nodeID) {
    // Nop
  }

  @Override
  public void startElectionIfNecessary(NodeID disconnectedNode) {
    // Nop
  }

  @Override
  public void moveToPassiveStandbyState() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void handleClusterStateMessage(L2StateMessage clusterMsg) {
    throw new UnsupportedOperationException();
  }

  @Override
  public NodeID getActiveNodeID() {
    return localNodeID;
  }

}
