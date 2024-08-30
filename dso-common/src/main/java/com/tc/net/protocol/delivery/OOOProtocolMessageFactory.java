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

import com.tc.bytes.TCByteBuffer;
import com.tc.net.protocol.TCNetworkMessage;
import com.tc.util.UUID;

public class OOOProtocolMessageFactory {

  public OOOProtocolMessage createNewHandshakeMessage(UUID sessionId, long ack) {
    return new OOOProtocolMessageImpl(new OOOProtocolMessageHeader(OOOProtocolMessageHeader.VERSION,
                                                                   OOOProtocolMessageHeader.TYPE_HANDSHAKE, ack,
                                                                   sessionId));
  }

  public OOOProtocolMessage createNewAckMessage(UUID sessionId, long ackSequence) {
    return new OOOProtocolMessageImpl(new OOOProtocolMessageHeader(OOOProtocolMessageHeader.VERSION,
                                                                   OOOProtocolMessageHeader.TYPE_ACK, 0, ackSequence,
                                                                   sessionId));
  }

  public OOOProtocolMessage createNewSendMessage(UUID sessionId, long sequence, long ackSequence,
                                                 TCNetworkMessage payload) {
    return new OOOProtocolMessageImpl(new OOOProtocolMessageHeader(OOOProtocolMessageHeader.VERSION,
                                                                   OOOProtocolMessageHeader.TYPE_SEND, sequence, ackSequence,
                                                                   sessionId), payload);
  }

  public OOOProtocolMessage createNewMessage(OOOProtocolMessageHeader header, TCByteBuffer[] data) {
    return new OOOProtocolMessageImpl(header, data);
  }

  public OOOProtocolMessage createNewGoodbyeMessage(UUID sessionId) {
    return new OOOProtocolMessageImpl(new OOOProtocolMessageHeader(OOOProtocolMessageHeader.VERSION,
                                                                   OOOProtocolMessageHeader.TYPE_GOODBYE, 0, sessionId));
  }

  public OOOProtocolMessage createNewHandshakeReplyOkMessage(UUID sessionId, long sequence) {
    return new OOOProtocolMessageImpl(new OOOProtocolMessageHeader(OOOProtocolMessageHeader.VERSION,
                                                                   OOOProtocolMessageHeader.TYPE_HANDSHAKE_REPLY_OK,
                                                                   sequence, sessionId));
  }

  public OOOProtocolMessage createNewHandshakeReplyFailMessage(UUID sessionId, long sequence) {
    return new OOOProtocolMessageImpl(new OOOProtocolMessageHeader(OOOProtocolMessageHeader.VERSION,
                                                                   OOOProtocolMessageHeader.TYPE_HANDSHAKE_REPLY_FAIL,
                                                                   sequence, sessionId));
  }
}
