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
package com.tc.objectserver.dgc.impl;

import com.tc.objectserver.dgc.api.GarbageCollectionInfo;
import com.tc.util.ObjectIDSet;

public class GCLoggerEventPublisher extends GarbageCollectorEventListenerAdapter {

  private final GCLogger gcLogger;

  public GCLoggerEventPublisher(GCLogger logger) {
    gcLogger = logger;
  }

  @Override
  public void garbageCollectorStart(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_start(info.getGarbageCollectionID(), info.isFullGC());
  }

  @Override
  public void garbageCollectorMark(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_markStart(info.getGarbageCollectionID(), info.getBeginObjectCount());
  }

  @Override
  public void garbageCollectorMarkResults(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_markResults(info.getGarbageCollectionID(), info.getPreRescueCount());
  }

  @Override
  public void garbageCollectorRescue1Complete(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_rescue_complete(info.getGarbageCollectionID(), 1, info.getRescue1Count());
  }

  @Override
  public void garbageCollectorPausing(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_quiescing(info.getGarbageCollectionID());
  }

  @Override
  public void garbageCollectorPaused(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_paused(info.getGarbageCollectionID());
  }

  @Override
  public void garbageCollectorRescue2Start(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_rescue_start(info.getGarbageCollectionID(), 2, info.getCandidateGarbageCount());
  }

  @Override
  public void garbageCollectorMarkComplete(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_markComplete(info.getGarbageCollectionID(), info.getCandidateGarbageCount());
  }

  @Override
  public void garbageCollectorCycleCompleted(GarbageCollectionInfo info, ObjectIDSet toDelete) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_cycleComplete(info.getGarbageCollectionID(), info);
  }

  @Override
  public void garbageCollectorCompleted(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_complete(info.getGarbageCollectionID(), info.getActualGarbageCount(), info.getElapsedTime());
  }

  @Override
  public void garbageCollectorCanceled(GarbageCollectionInfo info) {
    if (info.isInlineDGC()) { return; }
    gcLogger.log_canceled(info.getGarbageCollectionID());
  }
}
