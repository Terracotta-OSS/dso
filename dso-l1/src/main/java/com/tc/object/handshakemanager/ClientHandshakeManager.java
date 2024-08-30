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
package com.tc.object.handshakemanager;

import com.tc.net.NodeID;
import com.tc.object.msg.ClientHandshakeAckMessage;

public interface ClientHandshakeManager {

  public void initiateHandshake(NodeID remoteNode);

  public void disconnected(NodeID remoteNode);

  public void connected(NodeID remoteNode);

  public void fireNodeErrorIfNecessary(boolean isRejoinEnabled);

  public void acknowledgeHandshake(ClientHandshakeAckMessage handshakeAck);

  public boolean serverIsPersistent();

  public void waitForHandshake();

  public void reset();

  public void shutdown(boolean fromShutdownHook);

  public boolean isShutdown();
}
