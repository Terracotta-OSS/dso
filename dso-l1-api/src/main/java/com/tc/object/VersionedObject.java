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
package com.tc.object;

import com.google.common.base.MoreObjects;

public class VersionedObject {

  private final Object object;
  private final long   version;

  public VersionedObject(final Object object, final long version) {
    this.object = object;
    this.version = version;
  }

  public Object getObject() {
    return this.object;
  }

  public long getVersion() {
    return this.version;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("object", object)
        .add("version", version)
        .toString();
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final VersionedObject that = (VersionedObject)o;

    return version == that.version && object.equals(that.object);

  }

  @Override
  public int hashCode() {
    int result = object.hashCode();
    result = 31 * result + (int)(version ^ (version >>> 32));
    return result;
  }
}
