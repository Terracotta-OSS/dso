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
package com.tc.net.protocol.tcm;

import com.tc.license.ProductID;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.net.ClientID;
import com.tc.net.ServerID;
import com.tc.net.protocol.NetworkStackID;

/**
 * TODO: Document me
 * 
 * @author teck
 */
public class ServerMessageChannelImpl extends AbstractMessageChannel implements ServerMessageChannel {
  private static final TCLogger logger = TCLogging.getLogger(ServerMessageChannel.class);
  private final ChannelID       sessionID;

  /**
   * this is for the server it needs a session ID
   */
  protected ServerMessageChannelImpl(ChannelID sessionID, TCMessageRouter router, TCMessageFactory msgFactory,
                                     final ServerID serverID, final ProductID productId) {
    super(router, logger, msgFactory, new ClientID(sessionID.toLong()), productId);
    this.sessionID = sessionID;
    setLocalNodeID(serverID);

    // server message channels should always be open initially
    synchronized (getStatus()) {
      channelOpened();
    }
  }

  @Override
  public ChannelID getChannelID() {
    return sessionID;
  }

  @Override
  public NetworkStackID open() {
    throw new UnsupportedOperationException("Server channels don't support open()");
  }

  @Override
  public NetworkStackID open(char[] password) {
    throw new UnsupportedOperationException("Server channels don't support open()");
  }

  @Override
  public void reset() {
    throw new UnsupportedOperationException("Server channels don't support reset()");
  }

}