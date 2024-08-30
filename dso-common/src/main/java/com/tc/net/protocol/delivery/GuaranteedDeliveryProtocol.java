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
import com.tc.properties.ReconnectConfig;
import com.tc.util.Assert;
import com.tc.util.Util;

/**
 * This implements an asynchronous Once and only once protocol. Sent messages go out on the sent queue received messages
 * come in to the ProtocolMessageDelivery instance.
 */
class GuaranteedDeliveryProtocol {
  private final SendStateMachine           sender;
  private final ReceiveStateMachine        receiver;

  public GuaranteedDeliveryProtocol(OOOProtocolMessageDelivery delivery, ReconnectConfig reconnectConfig,
                                    boolean isClient) {
    this.sender = new SendStateMachine(delivery, reconnectConfig, isClient);
    this.receiver = new ReceiveStateMachine(delivery, reconnectConfig, isClient);
  }

  public void send(TCNetworkMessage message) {
    boolean interrupted = false;
    try {
      do {
        try {
          sender.put(message);
          break;
        } catch (InterruptedException e) {
          interrupted = true;
        }
      } while (true);

      sender.execute(null);
    } finally {
      Util.selfInterruptIfNeeded(interrupted);
    }
  }

  public void receive(OOOProtocolMessage msg) {
    if (msg.isSend()) {
      // Handle the ACKed sequence from the message.
      sender.execute(msg);

      receiver.execute(msg);
    } else if (msg.isAck() || msg.isHandshakeReplyOk()) {
      sender.execute(msg);
    } else {
      Assert.eval("Unexpected OOO Msg: " + msg, false);
    }
  }

  public synchronized void start() {
    sender.start();
    receiver.start();
  }

  public synchronized void pause() {
    if (!sender.isPaused()) sender.pause();
    if (!receiver.isPaused()) receiver.pause();
  }

  public synchronized boolean isPaused() {
    return (sender.isPaused() && receiver.isPaused());
  }

  public synchronized void resume() {
    sender.resume();
    receiver.resume();
  }

  public synchronized void reset() {
    sender.reset();
    receiver.reset();
  }

  public ReceiveStateMachine getReceiver() {
    return receiver;
  }

  public SendStateMachine getSender() {
    return sender;
  }

  @Override
  public String toString() {
    return "SendStateMachine: " + sender + "; ReceiveStateMachine: " + receiver;
  }
}
