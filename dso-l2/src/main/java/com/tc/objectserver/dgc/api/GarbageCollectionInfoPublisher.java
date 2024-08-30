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

public interface GarbageCollectionInfoPublisher {

  public void removeListener(GarbageCollectorEventListener listener);

  public void addListener(GarbageCollectorEventListener listener);

  public void fireGCStartEvent(GarbageCollectionInfo info);

  public void fireGCMarkEvent(GarbageCollectionInfo info);

  public void fireGCMarkResultsEvent(GarbageCollectionInfo info);

  public void fireGCRescue1CompleteEvent(GarbageCollectionInfo info);

  public void fireGCPausingEvent(GarbageCollectionInfo info);

  public void fireGCPausedEvent(GarbageCollectionInfo info);

  public void fireGCRescue2StartEvent(GarbageCollectionInfo info);

  public void fireGCMarkCompleteEvent(GarbageCollectionInfo info);

  public void fireGCCycleCompletedEvent(GarbageCollectionInfo info, ObjectIDSet toDelete);

  public void fireGCCompletedEvent(GarbageCollectionInfo info);

  public void fireGCCanceledEvent(GarbageCollectionInfo info);
}
