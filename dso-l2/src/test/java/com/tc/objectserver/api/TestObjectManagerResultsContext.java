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

import com.tc.object.ObjectID;
import com.tc.objectserver.context.ObjectManagerResultsContext;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.util.BitSetObjectIDSet;
import com.tc.util.ObjectIDSet;

import java.util.Map;

public class TestObjectManagerResultsContext implements ObjectManagerResultsContext {

  private final Map<ObjectID, ManagedObject> results;
  private final ObjectIDSet objectIDs;

  public TestObjectManagerResultsContext(Map<ObjectID, ManagedObject> results, ObjectIDSet objectIDs) {
    this.results = results;
    this.objectIDs = objectIDs;
  }

  public Map getResults() {
    return results;
  }

  @Override
  public void setResults(ObjectManagerLookupResults results) {
    this.results.putAll(results.getObjects());
    if (!results.getMissingObjectIDs().isEmpty()) { throw new AssertionError("Missing Objects : "
                                                                             + results.getMissingObjectIDs()); }
  }

  @Override
  public ObjectIDSet getLookupIDs() {
    return objectIDs;
  }

  @Override
  public ObjectIDSet getNewObjectIDs() {
    return new BitSetObjectIDSet();
  }

  public boolean updateStats() {
    return true;
  }

}