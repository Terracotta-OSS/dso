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
package com.tc.async.api;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;

import java.util.Collection;
import java.util.Iterator;

/**
 * Simple superclass for event handlers that does the iterating over events in the array
 * 
 * @author steve
 */
public abstract class AbstractEventHandler implements EventHandler {

  private ConfigurationContext configContext;
  private TCLogger             logger;

  @Override
  public abstract void handleEvent(EventContext context) throws EventHandlerException;

  @Override
  public void handleEvents(Collection contexts) throws EventHandlerException {
    for (Iterator i = contexts.iterator(); i.hasNext();) {
      EventContext eh = (EventContext) i.next();
      handleEvent(eh);
    }
  }

  @Override
  public synchronized final void initializeContext(ConfigurationContext context) {
    if (context == null) {
      this.logger = TCLogging.getLogger(this.getClass());
      logger.warn("Setting config context to null. This is highly unusual");
    } else {
      this.logger = context.getLogger(this.getClass());
    }
    this.configContext = context;
    initialize(context);
  }

  protected void initialize(ConfigurationContext context) {
    // Subclasses can override this.
  }

  public TCLogger getLogger() {
    return logger;
  }

  @Override
  public synchronized void destroy() {
    configContext = null;
  }

  /**
   * @return the ConfigurationContext object that was passed to the <code>initialize</code> method.
   */
  protected synchronized ConfigurationContext getConfigurationContext() {
    return configContext;
  }

}
