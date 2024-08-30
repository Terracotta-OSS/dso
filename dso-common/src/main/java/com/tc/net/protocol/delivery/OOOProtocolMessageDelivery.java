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
package com.tc.net.protocol.delivery;

import com.tc.net.protocol.TCNetworkMessage;
import com.tc.net.protocol.transport.ConnectionID;

public interface OOOProtocolMessageDelivery {

  public OOOProtocolMessage createHandshakeMessage(long ack);

  public OOOProtocolMessage createHandshakeReplyOkMessage(long ack);

  public OOOProtocolMessage createHandshakeReplyFailMessage(long ack);

  public OOOProtocolMessage createAckMessage(long sequence);

  public boolean sendMessage(OOOProtocolMessage msg);

  public void receiveMessage(OOOProtocolMessage msg);

  public OOOProtocolMessage createProtocolMessage(long sent, TCNetworkMessage msg);

  public ConnectionID getConnectionId();

}
