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
package com.tc.objectserver.impl;

import com.tc.object.ObjectID;
import com.tc.objectserver.api.ObjectManagerLookupResults;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.util.ObjectIDSet;

import java.util.Map;

public class ObjectManagerLookupResultsImpl implements ObjectManagerLookupResults {

  private final Map<ObjectID, ManagedObject> objects;
  private final ObjectIDSet                  lookupPendingObjectIDs;
  private final ObjectIDSet                  missingObjectIDs;

  public ObjectManagerLookupResultsImpl(Map<ObjectID, ManagedObject> objects, ObjectIDSet lookupPendingObjectIDs,
                                        ObjectIDSet missingObjectIDs) {
    this.objects = objects;
    this.lookupPendingObjectIDs = lookupPendingObjectIDs;
    this.missingObjectIDs = missingObjectIDs;
  }

  @Override
  public Map<ObjectID, ManagedObject> getObjects() {
    return this.objects;
  }

  @Override
  public ObjectIDSet getLookupPendingObjectIDs() {
    return lookupPendingObjectIDs;
  }

  @Override
  public ObjectIDSet getMissingObjectIDs() {
    return missingObjectIDs;
  }
}
