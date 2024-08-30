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
package com.tc.object.servermap.localcache;

import java.util.Map;

/**
 * This interface would be called when an eviction happens in the L1ServerMapLocalStore.<br>
 * Currently this interface should be called only when:<br>
 * 1) capacity eviction happens<br>
 * 2) evict (count) gets called on L1ServerMapLocalStore<br>
 */
public interface L1ServerMapLocalCacheStoreListener<K, V> {

  /**
   * When a key gets evicted.
   */
  public void notifyElementEvicted(K key, V value);

  /**
   * When a set if keys get evicted.
   */
  public void notifyElementsEvicted(Map<K, V> evictedElements);

  /**
   * When a key is expired
   */
  public void notifyElementExpired(K key, V value);

  /**
   * Called whenever a store is diposed of
   */
  public void notifyDisposed(L1ServerMapLocalCacheStore store);
}
