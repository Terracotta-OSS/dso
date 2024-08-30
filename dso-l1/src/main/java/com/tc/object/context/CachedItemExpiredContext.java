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
package com.tc.object.context;

import com.tc.async.api.EventContext;
import com.tc.object.servermap.localcache.AbstractLocalCacheStoreValue;
import com.tc.object.servermap.localcache.ServerMapLocalCache;

public class CachedItemExpiredContext implements EventContext {

  private final ServerMapLocalCache          serverMapLocalCache;
  private final Object                       key;
  private final AbstractLocalCacheStoreValue value;

  public CachedItemExpiredContext(final ServerMapLocalCache serverMapLocalCache, final Object key,
                                  final AbstractLocalCacheStoreValue value) {
    this.serverMapLocalCache = serverMapLocalCache;
    this.key = key;
    this.value = value;
  }

  public ServerMapLocalCache getServerMapLocalCache() {
    return this.serverMapLocalCache;
  }

  public Object getKey() {
    return this.key;
  }

  public AbstractLocalCacheStoreValue getValue() {
    return this.value;
  }

}
