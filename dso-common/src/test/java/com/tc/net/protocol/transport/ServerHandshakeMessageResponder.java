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
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import junit.framework.Assert;

class ServerHandshakeMessageResponder extends HandshakeMessageResponderBase {

  private final BlockingQueue<String> synAckErrors = new LinkedBlockingQueue<String>();

  protected ServerHandshakeMessageResponder(BlockingQueue<TransportHandshakeMessage> sentQueue,
                                            BlockingQueue<TransportHandshakeMessage> receivedQueue,
                                            TransportHandshakeMessageFactory messageFactory,
                                            ConnectionID assignedConnectionId, MessageTransportBase transport,
                                            AtomicReference<Throwable> errorRef) {
    super(sentQueue, receivedQueue, messageFactory, assignedConnectionId, transport, errorRef);
  }

  @Override
  public void handleHandshakeMessage(final TransportHandshakeMessage message) {
    try {
      if (message.isSynAck()) {
        Assert.assertNotNull(message.getConnectionId());
        Assert.assertEquals(this.assignedConnectionId, message.getConnectionId());
        final SynAckMessage synAck = (SynAckMessage)message;
        if (synAck.hasErrorContext()) {
          this.synAckErrors.put(synAck.getErrorContext());
        } else {
          TransportHandshakeMessage ack = messageFactory.createAck(this.assignedConnectionId, synAck.getSource());
          this.sendResponseMessage(ack);
        }
      } else {
        Assert.fail("Recieved an unexpected message type: " + message);
      }
    } catch (Exception e) {
      setError(e);
    }
  }

  public boolean wasSynAckReceived(long timeout) throws Exception {
    TransportHandshakeMessage message = this.receivedQueue.poll(timeout, TimeUnit.MILLISECONDS);
    return message != null && message.isSynAck();
  }

  public String waitForSynAckErrorToBeReceived(long timeout) throws InterruptedException {
    return this.synAckErrors.poll(timeout, TimeUnit.MILLISECONDS);
  }

}
