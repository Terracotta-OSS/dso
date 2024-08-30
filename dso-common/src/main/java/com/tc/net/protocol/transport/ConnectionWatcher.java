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

public class ConnectionWatcher implements MessageTransportListener {

  protected final ClientMessageTransport      cmt;
  protected final ClientConnectionEstablisher cce;
  protected final MessageTransportListener    target;

  /**
   * Listens to events from a MessageTransport, acts on them, and passes events through to target
   */
  public ConnectionWatcher(ClientMessageTransport cmt, MessageTransportListener target, ClientConnectionEstablisher cce) {
    this.cmt = cmt;
    this.target = target;
    this.cce = cce;
  }

  @Override
  public void notifyTransportClosed(MessageTransport transport) {
    cce.quitReconnectAttempts();
    target.notifyTransportClosed(transport);
  }

  @Override
  public void notifyTransportDisconnected(MessageTransport transport, final boolean forcedDisconnect) {
    cce.asyncReconnect(cmt);
    target.notifyTransportDisconnected(transport, forcedDisconnect);
  }

  @Override
  public void notifyTransportConnectAttempt(MessageTransport transport) {
    target.notifyTransportConnectAttempt(transport);
  }

  @Override
  public void notifyTransportConnected(MessageTransport transport) {
    target.notifyTransportConnected(transport);
  }

  @Override
  public void notifyTransportReconnectionRejected(MessageTransport transport) {
    target.notifyTransportReconnectionRejected(transport);
  }

  @Override
  public void notifyTransportClosedOnStart(MessageTransport transport) {
    target.notifyTransportClosedOnStart(transport);
  }
}
