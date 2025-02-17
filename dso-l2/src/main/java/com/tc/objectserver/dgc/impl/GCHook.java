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

import com.tc.object.ObjectID;
import com.tc.objectserver.core.api.Filter;
import com.tc.objectserver.core.impl.GarbageCollectionID;
import com.tc.objectserver.dgc.api.GarbageCollectionInfo;
import com.tc.util.ObjectIDSet;

import java.util.Set;

public interface GCHook {

  public ObjectIDSet getGCCandidates();

  public ObjectIDSet getRootObjectIDs(ObjectIDSet candidateIDs);

  public int getLiveObjectCount();

  public GarbageCollectionInfo createGCInfo(GarbageCollectionID id);

  public String getDescription();

  public void startMonitoringReferenceChanges();

  public void stopMonitoringReferenceChanges();

  public Filter getCollectCycleFilter(Set candidateIDs);

  public void waitUntilReadyToGC();

  public Set<ObjectID> getObjectReferencesFrom(ObjectID id);

  public ObjectIDSet getRescueIDs();

}