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
package com.tc.objectserver.dgc.impl;

import com.tc.object.ObjectID;
import com.tc.objectserver.api.ObjectManager;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;
import com.tc.util.concurrent.ThreadUtil;

import java.util.Set;

public class DGCRequestThrottler {
  private static final long     THROTTLE_GC_MILLIS    = TCPropertiesImpl
                                                          .getProperties()
                                                          .getLong(
                                                                   TCPropertiesConsts.L2_OBJECTMANAGER_DGC_THROTTLE_TIME);
  private static final long     REQUESTS_PER_THROTTLE = TCPropertiesImpl
                                                          .getProperties()
                                                          .getLong(
                                                                   TCPropertiesConsts.L2_OBJECTMANAGER_DGC_REQUEST_PER_THROTTLE);
  protected final ObjectManager objectManager;
  private long                  request_count         = 0;

  public DGCRequestThrottler(ObjectManager objectManager) {
    this.objectManager = objectManager;
  }

  public Set<ObjectID> getObjectReferencesFrom(final ObjectID id, final boolean cacheOnly) {
    throttleIfNecessary();
    return objectManager.getObjectReferencesFrom(id, cacheOnly);
  }

  private void throttleIfNecessary() {
    if (THROTTLE_GC_MILLIS > 0 && ++this.request_count % REQUESTS_PER_THROTTLE == 0) {
      ThreadUtil.reallySleep(THROTTLE_GC_MILLIS);
    }
  }
}
