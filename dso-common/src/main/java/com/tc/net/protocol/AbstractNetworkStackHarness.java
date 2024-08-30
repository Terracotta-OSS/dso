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
package com.tc.net.protocol;

import com.tc.net.core.TCConnection;
import com.tc.net.protocol.tcm.ChannelID;
import com.tc.net.protocol.tcm.MessageChannelInternal;
import com.tc.net.protocol.tcm.ServerMessageChannelFactory;
import com.tc.net.protocol.transport.MessageTransport;
import com.tc.net.protocol.transport.MessageTransportFactory;
import com.tc.util.Assert;
import com.tc.util.concurrent.SetOnceFlag;

public abstract class AbstractNetworkStackHarness implements NetworkStackHarness {
  protected MessageTransport                transport;
  protected MessageChannelInternal          channel;
  private final ServerMessageChannelFactory channelFactory;
  private final MessageTransportFactory     transportFactory;
  private final boolean                     isClientStack;
  private final SetOnceFlag                 finalized = new SetOnceFlag();

  protected AbstractNetworkStackHarness(ServerMessageChannelFactory channelFactory, MessageTransport transport) {
    this.channelFactory = channelFactory;
    this.transportFactory = null;
    this.transport = transport;
    this.isClientStack = false;
  }

  protected AbstractNetworkStackHarness(MessageTransportFactory transportFactory, MessageChannelInternal channel) {
    this.transportFactory = transportFactory;
    this.channelFactory = null;
    this.channel = channel;
    this.isClientStack = true;
  }

  /**
   * Connects a new transport to an existing stack (server-side).
   */
  @Override
  public final MessageTransport attachNewConnection(TCConnection connection) throws IllegalReconnectException {
    Assert.eval("Attempt to connect a transport to a stack that hasn't been finalized.", finalized.isSet());
    this.transport.attachNewConnection(connection);
    return this.transport;
  }

  /**
   * Creates and connects a new stack.
   */
  @Override
  public final void finalizeStack() {
    if (finalized.attemptSet()) {
      if (isClientStack) {
        Assert.assertNotNull(this.channel);
        Assert.assertNotNull(this.transportFactory);
        this.transport = transportFactory.createNewTransport();
      } else {
        Assert.assertNotNull(this.transport);
        Assert.assertNotNull(this.channelFactory);
        this.channel = channelFactory.createNewChannel(new ChannelID(this.transport.getConnectionId().getChannelID()),
            transport.getConnectionId().getProductId());
      }
      createIntermediateLayers();
      connectStack();
    } else {
      throw Assert.failure("Attempt to finalize an already finalized stack");
    }
  }

  protected abstract void createIntermediateLayers();

  protected abstract void connectStack();

  @Override
  public String toString() {
    return "AbstractNetworkStackHarness[ transport:" + transport + "channel" + channel + ",isclientStack:"
           + isClientStack + "]";
  }
}
