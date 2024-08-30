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
package com.tc.objectserver.locks;

import com.tc.object.locks.ThreadID;
import com.tc.object.locks.ServerLockContext.State;

import java.io.Serializable;

public class ServerLockContextBean implements Serializable {
  private final String   client;
  private final ThreadID threadID;
  private final State    state;
  private final long     timeout;

  public ServerLockContextBean(String client, ThreadID threadID, State state) {
    this(client, threadID, state, -1);
  }

  public ServerLockContextBean(String client, ThreadID threadID, State state, long timeout) {
    this.client = client;
    this.threadID = threadID;
    this.state = state;
    this.timeout = timeout;
  }

  public String getClient() {
    return client;
  }

  public ThreadID getThreadID() {
    return threadID;
  }

  public State getState() {
    return state;
  }

  public long getTimeout() {
    return timeout;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((client == null) ? 0 : client.hashCode());
    result = prime * result + ((state == null) ? 0 : state.hashCode());
    result = prime * result + ((threadID == null) ? 0 : threadID.hashCode());
    result = prime * result + (int) (timeout ^ (timeout >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    ServerLockContextBean other = (ServerLockContextBean) obj;
    if (client == null) {
      if (other.client != null) return false;
    } else if (!client.equals(other.client)) return false;
    if (state == null) {
      if (other.state != null) return false;
    } else if (!state.equals(other.state)) return false;
    if (threadID == null) {
      if (other.threadID != null) return false;
    } else if (!threadID.equals(other.threadID)) return false;
    if (timeout != other.timeout) return false;
    return true;
  }

  @Override
  public String toString() {
    return "ServerLockContextBean [client=" + client + ", state=" + state + ", threadID=" + threadID + ", timeout="
           + timeout + "]";
  }
}
