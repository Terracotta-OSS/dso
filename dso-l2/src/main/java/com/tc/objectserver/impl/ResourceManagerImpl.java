/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
package com.tc.objectserver.impl;

import com.tc.net.GroupID;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.object.msg.ResourceManagerThrottleMessage;
import com.tc.object.net.DSOChannelManager;
import com.tc.objectserver.api.ResourceManager;
import com.tc.text.PrettyPrinter;

/**
 * @author tim
 */
public class ResourceManagerImpl implements ResourceManager {
  private final DSOChannelManager channelManager;
  private final GroupID groupID;

  private volatile State state = State.NORMAL;

  private float throttleAmount = 0.0f;
  private State lastBroadcastState = null;
  private float lastBroadcastThrottleAmount = 0.0f;

  public ResourceManagerImpl(final DSOChannelManager channelManager, final GroupID groupID) {
    this.channelManager = channelManager;
    this.groupID = groupID;
  }

  @Override
  public State getState() {
    return state;
  }

  @Override
  public synchronized void setThrottle(final float percentage) {
    if (percentage < 0.0f || percentage > 1.0f) {
      throw new IllegalArgumentException("Ratio out of range [0.0, 1.0], actual " + percentage);
    }
    state = State.THROTTLED;
    throttleAmount = percentage;
    broadcastMessage();
  }

  @Override
  public synchronized void setRestricted() {
    throttleAmount = 0.0f;
    state = State.RESTRICTED;
    broadcastMessage();
  }

  @Override
  public synchronized void resetState() {
    throttleAmount = 0.0f;
    state = State.NORMAL;
    broadcastMessage();
  }

  private void broadcastMessage() {
    if (state == lastBroadcastState && throttleAmount == lastBroadcastThrottleAmount) {
      // broadcast is the same as the last one, don't bother sending it.
      return;
    }
    for (MessageChannel clientChannel : channelManager.getActiveChannels()) {
      sendMessageTo(clientChannel);
    }
    lastBroadcastState = state;
    lastBroadcastThrottleAmount = throttleAmount;
  }

  private void sendMessageTo(MessageChannel clientChannel) {
    ResourceManagerThrottleMessage throttleMessage = (ResourceManagerThrottleMessage) clientChannel.createMessage(TCMessageType.RESOURCE_MANAGER_THROTTLE_STATE_MESSAGE);
    throttleMessage.initialize(groupID, state == State.RESTRICTED, throttleAmount);
    throttleMessage.send();
  }

  @Override
  public synchronized void channelCreated(final MessageChannel channel) {
    // Send an update to any freshly connected clients so they know the throttle status.
    sendMessageTo(channel);
  }

  @Override
  public void channelRemoved(final MessageChannel channel) {
    // Nothing to do.
  }

  @Override
  public PrettyPrinter prettyPrint(final PrettyPrinter out) {
    out.println(getClass().getName());
    out.println("State: " + state);
    out.println("Throttle amount: " + throttleAmount);
    out.flush();
    return out;
  }
}
