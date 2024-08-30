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

import org.terracotta.toolkit.internal.cache.VersionedValue;

public class VersionedValueImpl<V> implements VersionedValue<V> {

  private final V    value;
  private final long version;

  public VersionedValueImpl(V value, long version) {
    this.value = value;
    this.version = version;
  }

  @Override
  public V getValue() {
    return this.value;
  }

  @Override
  public long getVersion() {
    return this.version;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final VersionedValueImpl that = (VersionedValueImpl)o;

    return version == that.version && value.equals(that.value);

  }

  @Override
  public int hashCode() {
    int result = value.hashCode();
    result = 31 * result + (int)(version ^ (version >>> 32));
    return result;
  }
}
