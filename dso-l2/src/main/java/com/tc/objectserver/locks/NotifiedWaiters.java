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
package com.tc.objectserver.locks;

import com.tc.net.NodeID;
import com.tc.object.locks.ClientServerExchangeLockContext;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NotifiedWaiters {

  private final Map notifiedSets = new HashMap();

  @Override
  public String toString() {
    synchronized (notifiedSets) {
      return "NotifiedWaiters[" + notifiedSets + "]";
    }
  }

  public boolean isEmpty() {
    return notifiedSets.isEmpty();
  }

  public void addNotification(ClientServerExchangeLockContext context) {
    synchronized (notifiedSets) {
      getOrCreateSetFor(context.getNodeID()).add(context);
    }
  }

  public Set getNotifiedFor(NodeID nodeID) {
    synchronized (notifiedSets) {
      Set rv = getSetFor(nodeID);
      return (rv == null) ? Collections.EMPTY_SET : rv;
    }
  }

  private Set getSetFor(NodeID nodeID) {
    return (Set) notifiedSets.get(nodeID);
  }

  private Set getOrCreateSetFor(NodeID nodeID) {
    Set rv = getSetFor(nodeID);
    if (rv == null) {
      rv = new HashSet();
      notifiedSets.put(nodeID, rv);
    }
    return rv;
  }

}
