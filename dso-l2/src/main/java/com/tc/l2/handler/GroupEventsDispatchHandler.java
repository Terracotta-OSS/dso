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
package com.tc.l2.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.async.api.Sink;
import com.tc.net.NodeID;
import com.tc.net.groups.GroupEventsListener;

import java.util.ArrayList;
import java.util.List;

public class GroupEventsDispatchHandler extends AbstractEventHandler {

  private final List<GroupEventsListener> listeners = new ArrayList();

  public void addListener(GroupEventsListener listener) {
    this.listeners.add(listener);
  }

  @Override
  public void handleEvent(EventContext context) {
    GroupEvent e = (GroupEvent) context;
    if (e.nodeJoined()) {
      for (GroupEventsListener listener : listeners) {
        listener.nodeJoined(e.getNodeID());
      }
    } else {
      for (GroupEventsListener listener : listeners) {
        listener.nodeLeft(e.getNodeID());
      }
    }
  }

  public static final class GroupEventsDispatcher implements GroupEventsListener {
    private final Sink sink;

    public GroupEventsDispatcher(Sink sink) {
      this.sink = sink;
    }

    @Override
    public void nodeJoined(NodeID nodeID) {
      sink.add(new GroupEvent(nodeID, true));
    }

    @Override
    public void nodeLeft(NodeID nodeID) {
      sink.add(new GroupEvent(nodeID, false));
    }
  }

  private static final class GroupEvent implements EventContext {

    private final NodeID  nodeID;
    private final boolean joined;

    public GroupEvent(NodeID nodeID, boolean joined) {
      this.nodeID = nodeID;
      this.joined = joined;
    }

    public boolean nodeJoined() {
      return joined;
    }

    public NodeID getNodeID() {
      return nodeID;
    }

  }
}
