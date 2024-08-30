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

import org.junit.Test;

import com.tc.net.GroupID;
import com.tc.net.NodeID;
import com.tc.object.ServerEventListenerManager;
import com.tc.object.context.ServerEventDeliveryContext;
import com.tc.server.BasicServerEvent;
import com.tc.server.ServerEvent;
import com.tc.server.ServerEventType;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Eugene Shelestovich
 */
public class ServerEventDeliveryHandlerTest {

  @Test
  public void testMustForwardEventToManager() {
    final ServerEvent event = new BasicServerEvent(ServerEventType.PUT, "key1", "test-cache");
    final NodeID remoteNode = new GroupID(0);
    final ServerEventListenerManager manager = mock(ServerEventListenerManager.class);

    final ServerEventDeliveryHandler handler = new ServerEventDeliveryHandler(manager);
    handler.handleEvent(new ServerEventDeliveryContext(event, remoteNode));

    verify(manager).dispatch(event, remoteNode);
    verifyNoMoreInteractions(manager);
  }
}
