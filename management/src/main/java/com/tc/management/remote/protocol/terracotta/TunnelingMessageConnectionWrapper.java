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
package com.tc.management.remote.protocol.terracotta;

import com.tc.management.JMXAttributeContext;
import com.tc.net.protocol.tcm.MessageChannel;

import java.io.IOException;

import javax.management.remote.message.Message;

public class TunnelingMessageConnectionWrapper extends TunnelingMessageConnection {
  private final RemoteJMXAttributeProcessor jmAttributeProcessor = new RemoteJMXAttributeProcessor();

  public TunnelingMessageConnectionWrapper(MessageChannel channel, boolean isJmxConnectionServer) {
    super(channel, isJmxConnectionServer);
  }

  @Override
  public void writeMessage(Message outboundMessage) throws IOException {
    if (closed.isSet()) { throw new IOException("connection closed"); }
    
    JMXAttributeContext attributeContext = new JMXAttributeContext(this.channel, outboundMessage);
    jmAttributeProcessor.add(attributeContext);
  }

  @Override
  public void close() {
    super.close();
    jmAttributeProcessor.close();
  }
}
