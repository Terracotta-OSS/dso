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
package net.sf.ehcache;

import java.lang.reflect.Method;

/**
 * Helper class used for initializing ehcache's using protected {@link CacheManager#initializeEhcache(Ehcache, boolean)}
 * method
 */
public class EhcacheInitializationHelper {
  private final CacheManager cacheManager;

  /**
   * Create a cache initializer with the given {@link CacheManager}
   * 
   * @param cacheManager
   */
  public EhcacheInitializationHelper(final CacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  /**
   * Initialize the {@link Ehcache}.
   * 
   * @param cache
   */
  public void initializeEhcache(final Ehcache cache) {
    // CacheManager can be loaded using clusteredStateLoader in case of embedded ehcache or AppClassLoader if
    // ehcache-core is present in classpath. Reflection is used here to handle both the cases.
    try {
      Method method = this.cacheManager.getClass().getDeclaredMethod("initializeEhcache", Ehcache.class, boolean.class);
      method.setAccessible(true);
      method.invoke(cacheManager, cache, false);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
