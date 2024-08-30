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
package com.tc.objectserver.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventContext;
import com.tc.object.msg.KeysForOrphanedValuesMessage;
import com.tc.object.msg.NodeMetaDataMessage;
import com.tc.object.msg.NodesWithKeysMessage;
import com.tc.object.msg.NodesWithObjectsMessage;
import com.tc.objectserver.clustermetadata.ServerClusterMetaDataManager;
import com.tc.objectserver.core.api.ServerConfigurationContext;
import com.tc.util.Assert;

public class ServerClusterMetaDataHandler extends AbstractEventHandler {

  private ServerClusterMetaDataManager clusterMetaDataManager;

  @Override
  public void handleEvent(final EventContext context) {
    if (context instanceof NodesWithObjectsMessage) {
      this.clusterMetaDataManager.handleMessage((NodesWithObjectsMessage)context);
    } else if (context instanceof KeysForOrphanedValuesMessage) {
      this.clusterMetaDataManager.handleMessage((KeysForOrphanedValuesMessage)context);
    } else if (context instanceof NodeMetaDataMessage) {
      this.clusterMetaDataManager.handleMessage((NodeMetaDataMessage)context);
    } else if (context instanceof NodesWithKeysMessage) {
      this.clusterMetaDataManager.handleMessage((NodesWithKeysMessage)context);
    } else {
      Assert.fail("Unknown event type "+context.getClass().getName());
    }
  }

  @Override
  public void initialize(final ConfigurationContext ctxt) {
    super.initialize(ctxt);
    ServerConfigurationContext scc = ((ServerConfigurationContext) ctxt);
    this.clusterMetaDataManager = scc.getClusterMetaDataManager();
  }

}
