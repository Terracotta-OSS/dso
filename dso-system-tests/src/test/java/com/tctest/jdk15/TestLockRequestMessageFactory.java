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
package com.tctest.jdk15;

import com.tc.io.TCByteBufferOutputStream;
import com.tc.net.NodeID;
import com.tc.net.protocol.tcm.ChannelID;
import com.tc.net.protocol.tcm.NullMessageMonitor;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.net.protocol.tcm.TestMessageChannel;
import com.tc.object.msg.LockRequestMessage;
import com.tc.object.msg.LockRequestMessageFactory;
import com.tc.object.session.SessionID;

public class TestLockRequestMessageFactory implements LockRequestMessageFactory {

  @Override
  public LockRequestMessage newLockRequestMessage(final NodeID nodeId) {
    TestMessageChannel channel = new TestMessageChannel();
    channel.channelID = new ChannelID(100);
    return new LockRequestMessage(new SessionID(100), new NullMessageMonitor(), new TCByteBufferOutputStream(),
                                  channel, TCMessageType.LOCK_REQUEST_MESSAGE);
  }

}
