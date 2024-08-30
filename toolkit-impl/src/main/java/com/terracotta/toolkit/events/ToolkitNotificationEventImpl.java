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
package com.terracotta.toolkit.events;

import org.terracotta.toolkit.cluster.ClusterNode;
import org.terracotta.toolkit.events.ToolkitNotificationEvent;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.terracotta.toolkit.object.serialization.SerializationStrategy;

public class ToolkitNotificationEventImpl<T> implements ToolkitNotificationEvent<T> {
  private static final TCLogger       LOGGER = TCLogging.getLogger(ToolkitNotificationEventImpl.class);
  private final SerializationStrategy strategy;
  private final String                remoteNodeSerializedForm;
  private final String                msgSerializedForm;
  private volatile ClusterNode        remoteNode;
  private volatile T                  msg;

  public ToolkitNotificationEventImpl(SerializationStrategy strategy, String remoteNode, String msg) {
    this.strategy = strategy;
    this.remoteNodeSerializedForm = remoteNode;
    this.msgSerializedForm = msg;
  }

  @Override
  public T getMessage() {
    if (msg == null) {
      try {
        msg = (T) strategy.deserializeFromString(msgSerializedForm, false);
      } catch (Exception e) {
        LOGGER.warn("Ignoring toolkit notifier notification. Failed to deserialize notification msg - "
                    + msgSerializedForm, e);
      }
    }
    return msg;
  }

  @Override
  public ClusterNode getRemoteNode() {
    if (remoteNode == null) {
      try {
        remoteNode = (ClusterNode) strategy.deserializeFromString(remoteNodeSerializedForm, false);
      } catch (Exception e) {
        LOGGER.warn("Ignoring toolkit notifier notification. Failed to deserialize notification remote node - "
                    + remoteNodeSerializedForm, e);
      }
    }
    return remoteNode;
  }

  @Override
  public String toString() {
    return "ToolkitNotificationEventImpl [remoteNode=" + getRemoteNode() + ", msg=" + getMessage() + "]";
  }

}
