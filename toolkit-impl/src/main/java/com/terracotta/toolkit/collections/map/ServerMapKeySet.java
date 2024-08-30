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
package com.terracotta.toolkit.collections.map;

import com.google.common.base.Preconditions;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Set;

public class ServerMapKeySet<K, V> extends AbstractSet<K> {

  private final ServerMap<K, V> map;
  private final Set<K>          delegateKeySet;

  public ServerMapKeySet(ServerMap<K, V> clusteredMap, final Set<K> delegate) {
    map = clusteredMap;
    this.delegateKeySet = delegate;
  }

  @Override
  public int size() {
    return map.size();
  }

  @Override
  public boolean contains(final Object key) {
    return map.containsKey(key);
  }

  @Override
  public void clear() {
    map.clear();
  }

  @Override
  public boolean remove(final Object key) {
    return map.remove(key) != null;
  }

  @Override
  public Iterator<K> iterator() {
    return new KeyIterator<K, V>(map, this.delegateKeySet.iterator());
  }

  private class KeyIterator<KI, VI> implements Iterator<KI> {

    private final ServerMap<K, V> clusteredMap;
    private final Iterator<KI>    delegate;
    private KI                    lastKey;

    public KeyIterator(ServerMap<K, V> clusteredMap, final Iterator<KI> delegate) {
      this.clusteredMap = clusteredMap;
      this.delegate = delegate;
    }

    @Override
    public boolean hasNext() {
      return this.delegate.hasNext();
    }

    @Override
    public KI next() {
      final KI result = this.delegate.next();
      this.lastKey = result;
      return result;
    }

    @Override
    public void remove() {
      Preconditions.checkState(null != this.lastKey, "next needs to be called before calling remove");
      clusteredMap.remove(this.lastKey);
      this.lastKey = null;
    }

  }

}
