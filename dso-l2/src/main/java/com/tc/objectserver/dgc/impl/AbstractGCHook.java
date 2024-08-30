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
package com.tc.objectserver.dgc.impl;

import com.tc.objectserver.api.ObjectManager;
import com.tc.objectserver.l1.api.ClientStateManager;

public abstract class AbstractGCHook extends DGCRequestThrottler implements GCHook {
  protected final MarkAndSweepGarbageCollector collector;
  protected final ClientStateManager           stateManager;
  protected final boolean                      inlineCleanup;

  protected AbstractGCHook(MarkAndSweepGarbageCollector collector, ObjectManager objectManager,
                           ClientStateManager stateManager, boolean quiet) {
    super(objectManager);
    this.collector = collector;
    this.stateManager = stateManager;
    this.inlineCleanup = quiet;
  }

  @Override
  public void startMonitoringReferenceChanges() {
    this.collector.startMonitoringReferenceChanges();
  }

  @Override
  public void stopMonitoringReferenceChanges() {
    this.collector.stopMonitoringReferenceChanges();
  }

  @Override
  public void waitUntilReadyToGC() {
    this.objectManager.waitUntilReadyToGC();
  }

  @Override
  public int getLiveObjectCount() {
    return this.objectManager.getLiveObjectCount();
  }
}