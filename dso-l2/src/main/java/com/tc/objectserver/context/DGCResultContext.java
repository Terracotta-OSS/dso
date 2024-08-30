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

import com.tc.async.api.EventContext;
import com.tc.object.ObjectID;
import com.tc.objectserver.dgc.api.GarbageCollectionInfo;

import java.util.SortedSet;

public class DGCResultContext implements EventContext {
  private final SortedSet<ObjectID>   garbage;
  private final GarbageCollectionInfo info;

  public DGCResultContext(final SortedSet<ObjectID> garbage, final GarbageCollectionInfo info) {
    this.garbage = garbage;
    this.info = info;
  }

  public SortedSet<ObjectID> getGarbageIDs() {
    return garbage;
  }

  public int getGCIterationCount() {
    return this.info.getIteration();
  }

  public GarbageCollectionInfo getGCInfo() {
    return this.info;
  }

  @Override
  public String toString() {
    return "DGCResultContext [ " + this.info.getIteration() + " , " + getGarbageIDs().size() + " ]";
  }
}
