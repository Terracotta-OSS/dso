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

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import com.tc.license.ProductID;
import com.tc.management.RemoteManagement;
import com.tc.management.TerracottaRemoteManagement;
import com.tc.net.ClientID;
import com.tc.net.TCSocketAddress;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.operatorevent.NodeNameProvider;
import com.tc.operatorevent.TerracottaOperatorEvent;
import com.tc.operatorevent.TerracottaOperatorEventLogger;
import com.tc.operatorevent.TerracottaOperatorEventLogging;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author tim
 */
public class ClientChannelOperatorEventlistenerTest {
  @Rule public MockitoRule mockito = MockitoJUnit.rule();

  private TerracottaOperatorEventLogger logger;
  private ClientChannelOperatorEventlistener listener;
  private MockedStatic mockTerracottaOperatorEventLogging;

  @Before
  public void setUp() throws Exception {
    logger = spy(new TerracottaOperatorEventLogger(NodeNameProvider.DEFAULT_NODE_NAME_PROVIDER));
    mockTerracottaOperatorEventLogging = mockStatic(TerracottaOperatorEventLogging.class);
    mockTerracottaOperatorEventLogging.when(TerracottaOperatorEventLogging::getEventLogger).thenReturn(logger);
    listener = new ClientChannelOperatorEventlistener();
    RemoteManagement remoteManagement = mock(RemoteManagement.class);
    TerracottaRemoteManagement.setRemoteManagementInstance(remoteManagement);
  }

  @After
  public void tearDown() {
    TerracottaRemoteManagement.setRemoteManagementInstance(null);
    mockTerracottaOperatorEventLogging.close();
  }

  @Test
  public void testNoOperatorEventForInternalClientJoin() throws Exception {
    listener.channelCreated(messageChannelWithProductID(ProductID.WAN));
    verify(logger, never()).fireOperatorEvent(any(TerracottaOperatorEvent.class));
  }

  @Test
  public void testNoOperatorEventForInternalClientLeave() throws Exception {
    listener.channelRemoved(messageChannelWithProductID(ProductID.TMS));
    verify(logger, never()).fireOperatorEvent(any(TerracottaOperatorEvent.class));
  }

  @Test
  public void testFireJoinedOperatorEventForNormalClient() throws Exception {
    listener.channelCreated(messageChannelWithProductID(ProductID.USER));
    verify(logger).fireOperatorEvent(any(TerracottaOperatorEvent.class));
  }

  @Test
  public void testFireNodeLeftOperatorEventForNormalClient() throws Exception {
    listener.channelRemoved(messageChannelWithProductID(ProductID.USER));
    verify(logger).fireOperatorEvent(any(TerracottaOperatorEvent.class));
  }

  @Test
  public void testNoOperatorEventForReconnectWindowClose() throws Exception {
    MessageChannel channel = messageChannelWithProductID(ProductID.USER);
    when(channel.getRemoteAddress()).thenReturn(null);
    listener.channelRemoved(channel);
    verify(logger, never()).fireOperatorEvent(any(TerracottaOperatorEvent.class));
  }

  private MessageChannel messageChannelWithProductID(ProductID productID) {
    MessageChannel messageChannel = mock(MessageChannel.class);
    when(messageChannel.getProductId()).thenReturn(productID);
    when(messageChannel.getRemoteNodeID()).thenReturn(new ClientID(1));
    when(messageChannel.getRemoteAddress()).thenReturn(new TCSocketAddress(0));
    return messageChannel;
  }
}
