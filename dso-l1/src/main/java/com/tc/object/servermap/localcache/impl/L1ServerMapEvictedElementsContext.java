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
package com.tc.object.servermap.localcache.impl;

import com.tc.async.api.EventContext;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.object.servermap.localcache.ServerMapLocalCache;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class L1ServerMapEvictedElementsContext implements EventContext {
  private static final TCLogger      logger              = TCLogging.getLogger(L1ServerMapEvictedElementsContext.class);
  private static final AtomicInteger elementsToBeEvicted = new AtomicInteger();
  private static final AtomicInteger instanceCount       = new AtomicInteger();

  private final Map                  evictedElements;
  private final ServerMapLocalCache  serverMapLocalCache;

  public L1ServerMapEvictedElementsContext(Map evictedElements, ServerMapLocalCache serverMapLocalCache) {
    this.evictedElements = evictedElements;
    this.serverMapLocalCache = serverMapLocalCache;
    int currentInstanceCount = instanceCount.incrementAndGet();
    if (elementsToBeEvicted.addAndGet(evictedElements.size()) > 200 && currentInstanceCount % 10 == 0
        && logger.isDebugEnabled()) {
      logger.debug("Elements waiting to be evicted: " + elementsToBeEvicted.get());
    }
  }

  public Map getEvictedElements() {
    return evictedElements;
  }

  public ServerMapLocalCache getServerMapLocalCache() {
    return serverMapLocalCache;
  }

  public void elementsEvicted() {
    elementsToBeEvicted.addAndGet(-evictedElements.size());
  }
}
