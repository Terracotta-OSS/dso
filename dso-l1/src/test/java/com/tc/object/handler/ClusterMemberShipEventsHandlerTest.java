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
package com.tc.object.handler;

import org.junit.Before;
import org.junit.Test;

import com.tc.license.ProductID;
import com.tc.net.ClientID;
import com.tc.net.NodeID;
import com.tc.object.msg.ClusterMembershipMessage;
import com.tcclient.cluster.DsoClusterInternalEventsGun;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author tim
 */
public class ClusterMemberShipEventsHandlerTest {
  private ClusterMemberShipEventsHandler handler;
  private DsoClusterInternalEventsGun dsoClusterEventsGun;
  private ClientID clientID;

  @Before
  public void setUp() throws Exception {
    clientID = new ClientID(1);
    dsoClusterEventsGun = mock(DsoClusterInternalEventsGun.class);
    handler = new ClusterMemberShipEventsHandler(dsoClusterEventsGun);
  }

  @Test
  public void testInternalNodeJoined() throws Exception {
    handler.handleEvent(joinedMsg(clientID, ProductID.TMS));
    verify(dsoClusterEventsGun, never()).fireNodeJoined(clientID);
  }

  @Test
  public void testInternalNodeLeft() throws Exception {
    handler.handleEvent(leftMsg(clientID, ProductID.WAN));
    verify(dsoClusterEventsGun, never()).fireNodeLeft(clientID);
  }

  @Test
  public void testUserNodeJoined() throws Exception {
    handler.handleEvent(joinedMsg(clientID, ProductID.USER));
    verify(dsoClusterEventsGun).fireNodeJoined(clientID);
  }

  @Test
  public void testUserNodeLeft() throws Exception {
    handler.handleEvent(leftMsg(clientID, ProductID.USER));
    verify(dsoClusterEventsGun).fireNodeLeft(clientID);
  }

  private ClusterMembershipMessage joinedMsg(NodeID clientId, ProductID productID) {
    return msg(ClusterMembershipMessage.EventType.NODE_CONNECTED, clientId, productID);
  }

  private ClusterMembershipMessage leftMsg(NodeID clientId, ProductID productID) {
    return msg(ClusterMembershipMessage.EventType.NODE_DISCONNECTED, clientId, productID);
  }

  private ClusterMembershipMessage msg(int type, NodeID nodeID, ProductID productID) {
    ClusterMembershipMessage membershipMessage = mock(ClusterMembershipMessage.class);
    when(membershipMessage.getProductId()).thenReturn(productID);
    when(membershipMessage.getEventType()).thenReturn(type);
    when(membershipMessage.getNodeId()).thenReturn(nodeID);
    when(membershipMessage.isNodeConnectedEvent()).thenReturn(ClusterMembershipMessage.EventType.isNodeConnected(type));
    when(membershipMessage.isNodeDisconnectedEvent()).thenReturn(ClusterMembershipMessage.EventType.isNodeDisconnected(type));
    return membershipMessage;
  }
}
