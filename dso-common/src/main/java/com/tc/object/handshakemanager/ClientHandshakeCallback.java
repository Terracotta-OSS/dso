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
package com.tc.object.handshakemanager;

import com.tc.net.NodeID;
import com.tc.object.ClearableCallback;
import com.tc.object.msg.ClientHandshakeMessage;

public interface ClientHandshakeCallback extends ClearableCallback {

  /**
   * Pauses this callback, should be UnInterruptable.
   */
  public void pause(NodeID remoteNode, int disconnected);

  public void unpause(NodeID remoteNode, int disconnected);

  public void initializeHandshake(NodeID thisNode, NodeID remoteNode, ClientHandshakeMessage handshakeMessage);

  public void shutdown(boolean fromShutdownHook);

}
