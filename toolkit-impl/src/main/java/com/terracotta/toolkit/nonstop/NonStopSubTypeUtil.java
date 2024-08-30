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
package com.terracotta.toolkit.nonstop;

import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;

public class NonStopSubTypeUtil {

  private static Set<Class> SUPPORTED_SUB_TYPES = new HashSet<Class>();

  static {
    SUPPORTED_SUB_TYPES.add(Iterator.class);
    SUPPORTED_SUB_TYPES.add(ListIterator.class);
    SUPPORTED_SUB_TYPES.add(Collection.class);
    SUPPORTED_SUB_TYPES.add(Set.class);
    SUPPORTED_SUB_TYPES.add(List.class);
    SUPPORTED_SUB_TYPES.add(Map.class);
    SUPPORTED_SUB_TYPES.add(SortedMap.class);
    SUPPORTED_SUB_TYPES.add(SortedSet.class);
    SUPPORTED_SUB_TYPES.add(ToolkitLock.class);
    SUPPORTED_SUB_TYPES.add(ToolkitReadWriteLock.class);
  }

  public static boolean isNonStopSubtype(Class klazz) {
    return SUPPORTED_SUB_TYPES.contains(klazz);
  }
}
