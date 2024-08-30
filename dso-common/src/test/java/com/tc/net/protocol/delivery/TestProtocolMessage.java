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
import com.tc.exception.ImplementMe;
import com.tc.net.protocol.TCNetworkHeader;
import com.tc.net.protocol.TCNetworkMessage;
import com.tc.util.UUID;

/**
 * 
 */
public class TestProtocolMessage implements OOOProtocolMessage {
  public TCNetworkMessage msg;
  public long             sent;
  public long             ack;
  public boolean          isHandshake          = false;
  public boolean          isHandshakeReplyOk   = false;
  public boolean          isHandshakeReplyFail = false;
  public boolean          isSend               = false;
  public boolean          isAck                = false;
  private boolean         isGoodbye            = false;
  public UUID             sessionId            = UUID.getUUID();

  public TestProtocolMessage(TCNetworkMessage msg, long sent, long ack) {
    this.msg = msg;
    this.sent = sent;
    this.ack = ack;
  }

  public TestProtocolMessage() {
    //
  }

  @Override
  public long getAckSequence() {
    return ack;
  }

  @Override
  public long getSent() {
    return sent;
  }

  @Override
  public boolean isHandshake() {
    return isHandshake;
  }

  @Override
  public boolean isHandshakeReplyOk() {
    return isHandshakeReplyOk;
  }

  @Override
  public boolean isHandshakeReplyFail() {
    return isHandshakeReplyFail;
  }

  @Override
  public boolean isSend() {
    return isSend;
  }

  @Override
  public boolean isAck() {
    return isAck;
  }

  @Override
  public UUID getSessionId() {
    return (sessionId);
  }

  public void setSessionId(UUID id) {
    sessionId = id;
  }

  /*********************************************************************************************************************
   * TCNetworkMessage stuff
   */

  @Override
  public TCNetworkHeader getHeader() {
    throw new ImplementMe();
  }

  @Override
  public TCNetworkMessage getMessagePayload() {
    throw new ImplementMe();
  }

  @Override
  public TCByteBuffer[] getPayload() {
    throw new ImplementMe();
  }

  @Override
  public TCByteBuffer[] getEntireMessageData() {
    throw new ImplementMe();
  }

  @Override
  public boolean isSealed() {
    throw new ImplementMe();
  }

  @Override
  public void seal() {
    throw new ImplementMe();
  }

  @Override
  public int getDataLength() {
    throw new ImplementMe();
  }

  @Override
  public int getHeaderLength() {
    throw new ImplementMe();
  }

  @Override
  public int getTotalLength() {
    throw new ImplementMe();
  }

  @Override
  public void wasSent() {
    throw new ImplementMe();
  }

  @Override
  public void setSentCallback(Runnable callback) {
    throw new ImplementMe();
  }

  @Override
  public Runnable getSentCallback() {
    throw new ImplementMe();
  }

  @Override
  public void recycle() {
    throw new ImplementMe();
  }

  @Override
  public boolean isGoodbye() {
    return isGoodbye;
  }

  @Override
  public void reallyDoRecycleOnWrite() {
    //
  }
}
