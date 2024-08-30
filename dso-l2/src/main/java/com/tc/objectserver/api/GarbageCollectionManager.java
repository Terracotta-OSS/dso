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
package com.tc.objectserver.api;

import com.tc.async.api.PostInit;
import com.tc.l2.state.StateChangeListener;
import com.tc.object.ObjectID;
import com.tc.objectserver.dgc.api.GarbageCollector.GCType;

import java.util.Set;
import java.util.SortedSet;

public interface GarbageCollectionManager extends PostInit, StateChangeListener {

  void deleteObjects(SortedSet<ObjectID> objects, final Set<ObjectID> checkouts);

  void inlineCleanup();

  void scheduleInlineGarbageCollectionIfNecessary();

  /**
   * Schedule a garbage collect to run asynchronously.
   */
  void scheduleGarbageCollection(GCType type, long delay);

  /**
   * Run a garbage collect synchronously.
   */
  void doGarbageCollection(GCType type);

  void scheduleGarbageCollection(GCType type);
}
