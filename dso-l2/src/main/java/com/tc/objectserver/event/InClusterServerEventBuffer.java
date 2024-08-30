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
package com.tc.objectserver.event;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.tc.net.ClientID;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.server.ServerEvent;

import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * Sends L2 cache events to all interested L1 clients within the same cluster.
 *
 * @author Eugene Shelestovich
 */
public class InClusterServerEventBuffer implements ServerEventBuffer {

  private final static Multimap<ClientID, ServerEvent> EMPTY_MAP = ImmutableListMultimap.of();
  private final ConcurrentMap<GlobalTransactionID, Multimap<ClientID, ServerEvent>> eventMap  = Maps.newConcurrentMap();


  @Override
  public final void storeEvent(final GlobalTransactionID gtxId, final ServerEvent serverEvent,
                               final Set<ClientID> clients) {
    Multimap<ClientID,ServerEvent> multimap = eventMap.computeIfAbsent(gtxId, (g)->ArrayListMultimap.create(1,1));

    for (ClientID clientID : clients) {
      multimap.put(clientID, serverEvent);
    }
  }


  @Override
  public Multimap<ClientID, ServerEvent> getServerEventsPerClient(GlobalTransactionID gtxId) {
    final Multimap<ClientID, ServerEvent> eventsPerClient = eventMap.get(gtxId);
    return (eventsPerClient == null) ? EMPTY_MAP : eventsPerClient;
  }


  @Override
  public void removeEventsForTransaction(GlobalTransactionID globalTransactionID) {
    eventMap.remove(globalTransactionID);
  }


  @Override
  public void clearEventBufferBelowLowWaterMark(final GlobalTransactionID lowWatermark) {
    eventMap.keySet().removeIf(k->k.lessThan(lowWatermark));
  }
}
