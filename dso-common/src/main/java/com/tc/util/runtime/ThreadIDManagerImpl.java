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
package com.tc.util.runtime;

import com.tc.object.locks.ThreadID;
import com.tc.util.VicariousThreadLocal;

public class ThreadIDManagerImpl implements ThreadIDManager {

  private final ThreadLocal threadID;
  private long              threadIDSequence;
  private final ThreadIDMap threadIDMap;

  public ThreadIDManagerImpl(final ThreadIDMap threadIDMap) {
    this.threadID = new VicariousThreadLocal();
    this.threadIDMap = threadIDMap;
  }

  @Override
  public ThreadID getThreadID() {
    ThreadID rv = (ThreadID) threadID.get();
    if (rv == null) {
      rv = new ThreadID(nextThreadID(), Thread.currentThread().getName());
      threadIDMap.addTCThreadID(rv);
      threadID.set(rv);
    }
    return rv;
  }

  private synchronized long nextThreadID() {
    return ++threadIDSequence;
  }
}
