/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
package com.terracotta.toolkit.roots.impl;

public abstract class ToolkitTypeConstants {

  private static final String PREFIX                           = "__toolkit@";

  public static final String  TOOLKIT_LIST_ROOT_NAME           = PREFIX + "toolkitListRoot";
  public static final String  TOOLKIT_MAP_ROOT_NAME            = PREFIX + "toolkitMapRoot";
  public static final String  TOOLKIT_SORTED_MAP_ROOT_NAME     = PREFIX + "toolkitSortedMapRoot";
  public static final String  TOOLKIT_STORE_ROOT_NAME          = PREFIX + "toolkitStoreRoot";
  public static final String  TOOLKIT_CACHE_ROOT_NAME          = PREFIX + "toolkitCacheRoot";
  public static final String  TOOLKIT_ATOMIC_LONG_MAP_NAME     = PREFIX + "toolkitAtomicLongMap";
  public static final String  TOOLKIT_BARRIER_MAP_NAME         = PREFIX + "toolkitBarrierMap";
  public static final String  TOOLKIT_NOTIFIER_ROOT_NAME       = PREFIX + "toolkitNotifierRoot";
  public static final String  TOOLKIT_SET_ROOT_NAME            = PREFIX + "toolkitSetRoot";
  public static final String  SERIALIZER_MAP_ROOT_NAME         = PREFIX + "serializerMapRoot";
  public static final String  TOOLKIT_BLOCKING_QUEUE_ROOT_NAME = PREFIX + "toolkitBlockingQueueRoot";
  public static final String  TOOLKIT_SORTED_SET_ROOT_NAME     = PREFIX + "toolkitSortedSetRoot";
  public static final String  TOOLKIT_BARRIER_UID_NAME         = PREFIX + "__tc__toolkit_barrier_uid@";
  public static final String  TOOLKIT_LONG_UID_NAME            = PREFIX + "__tc__toolkit_long_uid@";

}
