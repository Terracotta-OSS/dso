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
package com.tc.object.session;

import com.tc.net.NodeID;

public class NullSessionManager implements SessionManager, SessionProvider {

  @Override
  public SessionID getSessionID(NodeID nid) {
    return SessionID.NULL_ID;
  }

  @Override
  public SessionID nextSessionID(NodeID nid) {
    return SessionID.NULL_ID;
  }

  @Override
  public void newSession(NodeID nid) {
    return;
  }

  @Override
  public boolean isCurrentSession(NodeID nid, SessionID sessionID) {
    return true;
  }

  @Override
  public void initProvider(NodeID nid) {
    return;
  }

  @Override
  public void resetSessionProvider() {
    return;
  }

}
