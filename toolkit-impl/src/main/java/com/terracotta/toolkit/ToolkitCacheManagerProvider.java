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
package com.terracotta.toolkit;

import net.sf.ehcache.CacheManager;

import java.util.UUID;

public class ToolkitCacheManagerProvider {
  private final CacheManager defaultToolkitCacheManager;

  public ToolkitCacheManagerProvider() {
    this.defaultToolkitCacheManager = createDefaultToolkitCacheManager();
  }

  private CacheManager createDefaultToolkitCacheManager() {
    String cacheManagerUniqueName = "toolkitDefaultCacheManager-" + UUID.randomUUID().toString();
    return CacheManager.newInstance(new net.sf.ehcache.config.Configuration().name(cacheManagerUniqueName));
  }

  public CacheManager getDefaultCacheManager() {
    return defaultToolkitCacheManager;
  }

  public void shutdownDefaultCacheManager() {
    defaultToolkitCacheManager.shutdown();
  }

}
