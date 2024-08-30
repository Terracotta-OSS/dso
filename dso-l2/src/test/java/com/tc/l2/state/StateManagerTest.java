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

import com.tc.async.api.Sink;
import com.tc.l2.ha.WeightGeneratorFactory;
import com.tc.logging.TCLogging;
import com.tc.net.groups.GroupManager;
import com.tc.net.groups.GroupMessage;
import com.tc.objectserver.persistence.ClusterStatePersistor;
import com.tc.objectserver.persistence.TestClusterStatePersistor;
import com.tc.test.TCTestCase;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

/**
 * @author tim
 */
public class StateManagerTest extends TCTestCase {
  private GroupManager groupManager;
  private StateManager stateManager;
  private StateManagerConfig stateManagerConfig;
  private Sink stateChangeSink;
  private WeightGeneratorFactory weightGeneratorFactory;
  private ClusterStatePersistor clusterStatePersistor;

  @Override
  public void setUp() throws Exception {
    groupManager = mock(GroupManager.class);
    stateManagerConfig = new StateManagerConfigImpl(5);
    stateChangeSink = mock(Sink.class);
    weightGeneratorFactory = spy(new WeightGeneratorFactory());
    clusterStatePersistor = new TestClusterStatePersistor();
    stateManager = new StateManagerImpl(TCLogging.getLogger(getClass()), groupManager,
        stateChangeSink, stateManagerConfig, weightGeneratorFactory, clusterStatePersistor, null);
  }

  public void testSkipElectionWhenRecoveredPassive() throws Exception {
    Map<String, String> clusterStateMap = new HashMap<String, String>();
    // Simulate going down as PASSIVE_STANDBY, by restarting the clusterStatePersistor and stateManager.
    new TestClusterStatePersistor(clusterStateMap).setCurrentL2State(StateManager.PASSIVE_STANDBY);
    clusterStatePersistor = new TestClusterStatePersistor(clusterStateMap);
    stateManager = new StateManagerImpl(TCLogging.getLogger(getClass()), groupManager,
        stateChangeSink, stateManagerConfig, weightGeneratorFactory, clusterStatePersistor, null);
    stateManager.startElection();
    verifyElectionDidNotStart();
  }

  public void testSkipElectionWhenRecoveredUnitialized() throws Exception {
    Map<String, String> clusterStateMap = new HashMap<String, String>();
    new TestClusterStatePersistor(clusterStateMap).setCurrentL2State(StateManager.PASSIVE_UNINITIALIZED);
    clusterStatePersistor = new TestClusterStatePersistor(clusterStateMap);
    stateManager = new StateManagerImpl(TCLogging.getLogger(getClass()), groupManager,
        stateChangeSink, stateManagerConfig, weightGeneratorFactory, clusterStatePersistor, null);
    stateManager.startElection();
    verifyElectionDidNotStart();
  }

  private void verifyElectionDidNotStart() {
    verify(groupManager, never()).sendAll(any(GroupMessage.class));
  }
}
