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
package com.tc.object;

import com.tc.net.NodeID;
import com.tc.object.servermap.localcache.AbstractLocalCacheStoreValue;
import com.tc.object.servermap.localcache.L1ServerMapLocalCacheStore;
import com.tc.util.ObjectIDSet;

import java.util.Set;

public interface TCObjectSelfStore extends ClearableCallback {
  void initializeTCObjectSelfStore(TCObjectSelfCallback callback);

  Object getById(ObjectID oid);

  boolean addTCObjectSelf(L1ServerMapLocalCacheStore store, AbstractLocalCacheStoreValue localStoreValue,
                          Object tcoself, boolean isNew);

  int size();

  void addAllObjectIDs(Set oids);

  boolean contains(ObjectID objectID);

  void addTCObjectSelfTemp(TCObjectSelf obj);

  void removeTCObjectSelfTemp(TCObjectSelf value, boolean notifyServer);

  public void removeTCObjectSelf(AbstractLocalCacheStoreValue localStoreValue);

  void initializeTCObjectSelfIfRequired(TCObjectSelf tcoSelf);

  public void removeObjectById(ObjectID oid);

  /**
   * Handshake manager tries to get hold of all the objects present in the local caches
   *
   * @param remoteNode
   */
  public ObjectIDSet getObjectIDsToValidate(NodeID remoteNode);

  void shutdown(boolean fromShutdownHook);

  void rejoinInProgress(boolean rejoinInProgress);

  void removeTCObjectSelf(TCObjectSelf self);
}
