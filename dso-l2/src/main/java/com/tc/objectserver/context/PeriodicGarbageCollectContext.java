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
package com.tc.objectserver.context;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.objectserver.dgc.api.GarbageCollector.GCType;

public class PeriodicGarbageCollectContext extends GarbageCollectContext {
  private static final TCLogger logger = TCLogging.getLogger(PeriodicGarbageCollectContext.class);
  private final long            interval;

  public PeriodicGarbageCollectContext(final GCType type, final long interval) {
    super(type, interval);
    this.interval = interval;
  }

  @Override
  public void done() {
    // do nothing
  }

  @Override
  public void waitForCompletion() {
    logger.warn("Attempted to wait for completion on Periodic garbage collection.");
  }

  public long getInterval() {
    return interval;
  }

  public void reset() {
    setDelay(interval);
  }
}
