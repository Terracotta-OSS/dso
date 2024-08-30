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
import com.tc.async.api.EventContext;
import com.tc.async.api.EventHandler;
import com.tc.invalidation.Invalidations;
import com.tc.invalidation.InvalidationsProcessor;
import com.tc.object.msg.InvalidateObjectsMessage;

public class ReceiveInvalidationHandler extends AbstractEventHandler implements EventHandler {

  private final InvalidationsProcessor invalidationsProcessor;

  public ReceiveInvalidationHandler(InvalidationsProcessor invalidationsProcessor) {
    this.invalidationsProcessor = invalidationsProcessor;
  }

  @Override
  public void handleEvent(EventContext context) {
    InvalidateObjectsMessage invalidationContext = (InvalidateObjectsMessage) context;
    invalidationsProcessor.processInvalidations(new Invalidations(invalidationContext.getObjectIDsToInvalidate()));
  }
}
