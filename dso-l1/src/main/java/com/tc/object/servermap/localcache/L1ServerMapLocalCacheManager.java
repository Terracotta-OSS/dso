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
package com.tc.object.servermap.localcache;

import com.tc.object.ClientObjectManager;
import com.tc.object.ObjectID;
import com.tc.object.TCObjectSelfStore;
import com.tc.object.servermap.localcache.impl.L1ServerMapLocalStoreTransactionCompletionListener;
import com.tc.platform.PlatformService;
import com.tc.util.ObjectIDSet;

import java.util.Map;
import java.util.Set;

/**
 * A Global cache manager which contains info about all the LocalCache present in the L1.<br>
 * This acts a multiplexer between RemoteServerMapManager, HandshakeManager and the LocalCaches present
 */
public interface L1ServerMapLocalCacheManager extends TCObjectSelfStore {

  /**
   * Create a local cache for use or return already created local cache for the mapId
   * 
   * @param serverMapLocalStore
   * @param callback
   */
  public ServerMapLocalCache getOrCreateLocalCache(ObjectID mapId, ClientObjectManager objectManager,
                                                   PlatformService platformService,
                                                   boolean localCacheEnabled,
                                                   L1ServerMapLocalCacheStore serverMapLocalStore,
                                                   PinnedEntryFaultCallback callback);

  /**
   * flush the entries from the LocalCache associated with the given map id.<br>
   * This is used in the process of invalidations
   */
  public ObjectIDSet removeEntriesForObjectId(ObjectID mapID, Set<ObjectID> set);


  /**
   * Shut down all local caches
   */
  @Override
  public void shutdown(boolean fromShutdownHook);

  public void evictElements(Map evictedElements, ServerMapLocalCache serverMapLocalCache);

  public void transactionComplete(
                                  L1ServerMapLocalStoreTransactionCompletionListener l1ServerMapLocalStoreTransactionCompletionListener);

}
