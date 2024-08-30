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
package com.tc.l2.operatorevent;

import com.tc.config.NodesStore;
import com.tc.management.TSAManagementEventPayload;
import com.tc.management.TerracottaRemoteManagement;
import com.tc.net.NodeID;
import com.tc.net.ServerID;
import com.tc.net.groups.GroupEventsListener;
import com.tc.operatorevent.TerracottaOperatorEventFactory;
import com.tc.operatorevent.TerracottaOperatorEventLogger;
import com.tc.operatorevent.TerracottaOperatorEventLogging;
import com.tc.util.Assert;

public class OperatorEventsNodeConnectionListener implements GroupEventsListener {

  private final TerracottaOperatorEventLogger operatorEventLogger = TerracottaOperatorEventLogging.getEventLogger();
  private final NodesStore                    nodesStore;

  public OperatorEventsNodeConnectionListener(NodesStore nodesStore) {
    this.nodesStore = nodesStore;
  }

  @Override
  public void nodeJoined(NodeID nodeID) {
    Assert.assertTrue(nodeID instanceof ServerID);
    String serverName = nodesStore.getServerNameFromNodeName(((ServerID) nodeID).getName());
    Assert.assertNotNull(serverName);
    TSAManagementEventPayload tsaManagementEventPayload = new TSAManagementEventPayload("TSA.TOPOLOGY.NODE_JOINED");
    tsaManagementEventPayload.getAttributes().put("Server.Name", serverName);
    TerracottaRemoteManagement.getRemoteManagementInstance().sendEvent(tsaManagementEventPayload.toManagementEvent());
    operatorEventLogger.fireOperatorEvent(TerracottaOperatorEventFactory.createNodeConnectedEvent(serverName));
  }

  @Override
  public void nodeLeft(NodeID nodeID) {
    Assert.assertTrue(nodeID instanceof ServerID);
    String serverName = nodesStore.getServerNameFromNodeName(((ServerID) nodeID).getName());
    Assert.assertNotNull(serverName);
    TSAManagementEventPayload tsaManagementEventPayload = new TSAManagementEventPayload("TSA.TOPOLOGY.NODE_LEFT");
    tsaManagementEventPayload.getAttributes().put("Server.Name", serverName);
    TerracottaRemoteManagement.getRemoteManagementInstance().sendEvent(tsaManagementEventPayload.toManagementEvent());
    operatorEventLogger.fireOperatorEvent(TerracottaOperatorEventFactory.createNodeDisconnectedEvent(serverName));
  }

}
