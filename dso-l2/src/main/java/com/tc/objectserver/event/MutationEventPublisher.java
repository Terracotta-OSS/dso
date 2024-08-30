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

import com.tc.net.ClientID;
import com.tc.object.ObjectID;
import com.tc.objectserver.managedobject.CDSMValue;
import com.tc.server.ServerEventType;

import java.util.Set;

/**
 * Records all cache modifications and maps keys to actual values
 * omiting intermediate object id.
 *
 * @author Eugene Shelestovich
 */
public interface MutationEventPublisher {

  /**
   * This method records an <code>AdvancedServerEvent</code>.
   * 
   * @param clientIds
   */
  void publishEvent(Set<ClientID> clientIds, ServerEventType type, Object key, CDSMValue value, String cacheName);

  void setBytesForObjectID(ObjectID objectId, byte[] value);
}
