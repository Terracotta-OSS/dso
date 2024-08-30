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
package com.tc.stats;

import com.tc.objectserver.api.GCStats;
import com.tc.objectserver.dgc.impl.GCStatsEventPublisher;
import com.tc.stats.api.DGCMBean;

import javax.management.NotCompliantMBeanException;

public class LocalDGCStats extends AbstractNotifyingMBean implements DGCMBean {

  private final GCStatsEventPublisher gcStatsPublisher;

  public LocalDGCStats(GCStatsEventPublisher gcStatsPublisher) throws NotCompliantMBeanException {
    super(DGCMBean.class);
    this.gcStatsPublisher = gcStatsPublisher;
  }

  @Override
  public GCStats[] getGarbageCollectorStats() {
    return this.gcStatsPublisher.getGarbageCollectorStats();
  }

  @Override
  public long getLastCollectionGarbageCount() {
    GCStats gcStats = gcStatsPublisher.getLastGarbageCollectorStats();
    return gcStats != null ? gcStats.getActualGarbageCount() : -1;
  }

  @Override
  public long getLastCollectionElapsedTime() {
    GCStats gcStats = gcStatsPublisher.getLastGarbageCollectorStats();
    return gcStats != null ? gcStats.getElapsedTime() : -1;
  }

  @Override
  public void reset() {
    // TODO: implement this?
  }
}
