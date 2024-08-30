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
package com.tc.objectserver.impl;

import com.tc.objectserver.api.ObjectManagerStats;
import com.tc.objectserver.api.ObjectManagerStatsListener;
import com.tc.stats.counter.sampled.SampledCounter;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Implements the object manager stats
 */
public class ObjectManagerStatsImpl implements ObjectManagerStatsListener, ObjectManagerStats {

  private final AtomicLong     objectsCreated = new AtomicLong();
  private final SampledCounter newObjectCounter;

  public ObjectManagerStatsImpl(SampledCounter newObjectCounter) {
    this.newObjectCounter = newObjectCounter;
  }

  @Override
  public void newObjectCreated() {
    this.objectsCreated.incrementAndGet();
    this.newObjectCounter.increment();
  }

  @Override
  public long getTotalObjectsCreated() {
    return this.objectsCreated.get();
  }

}
