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
package com.tc.management.stats;

import java.util.LinkedHashMap;
import java.util.Map;

public class LRUMap extends LinkedHashMap {
  private final static int NO_LIMIT = -1;

  private final int maxSize;

  public LRUMap() {
    this(NO_LIMIT);
  }

  public LRUMap(int maxSize) {
    super(100, 0.75f, true);
    this.maxSize = maxSize;
  }

  protected boolean removeEldestEntry(Map.Entry eldest) {
    if (maxSize != NO_LIMIT) {
      return size() >= this.maxSize;
    } else {
      return false;
    }
  }
}
