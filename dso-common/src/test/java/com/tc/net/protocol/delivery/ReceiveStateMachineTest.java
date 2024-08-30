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
import com.tc.net.protocol.tcm.NullMessageMonitor;
import com.tc.net.protocol.tcm.msgs.PingMessage;
import com.tc.properties.L1ReconnectConfigImpl;
import com.tc.test.TCTestCase;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Testing the basic functionality of OOO Receive State Machine. More functional test at GuaranteedDeliveryProtocolTest
 */

public class ReceiveStateMachineTest extends TCTestCase {

  public void tests() throws Exception {
    BlockingQueue<TCNetworkMessage> receiveQueue = new LinkedBlockingQueue<TCNetworkMessage>();
    TestProtocolMessageDelivery delivery = new TestProtocolMessageDelivery(receiveQueue);
    ReceiveStateMachine rsm = new ReceiveStateMachine(delivery, new L1ReconnectConfigImpl(), true);
    TestProtocolMessage tpm = new TestProtocolMessage();
    tpm.isHandshake = true;

    rsm.start();

    tpm.msg = new PingMessage(new NullMessageMonitor());
    tpm.sent = 0;
    tpm.isSend = true;

    assertEquals(0, delivery.receivedMessageCount);
    // REceive message
    rsm.execute(tpm);
    int received = delivery.receivedMessageCount;
    assertTrue(delivery.receivedMessageCount > 0);
    assertTrue(receiveQueue.poll() != null);

    // Receive a second time
    rsm.execute(tpm);
    assertEquals(received, delivery.receivedMessageCount);
    assertTrue(receiveQueue.poll() == null);
  }
}
