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

import net.sf.ehcache.util.NamedThreadFactory;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class NonStopExecutor {
  public final ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(1,
      new NamedThreadFactory("NonStopExecutor", true));

  public Future schedule(Runnable task, long timeout) {
    return executor.schedule(task, timeout, TimeUnit.MILLISECONDS);
  }

  public void remove(Future future) {
    if (future instanceof Runnable) {
      executor.remove((Runnable) future);
    }
  }

  public void shutdown() {
    executor.shutdown();
  }

}
