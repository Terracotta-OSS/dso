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
import com.tc.objectserver.api.ServerMapRequestManager;
import com.tc.objectserver.context.EntryForKeyResponseContext;
import com.tc.objectserver.context.ServerMapMissingObjectResponseContext;
import com.tc.objectserver.core.api.ServerConfigurationContext;

public class RespondToServerMapRequestHandler extends AbstractEventHandler {

  private ServerMapRequestManager serverMapRequestManager;

  @Override
  public void handleEvent(final EventContext context) {
    if (context instanceof ServerMapMissingObjectResponseContext) {
      final ServerMapMissingObjectResponseContext responseContext = (ServerMapMissingObjectResponseContext) context;
      serverMapRequestManager.sendMissingObjectResponseFor(responseContext.getMapID());
    } else if (context instanceof EntryForKeyResponseContext) {
      final EntryForKeyResponseContext responseContext = (EntryForKeyResponseContext) context;
      serverMapRequestManager.sendResponseFor(responseContext.getMapID(), responseContext.getManagedObject());
    }
  }

  @Override
  public void initialize(final ConfigurationContext context) {
    final ServerConfigurationContext oscc = (ServerConfigurationContext) context;
    this.serverMapRequestManager = oscc.getServerMapRequestManager();
  }

}
