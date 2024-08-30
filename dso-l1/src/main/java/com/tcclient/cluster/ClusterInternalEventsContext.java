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
package com.tcclient.cluster;

import com.tc.async.api.EventContext;
import com.tc.cluster.DsoClusterEvent;
import com.tc.cluster.DsoClusterListener;
import com.tcclient.cluster.DsoClusterInternal.DsoClusterEventType;

/**
 * DsoCluster Events Contexts to be put in ClusterInternalEventsHandler.
 */
public class ClusterInternalEventsContext implements EventContext {

  private final DsoClusterEventType eventType;
  private final DsoClusterEvent     event;
  private final DsoClusterListener  dsoClusterListener;

  public ClusterInternalEventsContext(DsoClusterEventType eventType, DsoClusterEvent event, DsoClusterListener listener) {
    this.eventType = eventType;
    this.event = event;
    this.dsoClusterListener = listener;
  }

  public DsoClusterEventType getEventType() {
    return eventType;
  }

  public DsoClusterEvent getEvent() {
    return event;
  }

  public DsoClusterListener getDsoClusterListener() {
    return dsoClusterListener;
  }

  @Override
  public String toString() {
    return "ClusterInternalEventsContext [eventType=" + eventType + ", event=" + event + ", dsoClusterListener="
           + dsoClusterListener + "]";
  }

}
