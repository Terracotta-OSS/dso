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
package com.tc.object.handler;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyCollection;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;
import com.tc.async.api.Sink;
import com.tc.exception.TCNotRunningException;
import com.tc.net.ClientID;
import com.tc.net.NodeID;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.object.ClientConfigurationContext;
import com.tc.object.ObjectID;
import com.tc.object.context.ServerEventDeliveryContext;
import com.tc.object.dna.api.DNA;
import com.tc.object.gtx.ClientGlobalTransactionManager;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.object.msg.AcknowledgeTransactionMessageFactory;
import com.tc.object.msg.BroadcastTransactionMessage;
import com.tc.object.msg.BroadcastTransactionMessageImpl;
import com.tc.object.session.SessionManager;
import com.tc.object.tx.ClientTransactionManager;
import com.tc.object.tx.TransactionID;
import com.tc.object.tx.TxnType;
import com.tc.server.BasicServerEvent;
import com.tc.server.ServerEvent;
import com.tc.server.ServerEventType;

import java.util.Collections;

/**
 * @author Eugene Shelestovich
 */
public class ReceiveTransactionHandlerTest {

  @Test
  public void testMustSendEventsToDeliveryStage() {
    final Sink deliverySink = mock(Sink.class);

    final ReceiveTransactionHandler handler = new ReceiveTransactionHandler(null, null, null, deliverySink);

    final ServerEvent event1 = new BasicServerEvent(ServerEventType.PUT, "k1", "cache-1");
    final ServerEvent event2 = new BasicServerEvent(ServerEventType.REMOVE, "k1", "cache-1");
    final ServerEvent event3 = new BasicServerEvent(ServerEventType.PUT, "k2", "cache-2");

    final NodeID remoteNodeId = new ClientID(1L);

    final MessageChannel channel = mock(MessageChannel.class);
    when(channel.getRemoteNodeID()).thenReturn(remoteNodeId);

    final BroadcastTransactionMessage transactionMsg = mock(BroadcastTransactionMessage.class);
    when(transactionMsg.getChannel()).thenReturn(channel);
    when(transactionMsg.getEvents()).thenReturn(Lists.newArrayList(event1, event2, event3));

    handler.sendServerEvents(transactionMsg);

    verify(deliverySink).add(eq(new ServerEventDeliveryContext(event1, remoteNodeId)));
    verify(deliverySink).add(eq(new ServerEventDeliveryContext(event2, remoteNodeId)));
    verify(deliverySink).add(eq(new ServerEventDeliveryContext(event3, remoteNodeId)));
  }

  @Test
  public void testAckWhenShuttingDown() throws Exception {
    BroadcastTransactionMessage btm = createBroadcastTransactionMessage();
    ClientConfigurationContext context = mock(ClientConfigurationContext.class);
    ClientTransactionManager clientTransactionManager = mock(ClientTransactionManager.class);
    doThrow(new TCNotRunningException()).when(clientTransactionManager).apply(any(TxnType.class),
      anyList(), anyCollection(), anyMap());
    when(context.getTransactionManager()).thenReturn(clientTransactionManager);

    ClientGlobalTransactionManager clientGlobalTransactionManager = mock(ClientGlobalTransactionManager.class);
    when(
         clientGlobalTransactionManager.startApply(any(NodeID.class), any(TransactionID.class),
                                                   any(GlobalTransactionID.class), any(NodeID.class))).thenReturn(true);

    ReceiveTransactionHandler handler = Mockito
        .spy(new ReceiveTransactionHandler(mock(AcknowledgeTransactionMessageFactory.class),
                                           clientGlobalTransactionManager, mock(SessionManager.class), mock(Sink.class)));
    handler.initialize(context);

    handler.handleEvent(btm);
    verify(handler).sendAck(btm);
  }

  private static BroadcastTransactionMessage createBroadcastTransactionMessage() {
    BroadcastTransactionMessageImpl btm = mock(BroadcastTransactionMessageImpl.class);
    MessageChannel channel = mock(MessageChannel.class);
    when(btm.getLowGlobalTransactionIDWatermark()).thenReturn(GlobalTransactionID.NULL_ID);
    when(btm.getChannel()).thenReturn(channel);
    when(btm.getObjectChanges()).thenReturn(Collections.singleton(mock(DNA.class)));
    when(btm.getNewRoots()).thenReturn(Collections.singletonMap("foo", new ObjectID(1)));
    return btm;
  }
}
