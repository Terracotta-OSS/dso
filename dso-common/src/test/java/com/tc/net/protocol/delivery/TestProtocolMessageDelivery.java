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

import com.tc.net.protocol.TCNetworkMessage;
import com.tc.net.protocol.transport.ConnectionID;
import com.tc.util.Assert;

import java.util.concurrent.BlockingQueue;

/**
 *
 */
public class TestProtocolMessageDelivery implements OOOProtocolMessageDelivery {
  public boolean            sentAckRequest = false;
  public long               ackCount       = -1;
  public boolean            sentAck        = false;
  public OOOProtocolMessage msg            = null;
  public TCNetworkMessage   tcMessage      = null;
  public boolean            created        = false;
  public int                receivedMessageCount;
  private final BlockingQueue<TCNetworkMessage> receivedQueue;

  public TestProtocolMessageDelivery(BlockingQueue<TCNetworkMessage> receivedQueue) {
    this.receivedQueue = receivedQueue;
  }

  @Override
  public OOOProtocolMessage createHandshakeMessage(long ack) {
    sentAckRequest = true;
    return (new TestProtocolMessage());
  }

  public void sendAckRequest() {
    sentAckRequest = true;
  }

  @Override
  public OOOProtocolMessage createAckMessage(long sequence) {
    if (sequence > ackCount) {
      this.ackCount = sequence;
      this.sentAck = true;
    }

    TestProtocolMessage opm = new TestProtocolMessage(null, 0, sequence);
    opm.isAck = true;
    return (opm);
  }

  public void sendAck(long count) {
    this.ackCount = count;
    this.sentAck = true;
  }

  @Override
  public boolean sendMessage(OOOProtocolMessage pmsg) {
    this.msg = pmsg;
    return (true);
  }

  @Override
  public OOOProtocolMessage createProtocolMessage(long sent, TCNetworkMessage tcmsg) {
    Assert.eval(sent >= 0);
    this.tcMessage = tcmsg;
    this.created = true;
    TestProtocolMessage tpm = new TestProtocolMessage(tcmsg, sent, ackCount);
    tpm.isSend = true;
    return tpm;
  }

  public void clearAll() {
    sentAck = false;
    sentAckRequest = false;
    ackCount = -1;
    msg = null;
    tcMessage = null;
    created = false;
  }

  // clears all except ackCount
  public void clear() {
    sentAck = false;
    sentAckRequest = false;
    msg = null;
    tcMessage = null;
    created = false;
  }

  @Override
  public void receiveMessage(OOOProtocolMessage pm) {
    receivedMessageCount++;
    try {
      receivedQueue.put(((TestProtocolMessage) pm).msg);
    } catch (InterruptedException e) {
      e.printStackTrace();
      junit.framework.Assert.fail("yikes! " + e);
    }

  }

  @Override
  public ConnectionID getConnectionId() {
    return null;
  }

  @Override
  public OOOProtocolMessage createHandshakeReplyOkMessage(long sequence) {
    return null;
  }

  @Override
  public OOOProtocolMessage createHandshakeReplyFailMessage(long sequence) {
    return null;
  }

}
