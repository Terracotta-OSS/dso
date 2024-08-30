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
package com.tc.util.runtime;

import com.google.common.collect.MapMaker;
import com.tc.object.locks.ThreadID;

import java.util.Map;

public class ThreadIDMapImpl implements ThreadIDMap {
  private final Map<Long, ThreadID> id2ThreadIDMap = new MapMaker().weakValues().makeMap();

  @Override
  public synchronized void addTCThreadID(final ThreadID tcThreadID) {
    id2ThreadIDMap.put(Long.valueOf(Thread.currentThread().getId()), tcThreadID);
  }

  @Override
  public synchronized ThreadID getTCThreadID(final Long javaThreadId) {
    return id2ThreadIDMap.get(javaThreadId);
  }

  /** For testing only - not in interface */
  public synchronized int getSize() {
    return id2ThreadIDMap.size();
  }

}
