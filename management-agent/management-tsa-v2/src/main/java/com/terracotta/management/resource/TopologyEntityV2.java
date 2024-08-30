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
package com.terracotta.management.resource;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A {@link org.terracotta.management.resource.AbstractEntityV2} representing a TSA topology from the management API.
 *
 * @author Ludovic Orban
 */
public class TopologyEntityV2 extends AbstractTsaEntityV2 {

  private final Set<ServerGroupEntityV2> serverGroupEntities = new HashSet<ServerGroupEntityV2>();
  private final Set<ClientEntityV2>      clientEntities = new HashSet<ClientEntityV2>();
  private final Map<String, Integer>     unreadOperatorEventCount = new HashMap<String, Integer>();

  public Set<ServerGroupEntityV2> getServerGroupEntities() {
    return serverGroupEntities;
  }

  public Set<ClientEntityV2> getClientEntities() {
    return clientEntities;
  }

  public Map<String, Integer> getUnreadOperatorEventCount() {
    return unreadOperatorEventCount;
  }

}
