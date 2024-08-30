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

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.object.servermap.localcache.L1ServerMapLocalCacheManager;

import java.util.Map;

public class L1ServerMapCapacityEvictionHandler extends AbstractEventHandler {
  private volatile L1ServerMapLocalCacheManager l1LocalCacheManager;

  @Override
  public void handleEvent(EventContext context) {
    L1ServerMapEvictedElementsContext evictedElementsContext = (L1ServerMapEvictedElementsContext) context;
    Map evictedElements = evictedElementsContext.getEvictedElements();
    l1LocalCacheManager.evictElements(evictedElements, evictedElementsContext.getServerMapLocalCache());
    evictedElementsContext.elementsEvicted();
  }

  public void initialize(L1ServerMapLocalCacheManager localCacheManager) {
    this.l1LocalCacheManager = localCacheManager;
  }
}
