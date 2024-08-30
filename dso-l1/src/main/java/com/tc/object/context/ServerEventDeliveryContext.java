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
package com.tc.object.context;

import com.tc.async.api.MultiThreadedEventContext;
import com.tc.net.NodeID;
import com.tc.server.ServerEvent;

/**
 * @author Eugene Shelestovich
 */
public class ServerEventDeliveryContext implements MultiThreadedEventContext {

  private final ServerEvent event;
  private final NodeID remoteNode;

  public ServerEventDeliveryContext(final ServerEvent event, final NodeID remoteNode) {
    this.event = event;
    this.remoteNode = remoteNode;
  }

  public ServerEvent getEvent() {
    return event;
  }

  public NodeID getRemoteNode() {
    return remoteNode;
  }

  @Override
  public Object getKey() {
    return event.getKey();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final ServerEventDeliveryContext that = (ServerEventDeliveryContext) o;

    if (!event.equals(that.event)) return false;
    if (!remoteNode.equals(that.remoteNode)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = event.hashCode();
    result = 31 * result + remoteNode.hashCode();
    return result;
  }
}
