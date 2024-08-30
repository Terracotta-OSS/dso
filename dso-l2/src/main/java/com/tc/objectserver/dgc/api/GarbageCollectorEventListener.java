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
package com.tc.objectserver.dgc.api;

import com.tc.util.ObjectIDSet;

public interface GarbageCollectorEventListener {

  public void garbageCollectorStart(GarbageCollectionInfo info);

  public void garbageCollectorMark(GarbageCollectionInfo info);

  public void garbageCollectorMarkResults(GarbageCollectionInfo info);

  public void garbageCollectorRescue1Complete(GarbageCollectionInfo info);

  public void garbageCollectorPausing(GarbageCollectionInfo info);

  public void garbageCollectorPaused(GarbageCollectionInfo info);

  public void garbageCollectorRescue2Start(GarbageCollectionInfo info);

  public void garbageCollectorMarkComplete(GarbageCollectionInfo info);

  public void garbageCollectorCycleCompleted(GarbageCollectionInfo info, ObjectIDSet toDelete);

  public void garbageCollectorCompleted(GarbageCollectionInfo info);

  public void garbageCollectorCanceled(GarbageCollectionInfo info);

}
