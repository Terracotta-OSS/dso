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
package com.terracotta.toolkit.collections.servermap.api;

import java.util.List;

public interface ServerMapLocalStore<K, V> {

  public V put(K key, V value) throws ServerMapLocalStoreFullException;

  public V get(K key);

  public V remove(K key);

  public V remove(K key, V value);

  public void clear();

  public void cleanLocalState();

  public boolean addListener(ServerMapLocalStoreListener<K, V> listener);

  public boolean removeListener(ServerMapLocalStoreListener<K, V> listener);

  public List<K> getKeys();

  public int getMaxEntriesLocalHeap();

  public void setMaxEntriesLocalHeap(int newMaxEntriesLocalHeap);

  public void setMaxBytesLocalHeap(long newMaxBytesLocalHeap);

  public long getMaxBytesLocalHeap();

  public int getOffHeapSize();

  public int getOnHeapSize();

  public int getSize();

  public long getOnHeapSizeInBytes();

  public long getOffHeapSizeInBytes();

  public void dispose();

  public boolean containsKeyOnHeap(K key);

  public boolean containsKeyOffHeap(K key);

  public void recalculateSize(K key);

  public boolean isPinned();
}
