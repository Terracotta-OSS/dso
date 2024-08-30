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
package com.tc.object.servermap.localcache.impl;

import com.tc.object.ObjectID;
import com.tc.object.TCObjectSelf;
import com.tc.object.TCObjectSelfCallback;
import com.tc.util.BitSetObjectIDSet;

import java.util.Set;

public class MockTCObjectSelfCallback implements TCObjectSelfCallback {
  private final Set<ObjectID> oids = new BitSetObjectIDSet();

  @Override
  public void initializeTCClazzIfRequired(TCObjectSelf tcoObjectSelf) {
    // NO OP
    // We do not have tc class factory here
  }

  @Override
  public synchronized void removedTCObjectSelfFromStore(TCObjectSelf tcoObjectSelf) {
    oids.add(tcoObjectSelf.getObjectID());
  }

  public synchronized Set<ObjectID> getRemovedSet() {
    return oids;
  }

  public void removedTCObjectSelfFromStore(ObjectID objectID) {
    oids.add(objectID);
  }
}