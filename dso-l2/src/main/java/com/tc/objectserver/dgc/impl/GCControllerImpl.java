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

import com.tc.management.beans.object.ObjectManagementMonitor.GCController;
import com.tc.objectserver.dgc.api.GarbageCollector;
import com.tc.objectserver.dgc.api.GarbageCollector.GCType;

public class GCControllerImpl implements GCController {

  private final GarbageCollector collector;

  public GCControllerImpl(GarbageCollector collector) {
    this.collector = collector;
  }

  @Override
  public boolean isGCStarted() {
    return this.collector.isStarted();
  }

  @Override
  public boolean isGCDisabled() {
    return this.collector.isDisabled();
  }

  @Override
  public void startGC() {
    this.collector.doGC(GCType.FULL_GC);
  }

}
