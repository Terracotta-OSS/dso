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
package com.tc.objectserver.l1.api;

import com.tc.invalidation.Invalidations;
import com.tc.net.NodeID;
import com.tc.object.ObjectID;
import com.tc.object.dna.api.DNA;
import com.tc.objectserver.managedobject.ApplyTransactionInfo;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Client State Manager Interface
 */
public interface ClientStateManager {

  /**
   * Initializes the internal data structures for newly connected client
   * 
   * @return boolean true if this newly connecting client can be accepted, else false
   */
  public boolean startupNode(NodeID nodeID);

  /**
   * Clears internal data structures for disconnected clients
   */
  public void shutdownNode(NodeID deadNode);

  /**
   * The the server representation of the client's state now knows that clientID has a reference to objectID
   * 
   * @return boolean true of the reference was added.  False if the object was already referenced
   */
  public boolean addReference(NodeID nodeID, ObjectID objectID);

  /**
   * From the local state of the l1 named nodeID remove all the objectIDs that are references and also remove from the
   * requested list any refrence already present
   * 
   * @param nodeID nodeID of the client requesting the objects
   * @param removed set of objects removed from the client
   * @param requested set of Objects requested, this set is mutated to remove any object that is already present in the
   *        client.
   */
  public void removeReferences(NodeID nodeID, Set<ObjectID> removed, Set<ObjectID> requested);

  public boolean hasReference(NodeID nodeID, ObjectID objectID);

  /**
   * Prunes the changes list down to include only changes for objects the given client has.
   */
  public List<DNA> createPrunedChangesAndAddObjectIDTo(Collection<DNA> changes, ApplyTransactionInfo references,
                                                       NodeID clientID, Set<ObjectID> objectIDs,
                                                       Invalidations invalidationsForClient);

  public Set<ObjectID> addAllReferencedIdsTo(Set<ObjectID> rescueIds);

  public void removeReferencedFrom(NodeID nodeID, Set<ObjectID> secondPass);

  public Set<ObjectID> addReferences(NodeID nodeID, Set<ObjectID> oids);

  public int getReferenceCount(NodeID nodeID);

  public Set<NodeID> getConnectedClientIDs();

  public void registerObjectReferenceAddListener(ObjectReferenceAddListener listener);

  public void unregisterObjectReferenceAddListener(ObjectReferenceAddListener listener);
}
