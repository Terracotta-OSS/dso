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
package com.tc.async.impl;

import com.tc.async.api.EventContext;
import com.tc.async.api.Sink;
import com.tc.async.api.SpecializedEventContext;
import com.tc.net.NodeID;

public class InBandMoveToNextSink implements SpecializedEventContext {

  private final EventContext event;
  private final Sink         sink;
  private final NodeID       nodeID;

  public InBandMoveToNextSink(EventContext event, Sink sink, NodeID nodeID) {
    this.event = event;
    this.sink = sink;
    this.nodeID = nodeID;
  }

  @Override
  public void execute() {
    sink.add(event);
  }

  @Override
  public Object getKey() {
    return nodeID;
  }

}
