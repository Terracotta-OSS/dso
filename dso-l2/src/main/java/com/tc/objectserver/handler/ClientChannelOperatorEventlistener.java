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

import com.tc.management.TSAManagementEventPayload;
import com.tc.management.TerracottaManagement;
import com.tc.management.TerracottaRemoteManagement;
import com.tc.net.ClientID;
import com.tc.net.NodeID;
import com.tc.net.TCSocketAddress;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.object.net.DSOChannelManagerEventListener;
import com.tc.operatorevent.TerracottaOperatorEventFactory;
import com.tc.operatorevent.TerracottaOperatorEventLogger;
import com.tc.operatorevent.TerracottaOperatorEventLogging;

public class ClientChannelOperatorEventlistener implements DSOChannelManagerEventListener {

  private final TerracottaOperatorEventLogger operatorEventLogger = TerracottaOperatorEventLogging.getEventLogger();

  @Override
  public void channelCreated(MessageChannel channel) {
    // Don't generate operator events for internal products
    if (!channel.getProductId().isInternal()) {
      NodeID remoteNodeID = channel.getRemoteNodeID();
      ClientID clientID = (ClientID)remoteNodeID;
      TCSocketAddress remoteAddress = channel.getRemoteAddress();
      String jmxId = TerracottaManagement.buildNodeId(remoteAddress);

      TSAManagementEventPayload tsaManagementEventPayload = new TSAManagementEventPayload("TSA.TOPOLOGY.L1.JOINED");
      tsaManagementEventPayload.getAttributes().put("Client.NodeID", Long.toString(clientID.toLong()));
      tsaManagementEventPayload.getAttributes().put("Client.JmxID", jmxId);
      tsaManagementEventPayload.getAttributes().put("Client.RemoteAddress", remoteAddress.getCanonicalStringForm());

      TerracottaRemoteManagement.getRemoteManagementInstance().sendEvent(tsaManagementEventPayload.toManagementEvent());
      operatorEventLogger.fireOperatorEvent(TerracottaOperatorEventFactory.createNodeConnectedEvent(remoteNodeID.toString()));
    }
  }

  @Override
  public void channelRemoved(MessageChannel channel) {
    // Don't generate operator events for internal products
    // Also, don't generate events for clients being removed as a result of the reconnect window closing (channel remote address is null),
    // they weren't actually connected to begin with
    if (!channel.getProductId().isInternal() && channel.getRemoteAddress() != null) {
      NodeID remoteNodeID = channel.getRemoteNodeID();
      ClientID clientID = (ClientID)remoteNodeID;
      TCSocketAddress remoteAddress = channel.getRemoteAddress();
      String jmxId = TerracottaManagement.buildNodeId(remoteAddress);

      TSAManagementEventPayload tsaManagementEventPayload = new TSAManagementEventPayload("TSA.TOPOLOGY.L1.LEFT");
      tsaManagementEventPayload.getAttributes().put("Client.NodeID", Long.toString(clientID.toLong()));
      tsaManagementEventPayload.getAttributes().put("Client.JmxID", jmxId);
      tsaManagementEventPayload.getAttributes().put("Client.RemoteAddress", remoteAddress.getCanonicalStringForm());

      TerracottaRemoteManagement.getRemoteManagementInstance().sendEvent(tsaManagementEventPayload.toManagementEvent());
      operatorEventLogger.fireOperatorEvent(TerracottaOperatorEventFactory.createNodeDisconnectedEvent(remoteNodeID.toString()));
    }
  }

}
