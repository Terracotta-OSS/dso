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
package com.tc.object.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventContext;
import com.tc.object.ClientConfigurationContext;
import com.tc.object.RemoteObjectManager;
import com.tc.object.msg.ObjectsNotFoundMessage;
import com.tc.object.msg.RequestManagedObjectResponseMessage;

public class ReceiveObjectHandler extends AbstractEventHandler {
  private RemoteObjectManager objectManager;

  @Override
  public void handleEvent(EventContext context) {
    if (context instanceof RequestManagedObjectResponseMessage) {
      RequestManagedObjectResponseMessage m = (RequestManagedObjectResponseMessage) context;
      objectManager.addAllObjects(m.getLocalSessionID(), m.getBatchID(), m.getObjects(), m.getSourceNodeID());
    } else {
      ObjectsNotFoundMessage notFound = (ObjectsNotFoundMessage) context;
      objectManager.objectsNotFoundFor(notFound.getLocalSessionID(), notFound.getBatchID(), notFound
          .getMissingObjectIDs(), notFound.getSourceNodeID());
    }
  }

  @Override
  public void initialize(ConfigurationContext context) {
    super.initialize(context);
    ClientConfigurationContext ccc = (ClientConfigurationContext) context;
    this.objectManager = ccc.getObjectManager();
  }

}