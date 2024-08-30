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
package com.tc.objectserver.persistence;

import com.tc.objectserver.core.api.ManagedObjectState;
import com.tc.objectserver.managedobject.ManagedObjectStateStaticConfig;

public class PersistentCollectionsUtil {

  public static boolean isPersistableCollectionType(final byte type) {
    if (ManagedObjectStateStaticConfig.SERVER_MAP.getStateObjectType() == type) { return true; }
    switch (type) {
      case ManagedObjectState.MAP_TYPE:
      case ManagedObjectState.PARTIAL_MAP_TYPE:
      case ManagedObjectState.TOOLKIT_TYPE_ROOT_TYPE:
        return true;
      default:
        return false;
    }
  }

  public static boolean isNoReferenceObjectType(final byte type) {
    return type == ManagedObjectStateStaticConfig.SERIALIZED_CLUSTER_OBJECT.getStateObjectType();
  }

  public static boolean isEvictableMapType(final byte type) {
    if (ManagedObjectStateStaticConfig.SERVER_MAP.getStateObjectType() == type) { return true; }
    return false;
  }

}