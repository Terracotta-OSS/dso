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
package com.terracotta.management.service.impl.events;

import com.tc.management.ManagementEventListener;
import com.tc.management.RemoteManagement;
import com.tc.management.TCManagementEvent;
import com.tc.management.TSAManagementEventPayload;
import com.tc.management.TerracottaRemoteManagement;
import com.terracotta.management.service.impl.util.RemoteManagementSource;
import org.glassfish.jersey.media.sse.InboundEvent;
import org.terracotta.management.resource.Representable;
import org.terracotta.management.resource.events.EventEntityV2;
import org.terracotta.management.resource.services.events.EventServiceV2;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Ludovic Orban
 */
public class EventServiceImplV2 implements EventServiceV2 {
  private static final Logger LOG = LoggerFactory.getLogger(EventServiceImplV2.class);

  private final Map<EventListener, ListenerHolder> listenerMap = new IdentityHashMap<>();
  private final RemoteManagementSource remoteManagementSource;

  public EventServiceImplV2(RemoteManagementSource remoteManagementSource) {
    this.remoteManagementSource = remoteManagementSource;
  }

  @Override
  public void registerEventListener(final EventListener listener, boolean localOnly) {
    RemoteManagementSource.RemoteTSAEventListener remoteTSAEventListener = null;
    if (!localOnly) {
      remoteTSAEventListener = new RemoteManagementSource.RemoteTSAEventListener() {
        @Override
        public void onEvent(InboundEvent inboundEvent) {
          EventEntityV2 eventEntity = inboundEvent.readData(EventEntityV2.class);
          listener.onEvent(eventEntity);
        }

        @Override
        public void onError(Throwable throwable) {
          listener.onError(throwable);
        }
      };
      remoteManagementSource.addTsaEventListener(remoteTSAEventListener);
    }

    RemoteManagement remoteManagementInstance = TerracottaRemoteManagement.getRemoteManagementInstance();
    ManagementEventListener managementEventListener = new ManagementEventListener() {
      @Override
      public ClassLoader getClassLoader() {
        return EventServiceImplV2.class.getClassLoader();
      }

      @Override
      public void onEvent(TCManagementEvent event, Map<String, Object> context) {
        Serializable payload = event.getPayload();
        EventEntityV2 eventEntity;
        if (payload instanceof EventEntityV2) {
          eventEntity = (EventEntityV2)payload;
          String agentId = (String) context.get(ManagementEventListener.CONTEXT_SOURCE_JMX_ID);
          String clientID = (String) context.get(ManagementEventListener.CONTEXT_SOURCE_NODE_NAME);
          String remoteAddress = (String) context.get(ManagementEventListener.CONTEXT_SOURCE_REMOTE_ADDRESS);
          eventEntity.getRootRepresentables().put("RemoteAddress", remoteAddress);
          eventEntity.getRootRepresentables().put("ClientID", clientID);
          eventEntity.setAgentId(agentId);
        } else if (payload instanceof TSAManagementEventPayload) {
          TSAManagementEventPayload tsaManagementEventPayload = (TSAManagementEventPayload)payload;

          eventEntity = new EventEntityV2();
          eventEntity.setAgentId(Representable.EMBEDDED_AGENT_ID);
          eventEntity.getRootRepresentables().put("Server.Name", context.get(ManagementEventListener.CONTEXT_SOURCE_NODE_NAME));
          eventEntity.getRootRepresentables().putAll(tsaManagementEventPayload.getAttributes());
          eventEntity.setType(event.getType());
        } else {
          eventEntity = new EventEntityV2();
          eventEntity.setType("TSA.ERROR");
          eventEntity.setAgentId(Representable.EMBEDDED_AGENT_ID);
          eventEntity.getRootRepresentables().put("Error.Details", "Unknown event : " + payload);
        }
        listener.onEvent(eventEntity);
      }
    };
    synchronized (listenerMap) {
      listenerMap.put(listener, new ListenerHolder(managementEventListener, remoteTSAEventListener));
    }
    remoteManagementInstance.registerEventListener(managementEventListener);
  }

  @Override
  public void unregisterEventListener(EventListener listener) {
    ListenerHolder listenerHolder;
    synchronized (listenerMap) {
      listenerHolder = listenerMap.remove(listener);
    }
    if (listenerHolder != null) {
      RemoteManagement remoteManagementInstance = TerracottaRemoteManagement.getRemoteManagementInstance();
      remoteManagementInstance.unregisterEventListener(listenerHolder.managementEventListener);
      if (listenerHolder.remoteTSAEventListener != null) {
        remoteManagementSource.removeTsaEventListener(listenerHolder.remoteTSAEventListener);
      }
    }
  }

  static class ListenerHolder {
    ManagementEventListener managementEventListener;
    RemoteManagementSource.RemoteTSAEventListener remoteTSAEventListener;

    ListenerHolder(ManagementEventListener managementEventListener, RemoteManagementSource.RemoteTSAEventListener remoteTSAEventListener) {
      this.managementEventListener = managementEventListener;
      this.remoteTSAEventListener = remoteTSAEventListener;
    }
  }

}
