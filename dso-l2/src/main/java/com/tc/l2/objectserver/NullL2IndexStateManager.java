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
package com.tc.l2.objectserver;

import com.tc.net.NodeID;
import com.tc.util.State;

public class NullL2IndexStateManager implements L2IndexStateManager {

  @Override
  public boolean addL2(NodeID nodeID, State currentState) {
    return true;
  }

  @Override
  public void registerForL2IndexStateChangeEvents(L2IndexStateListener listener) {
    //
  }

  @Override
  public void removeL2(NodeID nodeID) {
    //
  }

  @Override
  public void initiateIndexSync(NodeID nodeID) {
    //
  }

  @Override
  public void receivedAck(NodeID nodeID, int amount) {
    //
  }

}
