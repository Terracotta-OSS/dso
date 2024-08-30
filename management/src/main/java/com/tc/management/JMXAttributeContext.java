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
package com.tc.management;

import com.tc.async.api.EventContext;
import com.tc.net.protocol.tcm.MessageChannel;

import javax.management.remote.message.Message;

public class JMXAttributeContext implements EventContext {

  private final MessageChannel channel;
  private final Message        outboundMessage;

  public JMXAttributeContext(MessageChannel channel, Message outboundMessage) {
    this.channel = channel;
    this.outboundMessage = outboundMessage;
  }

  public MessageChannel getChannel() {
    return channel;
  }

  public Message getOutboundMessage() {
    return outboundMessage;
  }
}
