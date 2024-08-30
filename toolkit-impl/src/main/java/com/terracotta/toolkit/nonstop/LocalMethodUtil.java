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

import org.terracotta.toolkit.ToolkitObjectType;

import com.terracotta.toolkit.collections.map.AggregateServerMap;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class LocalMethodUtil {
  private static final Map<ToolkitObjectType, Set<String>> localMethods = new HashMap<ToolkitObjectType, Set<String>>();
  static {
    Set<String> cacheLocalMethodSet = new HashSet<String>();
    cacheLocalMethodSet.add("unsafeLocalGet");
    cacheLocalMethodSet.add("containsLocalKey");
    cacheLocalMethodSet.add("localSize");
    cacheLocalMethodSet.add("localKeySet");
    cacheLocalMethodSet.add("localOnHeapSizeInBytes");
    cacheLocalMethodSet.add("localOffHeapSizeInBytes");
    cacheLocalMethodSet.add("localOnHeapSize");
    cacheLocalMethodSet.add("localOffHeapSize");
    cacheLocalMethodSet.add("containsKeyLocalOnHeap");
    cacheLocalMethodSet.add("containsKeyLocalOffHeap");
    cacheLocalMethodSet.add("disposeLocally");
    // Non-stop in search is handled separately, and there is a separate timeout setting for it!
    cacheLocalMethodSet.add("createQueryBuilder");
    cacheLocalMethodSet.add("executeQuery");
    validateMethodNamesExist(AggregateServerMap.class, cacheLocalMethodSet);
    localMethods.put(ToolkitObjectType.CACHE, cacheLocalMethodSet);
    localMethods.put(ToolkitObjectType.STORE, cacheLocalMethodSet);
  }

  static boolean isLocal(ToolkitObjectType objectType, String methodName) {
    Set<String> set = localMethods.get(objectType);
    if (set == null) { return false; }
    return set.contains(methodName);
  }

  private static void validateMethodNamesExist(Class klazz, Set<String> methodToCheck) {
    for (String methodName : methodToCheck) {
      if (!exist(klazz, methodName)) { throw new AssertionError("Method " + methodName + " does not exist in class "
                                                                + klazz.getName()); }
    }
  }

  private static boolean exist(Class klazz, String method) {
    Method[] methods = klazz.getMethods();
    for (Method m : methods) {
      if (m.getName().equals(method)) { return true; }
    }
    return false;
  }

}
