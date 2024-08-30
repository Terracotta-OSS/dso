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
import com.tc.net.groups.GroupException;
import com.tc.util.State;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public interface StateManager {

  public static final State       ACTIVE_COORDINATOR   = new State("ACTIVE-COORDINATOR");
  public static final State       RECOVERING           = new State("RECOVERING");
  public static final State       PASSIVE_UNINITIALIZED = new State("PASSIVE-UNINITIALIZED");
  public static final State       PASSIVE_STANDBY      = new State("PASSIVE-STANDBY");
  public static final State       START_STATE          = new State("START-STATE");
  public static final State       STOP_STATE           = new State("STOP-STATE");
  public static final State       SAFE_MODE_STATE      = new State("SAFE-MODE-STATE");
  public static final List<State> validStates          = Collections.unmodifiableList(Arrays
                                                           .asList(START_STATE, PASSIVE_UNINITIALIZED, PASSIVE_STANDBY,
                                                                   ACTIVE_COORDINATOR, STOP_STATE, RECOVERING, SAFE_MODE_STATE));

  public void startElection();

  public State getCurrentState();

  public void startElectionIfNecessary(NodeID disconnectedNode);

  public void registerForStateChangeEvents(StateChangeListener listener);

  public void fireStateChangedEvent(StateChangedEvent sce);

  public boolean isActiveCoordinator();

  public void moveNodeToPassiveStandby(NodeID nodeID);

  public void moveToPassiveStandbyState();

  public void publishActiveState(NodeID nodeID) throws GroupException;

  public void handleClusterStateMessage(L2StateMessage clusterMsg);

  public NodeID getActiveNodeID();
}
