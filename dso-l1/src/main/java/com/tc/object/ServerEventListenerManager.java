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
package com.tc.object;

import com.tc.net.NodeID;
import com.tc.object.handshakemanager.ClientHandshakeCallback;
import com.tc.server.ServerEvent;
import com.tc.server.ServerEventType;

import java.util.Set;

/**
 * Manages subscriptions and delivery of server events for L1 clients.
 *
 * @author Eugene Shelestovich
 */
public interface ServerEventListenerManager extends ClientHandshakeCallback {

  void registerListener(ServerEventDestination destination, Set<ServerEventType> listenTo);

  void unregisterListener(ServerEventDestination destination, final Set<ServerEventType> listenTo);

  void dispatch(ServerEvent event, NodeID remoteNode);
}
