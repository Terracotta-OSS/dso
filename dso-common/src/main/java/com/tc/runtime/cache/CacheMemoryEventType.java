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
package com.tc.runtime.cache;

public final class CacheMemoryEventType {

  public static final CacheMemoryEventType ABOVE_THRESHOLD          = new CacheMemoryEventType("ABOVE_THRESHOLD");
  public static final CacheMemoryEventType ABOVE_CRITICAL_THRESHOLD = new CacheMemoryEventType("ABOVE_CRITICAL_THRESHOLD");
  public static final CacheMemoryEventType BELOW_THRESHOLD          = new CacheMemoryEventType("BELOW_THRESHOLD");

  private final String                name;

  // Not exposed to anyone
  private CacheMemoryEventType(String name) {
    this.name = name;
  }

  @Override
  public String toString() {
    return "CacheMemoryEventType." + name;
  }

}
