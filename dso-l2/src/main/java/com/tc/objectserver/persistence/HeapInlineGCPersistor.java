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
package com.tc.objectserver.persistence;

import com.tc.object.ObjectID;
import com.tc.util.BitSetObjectIDSet;

import java.util.Collection;
import java.util.Set;

/**
 * @author tim
 */
public class HeapInlineGCPersistor implements InlineGCPersistor {
  private final Set<ObjectID> set = new BitSetObjectIDSet();

  @Override
  public synchronized int size() {
    return set.size();
  }

  @Override
  public synchronized void addObjectIDs(final Collection<ObjectID> oids) {
    set.addAll(oids);
  }

  @Override
  public synchronized void removeObjectIDs(final Collection<ObjectID> objectIDs) {
    set.removeAll(objectIDs);
  }

  @Override
  public synchronized Set<ObjectID> allObjectIDs() {
    return new BitSetObjectIDSet(set);
  }
}
