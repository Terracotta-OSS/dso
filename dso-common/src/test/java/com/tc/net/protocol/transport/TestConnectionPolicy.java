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
package com.tc.net.protocol.transport;

import com.tc.exception.ImplementMe;

import java.util.HashMap;
import java.util.HashSet;

public class TestConnectionPolicy implements ConnectionPolicy {

  int                                    clientConnected;
  boolean                                maxConnectionsExceeded;
  int                                    maxConnections;
  HashMap<String, HashSet<ConnectionID>> clientsByJvm = new HashMap<String, HashSet<ConnectionID>>();

  @Override
  public synchronized boolean connectClient(ConnectionID connID) {
    if (isMaxConnectionsReached()) { return false; }

    HashSet<ConnectionID> jvmClients = clientsByJvm.get(connID.getJvmID());

    if (jvmClients == null) {
      jvmClients = new HashSet<ConnectionID>();
      clientsByJvm.put(connID.getJvmID(), jvmClients);
    }

    if (!jvmClients.contains(connID)) {
      clientConnected++;
      jvmClients.add(connID);
    }

    return true;
  }

  @Override
  public synchronized void clientDisconnected(ConnectionID connID) {

    HashSet<ConnectionID> jvmClients = clientsByJvm.get(connID.getJvmID());

    if (jvmClients == null) return; // must have already received the event for this client

    if (jvmClients.remove(connID)) clientConnected--;

    if (jvmClients.size() == 0) {
      clientsByJvm.remove(connID.getJvmID());
    }
  }

  @Override
  public synchronized boolean isMaxConnectionsReached() {
    if (maxConnectionsExceeded) { return maxConnectionsExceeded; }
    return (clientsByJvm.size() >= maxConnections);
  }

  @Override
  public synchronized int getMaxConnections() {
    return maxConnections;
  }

  @Override
  public int getNumberOfActiveConnections() {
    throw new ImplementMe();
  }

  @Override
  public int getConnectionHighWatermark() {
    throw new ImplementMe();
  }

  @Override
  public boolean isConnectAllowed(ConnectionID connID) {
    HashSet<ConnectionID> jvmClients = clientsByJvm.get(connID.getJvmID());

    if (jvmClients == null) return !isMaxConnectionsReached();

    return true;
  }
}
