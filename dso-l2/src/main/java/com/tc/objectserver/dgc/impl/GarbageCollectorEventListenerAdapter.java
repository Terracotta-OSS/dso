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

import com.tc.objectserver.dgc.api.GarbageCollectionInfo;
import com.tc.objectserver.dgc.api.GarbageCollectorEventListener;
import com.tc.util.ObjectIDSet;

import java.io.Serializable;

public abstract class GarbageCollectorEventListenerAdapter implements GarbageCollectorEventListener, Serializable {

  @Override
  public void garbageCollectorCompleted(GarbageCollectionInfo info) {
    // do nothing
  }

  @Override
  public void garbageCollectorCycleCompleted(GarbageCollectionInfo info, ObjectIDSet toDelete) {
    //
  }

  @Override
  public void garbageCollectorMarkComplete(GarbageCollectionInfo info) {
    //
  }

  @Override
  public void garbageCollectorMark(GarbageCollectionInfo info) {
    //
  }

  @Override
  public void garbageCollectorMarkResults(GarbageCollectionInfo info) {
    //
  }

  @Override
  public void garbageCollectorPaused(GarbageCollectionInfo info) {
    //
  }

  @Override
  public void garbageCollectorPausing(GarbageCollectionInfo info) {
    //
  }

  @Override
  public void garbageCollectorRescue1Complete(GarbageCollectionInfo info) {
    //
  }

  @Override
  public void garbageCollectorRescue2Start(GarbageCollectionInfo info) {
    //
  }

  @Override
  public void garbageCollectorStart(GarbageCollectionInfo info) {
    //
  }

  @Override
  public void garbageCollectorCanceled(GarbageCollectionInfo info) {
    //
  }
}
