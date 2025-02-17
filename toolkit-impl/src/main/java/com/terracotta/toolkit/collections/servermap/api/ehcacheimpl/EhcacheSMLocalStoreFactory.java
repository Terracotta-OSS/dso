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
package com.terracotta.toolkit.collections.servermap.api.ehcacheimpl;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheInitializationHelper;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.EhcacheInitializationHelper;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration.Strategy;
import net.sf.ehcache.config.PinningConfiguration;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import net.sf.ehcache.terracotta.InternalEhcache;

import com.terracotta.toolkit.collections.servermap.api.ServerMapLocalStore;
import com.terracotta.toolkit.collections.servermap.api.ServerMapLocalStoreConfig;
import com.terracotta.toolkit.collections.servermap.api.ServerMapLocalStoreFactory;

public class EhcacheSMLocalStoreFactory implements ServerMapLocalStoreFactory {
  private final CacheManager defaultCacheManager;

  public EhcacheSMLocalStoreFactory(CacheManager defaultCacheManager) {
    this.defaultCacheManager = defaultCacheManager;
  }

  @Override
  public <K, V> ServerMapLocalStore<K, V> getOrCreateServerMapLocalStore(ServerMapLocalStoreConfig config) {
    InternalEhcache localStoreCache = getOrCreateEhcacheLocalCache(config);

    return (ServerMapLocalStore<K, V>) new EhcacheSMLocalStore(localStoreCache);
  }

  private synchronized InternalEhcache getOrCreateEhcacheLocalCache(ServerMapLocalStoreConfig config) {
    InternalEhcache ehcache;
    CacheManager cacheManager = getOrCreateCacheManager(config);
    final String localCacheName = "local_shadow_cache_for_" + cacheManager.getName() + "_" + config.getLocalStoreName();
    ehcache = (InternalEhcache) cacheManager.getEhcache(localCacheName);
    if (ehcache == null) {
      ehcache = createCache(localCacheName, config, cacheManager);
      new EhcacheInitializationHelper(cacheManager).initializeEhcache(ehcache);
    }
    return ehcache;
  }

  private synchronized CacheManager getOrCreateCacheManager(ServerMapLocalStoreConfig config) {
    String localStoreManagerName = config.getLocalStoreManagerName();
    if (localStoreManagerName == null || "".equals(localStoreManagerName.trim())) {
      // use default cache manager when no name is specified
      return defaultCacheManager;
    }
    CacheManager cacheManager = CacheInitializationHelper
        .getInitializingCacheManager(config.getLocalStoreManagerName());
    if (cacheManager != null) { return cacheManager; }

    cacheManager = CacheManager.getCacheManager(config.getLocalStoreManagerName());
    if (cacheManager == null) {
      cacheManager = CacheManager.create(new Configuration().name(config.getLocalStoreManagerName()));
    }
    return cacheManager;
  }

  private static InternalEhcache createCache(String cacheName, ServerMapLocalStoreConfig config,
                                             CacheManager cacheManager) {
    CacheConfiguration cacheConfig = new CacheConfiguration(cacheName, 0)
        .persistence(new PersistenceConfiguration().strategy(Strategy.NONE))
        .memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.CLOCK).overflowToDisk(false);

    // classloader for these caches needs to see toolkit internal types
    cacheConfig.setClassLoader(EhcacheSMLocalStoreFactory.class.getClassLoader());

    // wire up config
    if (config.getMaxCountLocalHeap() > 0) {
      // this is due to the meta mapping we put in the shadow cache
      cacheConfig.setMaxEntriesLocalHeap(config.getMaxCountLocalHeap() * 2 + 1);
    }

    if (config.getMaxBytesLocalHeap() > 0) {
      cacheConfig.setMaxBytesLocalHeap(config.getMaxBytesLocalHeap());
    }

    cacheConfig.setOverflowToOffHeap(config.isOverflowToOffheap());
    if (config.isOverflowToOffheap()) {
      long maxBytesLocalOffHeap = config.getMaxBytesLocalOffheap();
      if (maxBytesLocalOffHeap > 0) {
        cacheConfig.setMaxBytesLocalOffHeap(maxBytesLocalOffHeap);
      }
    }

    if (config.isPinnedInLocalMemory()) {
      // pin elements in local shadow cache.
      cacheConfig.pinning(new PinningConfiguration().store(PinningConfiguration.Store.LOCALMEMORY));
    }

    return new Cache(cacheConfig);
  }
}