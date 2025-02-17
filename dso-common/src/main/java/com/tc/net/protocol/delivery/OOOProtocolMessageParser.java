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
package com.tc.net.protocol.delivery;

import com.tc.bytes.TCByteBuffer;
import com.tc.net.protocol.TCProtocolException;
import com.tc.util.Assert;

/**
 * Parses incoming network data into ProtocolMessages
 */
class OOOProtocolMessageParser {
  private final OOOProtocolMessageFactory messageFactory;

  public OOOProtocolMessageParser(OOOProtocolMessageFactory messageFactory) {
    this.messageFactory = messageFactory;
  }

  public OOOProtocolMessage parseMessage(TCByteBuffer[] data) throws TCProtocolException {
    int hdrLength = OOOProtocolMessageHeader.HEADER_LENGTH;
    if (hdrLength > data[0].limit()) { throw new TCProtocolException("header not contained in first buffer: "
                                                                     + hdrLength + " > " + data[0].limit()); }

    OOOProtocolMessageHeader header = new OOOProtocolMessageHeader(data[0].duplicate()
        .limit(OOOProtocolMessageHeader.HEADER_LENGTH));
    header.validate();

    TCByteBuffer msgData[];
    if (header.getHeaderByteLength() < data[0].limit()) {
      msgData = new TCByteBuffer[data.length];
      System.arraycopy(data, 0, msgData, 0, msgData.length);

      TCByteBuffer firstPayloadBuffer = msgData[0].duplicate();
      firstPayloadBuffer.position(header.getHeaderByteLength());
      msgData[0] = firstPayloadBuffer.slice();
    } else {
      Assert.eval(data.length >= 1);
      msgData = new TCByteBuffer[data.length - 1];
      System.arraycopy(data, 1, msgData, 0, msgData.length);
    }

    return messageFactory.createNewMessage(header, msgData);
  }

}
