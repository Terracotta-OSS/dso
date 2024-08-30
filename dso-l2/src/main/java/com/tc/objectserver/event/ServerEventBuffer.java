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

import com.google.common.collect.Multimap;
import com.tc.net.ClientID;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.server.ServerEvent;

import java.util.Set;

public interface ServerEventBuffer {

  void storeEvent(GlobalTransactionID gtxId, ServerEvent serverEvent, Set<ClientID> clients);

  Multimap<ClientID, ServerEvent> getServerEventsPerClient(GlobalTransactionID gtxId);

  /**
   * Used by Passive server to clear event buffer, on basis of low watermark from clients
   */
  void clearEventBufferBelowLowWaterMark(GlobalTransactionID lowWatermark);

  /**
   * Used by Active server to remove events for a transaction, on basis of low watermark from clients
   */
  void removeEventsForTransaction(GlobalTransactionID globalTransactionID);

}
