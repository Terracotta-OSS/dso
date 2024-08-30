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
package com.tc.objectserver.impl;

import com.tc.object.ObjectID;
import com.tc.objectserver.persistence.ManagedObjectPersistor;
import com.tc.util.BitSetObjectIDSet;
import com.tc.util.ObjectIDSet;

import java.util.Arrays;

import junit.framework.TestCase;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class PersistentManagedObjectStoreTest extends TestCase {

  private ManagedObjectPersistor persistor;
  private PersistentManagedObjectStore objectStore;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    persistor = mock(ManagedObjectPersistor.class);
    objectStore = new PersistentManagedObjectStore(persistor);
  }

  public void testGetObjectByID() throws Exception {
    ObjectID objectID = new ObjectID(1);
    objectStore.getObjectByID(objectID);
    verify(persistor).loadObjectByID(objectID);
  }

  public void testContainsObject() throws Exception {
    ObjectID objectID = new ObjectID(1);
    objectStore.containsObject(objectID);
    verify(persistor).containsObject(objectID);
  }

  public void testRemoveObjectsByID() throws Exception {
    ObjectIDSet objectIDs = new BitSetObjectIDSet(Arrays.asList(new ObjectID(1), new ObjectID(2)));
    objectStore.removeAllObjectsByID(objectIDs);
    verify(persistor).deleteAllObjects(objectIDs);
  }
}
