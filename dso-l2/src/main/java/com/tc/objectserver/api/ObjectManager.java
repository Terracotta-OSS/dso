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
package com.tc.objectserver.api;

import com.tc.net.NodeID;
import com.tc.object.ObjectID;
import com.tc.objectserver.context.DGCResultContext;
import com.tc.objectserver.context.ObjectManagerResultsContext;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.objectserver.dgc.api.GarbageCollector;
import com.tc.util.ObjectIDSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Manages all access to objects on the server. This will be single threaded and only accessed via it's event handler.
 */
public interface ObjectManager extends ManagedObjectProvider, ObjectManagerMBean {

  public void stop();

  /**
   * releases the object and commits the transaction, so that if anyone needs it they can have it
   */
  public void release(ManagedObject object);

  /**
   * release all objects
   */
  public void releaseAllReadOnly(Collection<ManagedObject> objects);

  /**
   * release for objects that can not have changed while checked out
   */
  public void releaseReadOnly(ManagedObject object);

  /**
   * Release all objects in the given collection and commits the transaction too.
   *
   * @param collection
   */
  public void releaseAll(Collection<ManagedObject> collection);

  /**
   * Looks up the objects associated with the Object Lookups from the clients. What it does is if all the objects are
   * available it calls setResult() o ObjectManagerResultsContext. If not then it calls makesPending on
   * ObjectManagerResultsContext and hangs on to the request until it can be fulfilled.
   * 
   * @param nodeID - nodeID of the client that is interested in lookup
   * @param maxCount - max number of objects reachable from the requested objects that should be looked up
   * @param responseContext - ResultContext that gets notifications.
   * @return true if all the objects are successfully looked up.
   */
  public boolean lookupObjectsAndSubObjectsFor(NodeID nodeID, ObjectManagerResultsContext responseContext, int maxCount);

  /**
   * Looks up the objects associated with the transaction. What it does is if all the objects are available to be
   * updated it calls setResult() on ObjectManagerResultsContext. If not then it calls makesPending on
   * ObjectManagerResultsContext and hangs on to the request until it can be fulfilled.
   * 
   * @param nodeID - nodeID of the client that is interested in lookup
   * @param context - ResultContext that gets notifications.
   * @return true if all the objects are successfully looked up.
   */
  public boolean lookupObjectsFor(NodeID nodeID, ObjectManagerResultsContext context);

  /**
   * The list of root names
   * 
   * @return
   */
  @Override
  public Iterator getRoots();

  public Map getRootNamesToIDsMap();

  public void createRoot(String name, ObjectID id);

  public void createNewObjects(Set<ObjectID> ids);

  @Override
  public ObjectID lookupRootID(String name);

  public GarbageCollector getGarbageCollector();

  public void setGarbageCollector(GarbageCollector gc);

  /**
   * This method return a set of ids that are the children of the param
   * 
   * @param id - to return children of
   * @param cacheOnly - return set if only in cache.
   */
  public Set<ObjectID> getObjectReferencesFrom(ObjectID id, boolean cacheOnly);

  /**
   * Called by DGC thread (in object manager)
   */
  public void waitUntilReadyToGC();

  @Override
  public int getLiveObjectCount();

  /**
   * Called by DGC thread (in object manager)
   * 
   * @param dgcResultContext
   */
  public void notifyGCComplete(DGCResultContext dgcResultContext);

  /**
   * Checkout and delete objects.
   *
   * @param objectsToDelete set of objects to delete
   * @return set of object ids that were not found.
   */
  public Set<ObjectID> deleteObjects(Set<ObjectID> objectsToDelete);

  /**
   * Try to checkout and delete objects. Will simply skip (and return) any objects
   * that fail to be checked out without blocking.
   *
   *
   * @param objectsToDelete set of objects to delete
   * @param checkedOutObjects
   * @return objects that are either missing or were unable to be checked out.
   */
  public Set<ObjectID> tryDeleteObjects(Set<ObjectID> objectsToDelete, final Set<ObjectID> checkedOutObjects);

  public void start();

  public int getCheckedOutCount();

  public Set getRootIDs();

  public ObjectIDSet getAllObjectIDs();

  public ObjectIDSet getObjectIDsInCache();

  /**
   * Check out an object read only
   *
   * @param id ObjectID of the object to be checked out
   * @return ManagedObject if it exists; null otherwise
   */
  public ManagedObject getObjectByIDReadOnly(ObjectID id);

}
