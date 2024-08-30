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
package com.tc.objectserver.search;

import com.tc.l2.context.StateChangedEvent;
import com.tc.l2.state.StateManager;
import com.tc.net.NodeID;

import java.io.IOException;

public class NullIndexHACoordinator extends NullIndexManager implements IndexHACoordinator {

  public void setStateManager(StateManager stateManager) {
    //
  }

  @Override
  public void applyTempJournalsAndSwitch() throws IOException {
    //
  }

  @Override
  public void l2StateChanged(StateChangedEvent sce) {
    //
  }

  @Override
  public void applyIndexSync(String cacheName, String indexId, String fileName, byte[] data, boolean isTCFile,
                             boolean isLast) {
    //
  }

  public void nodeJoined(NodeID nodeID) {
    //
  }

  public void nodeLeft(NodeID nodeID) {
    //
  }

  @Override
  public void doSyncPrepare() {
    //
  }

  @Override
  public int getNumberOfIndexesPerCache() {
    return 0;
  }

}
