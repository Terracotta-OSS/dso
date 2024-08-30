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
package com.terracotta.toolkit.object.serialization;

import org.terracotta.toolkit.object.serialization.NotSerializableRuntimeException;

import com.tc.net.GroupID;

public interface SerializedClusterObjectFactory {

  /**
   * Creates serialized version of the object, that can be stored in the cluster, in the stripe denoted by gid
   * 
   * @throws NotSerializableRuntimeException if value is not serializable
   */
  SerializedClusterObject createSerializedClusterObject(Object value, GroupID gid)
      throws NotSerializableRuntimeException;

  /**
   * Creates serialized map value, that can be stored in the cluster, in the stripe denoted by gid
   */
  <T> SerializedMapValue<T> createSerializedMapValue(SerializedMapValueParameters<T> params, GroupID gid);
}
