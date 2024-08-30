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

import org.terracotta.toolkit.ToolkitObjectType;

import com.terracotta.toolkit.util.ToolkitObjectStatus;

import java.util.Comparator;
import java.util.SortedMap;

public class SubTypeWrapperSortedMap<K, V> extends SubTypeWrapperMap<K, V> implements SortedMap<K, V> {
  private final SortedMap<K, V> sortedMap;

  public SubTypeWrapperSortedMap(SortedMap<K, V> map, ToolkitObjectStatus status, String superTypeName,
                                 ToolkitObjectType toolkitObjectType) {
    super(map, status, superTypeName, toolkitObjectType);
    this.sortedMap = map;
  }

  @Override
  public Comparator<? super K> comparator() {
    assertStatus();
    return sortedMap.comparator();
  }

  @Override
  public SortedMap<K, V> subMap(K fromKey, K toKey) {
    assertStatus();
    return new SubTypeWrapperSortedMap<K, V>(sortedMap.subMap(fromKey, toKey), status, superTypeName, toolkitObjectType);
  }

  @Override
  public SortedMap<K, V> headMap(K toKey) {
    assertStatus();
    return new SubTypeWrapperSortedMap<K, V>(sortedMap.headMap(toKey), status, superTypeName, toolkitObjectType);
  }

  @Override
  public SortedMap<K, V> tailMap(K fromKey) {
    assertStatus();
    return new SubTypeWrapperSortedMap<K, V>(sortedMap.tailMap(fromKey), status, superTypeName, toolkitObjectType);
  }

  @Override
  public K firstKey() {
    assertStatus();
    return sortedMap.firstKey();
  }

  @Override
  public K lastKey() {
    assertStatus();
    return sortedMap.lastKey();
  }

}