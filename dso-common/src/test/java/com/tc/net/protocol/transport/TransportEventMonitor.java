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

/**
 * Monitors transport events.
 */
public class TransportEventMonitor implements MessageTransportListener {

  private final BlockingQueue<Object> connectedEvents      = new LinkedBlockingQueue<Object>();
  private final BlockingQueue<Object> disconnectedEvents   = new LinkedBlockingQueue<Object>();
  private final BlockingQueue<Object> connectAttemptEvents = new LinkedBlockingQueue<Object>();
  private final BlockingQueue<Object> closedEvents         = new LinkedBlockingQueue<Object>();
  private final BlockingQueue<Object> rejectedEvents       = new LinkedBlockingQueue<Object>();

  @Override
  public void notifyTransportConnected(MessageTransport transport) {
    try {
      connectedEvents.put(new Object());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void notifyTransportDisconnected(MessageTransport transport, final boolean forcedDisconnect) {
    try {
      disconnectedEvents.put(new Object());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void notifyTransportConnectAttempt(MessageTransport transport) {
    try {
      this.connectAttemptEvents.put(new Object());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void notifyTransportClosed(MessageTransport transport) {
    try {
      this.closedEvents.put(new Object());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void notifyTransportReconnectionRejected(MessageTransport transport) {
    try {
      this.rejectedEvents.put(new Object());
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

//  this is a no-op as this method is required for a specific case of a connection that is 
//  disconnected at start status itself and so does not fit the pattern here
  @Override
  public void notifyTransportClosedOnStart(MessageTransport transport) {
    // no-op
  }

  public boolean waitForConnect(long timeout) throws InterruptedException {
    return this.connectedEvents.poll(timeout, TimeUnit.MILLISECONDS) != null;
  }

  public boolean waitForDisconnect(long timeout) throws InterruptedException {
    return this.disconnectedEvents.poll(timeout, TimeUnit.MILLISECONDS) != null;
  }

  public boolean waitForConnectAttempt(long timeout) throws InterruptedException {
    return this.connectAttemptEvents.poll(timeout, TimeUnit.MILLISECONDS) != null;
  }

  public boolean waitForClose(long timeout) throws InterruptedException {
    return this.closedEvents.poll(timeout, TimeUnit.MILLISECONDS) != null;
  }

  public boolean waitForConnectionRejected(long timeout) throws InterruptedException {
    return this.rejectedEvents.poll(timeout, TimeUnit.MILLISECONDS) != null;
  }

}
