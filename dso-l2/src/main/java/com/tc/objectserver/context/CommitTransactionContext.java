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
import com.tc.object.tx.ServerTransactionID;
import com.tc.objectserver.core.api.ManagedObject;

import java.util.Collection;
import java.util.Map;
import java.util.SortedSet;

public class CommitTransactionContext implements EventContext {

  private Collection<ServerTransactionID> txnIDs;
  private Collection<ManagedObject>       objects;
  private Map<String, ObjectID>           newRoots;
  private SortedSet<ObjectID>             deletedObjects;
  private boolean                         isInitialized = false;

  public CommitTransactionContext() {
    // Empty constructor
  }

  public void initialize(final Collection<ServerTransactionID> appliedTxnIDs,
                         final Collection<ManagedObject> appliedObjects,
                         final Map<String, ObjectID> newRootsInAppliedTxns, final SortedSet<ObjectID> myDeletedObjects) {
    this.txnIDs = appliedTxnIDs;
    this.objects = appliedObjects;
    this.newRoots = newRootsInAppliedTxns;
    this.deletedObjects = myDeletedObjects;
    isInitialized = true;
  }

  public boolean isInitialized() {
    return isInitialized;
  }

  public Collection<ManagedObject> getObjects() {
    return objects;
  }

  public Collection<ServerTransactionID> getAppliedServerTransactionIDs() {
    return txnIDs;
  }

  public Map<String, ObjectID> getNewRoots() {
    return newRoots;
  }

  public SortedSet<ObjectID> getDeletedObjects() {
    return deletedObjects;
  }
}
