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
package com.tc.object;

import com.tc.object.servermap.localcache.L1ServerMapLocalCacheManager;
import com.tc.platform.PlatformService;

public class ServerMapTCClassImpl extends TCClassImpl implements TCClass {

  private final L1ServerMapLocalCacheManager globalLocalCacheManager;
  private final RemoteServerMapManager       remoteServerMapManager;
  private final PlatformService              platformService;

  ServerMapTCClassImpl(final PlatformService platformService,
                       final L1ServerMapLocalCacheManager globalLocalCacheManager,
                       final RemoteServerMapManager remoteServerMapManager, final TCClassFactory clazzFactory,
                       final ClientObjectManager objectManager, final Class peer, final boolean useNonDefaultConstructor) {
    super(clazzFactory, objectManager, peer, useNonDefaultConstructor);
    this.platformService = platformService;
    this.globalLocalCacheManager = globalLocalCacheManager;
    this.remoteServerMapManager = remoteServerMapManager;
  }

  @Override
  public TCObject createTCObject(final ObjectID id, final Object pojo, final boolean isNew) {
    if (pojo != null && !(pojo.getClass().getName().equals(TCClassFactory.SERVER_MAP_CLASSNAME))) {
      // bad formatter
      throw new AssertionError("This class should be used only for " + TCClassFactory.SERVER_MAP_CLASSNAME
                               + " but pojo : " + pojo.getClass().getName());
    }
    return new TCObjectServerMapImpl(this.platformService, getObjectManager(), this.remoteServerMapManager, id, pojo,
                                     this, isNew, this.globalLocalCacheManager);
  }

}
