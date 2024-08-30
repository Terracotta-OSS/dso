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
package com.tc.net.protocol.transport;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

class ClientHandshakeMessageResponder extends HandshakeMessageResponderBase {

  protected ClientHandshakeMessageResponder(BlockingQueue<TransportHandshakeMessage> sentQueue,
                                            BlockingQueue<TransportHandshakeMessage> receivedQueue,
                                            TransportHandshakeMessageFactory messageFactory,
                                            ConnectionID assignedConnectionId, MessageTransportBase transport,
                                            AtomicReference<Throwable> errorRef) {
    super(sentQueue, receivedQueue, messageFactory, assignedConnectionId, transport, errorRef);
  }

  @Override
  public void handleHandshakeMessage(TransportHandshakeMessage message) {
    if (message.isSyn()) {

      Assert.assertNotNull(message.getConnectionId());
      sendResponseMessage(messageFactory.createSynAck(this.assignedConnectionId, message.getSource(), false, -1,
                                                      TransportHandshakeMessage.NO_CALLBACK_PORT));
    } else if (message.isAck()) {
      // nothing to do.
    } else {
      Assert.fail("Bogus message received: " + message);
    }
  }

  public boolean waitForAckToBeReceived(long timeout) throws InterruptedException {
    TransportHandshakeMessage handshake;
    do {
      handshake = receivedQueue.poll(timeout, TimeUnit.MILLISECONDS);
      if (handshake == null) return false;
    } while (!(handshake.isAck()));
    return true;
  }

  public boolean waitForSynAckToBeSent(long timeout) throws InterruptedException {
    TransportHandshakeMessage handshake;
    do {
      handshake = sentQueue.poll(timeout, TimeUnit.MILLISECONDS);
      if (handshake == null) return false;
    } while (!handshake.isSynAck());
    return true;
  }
}
