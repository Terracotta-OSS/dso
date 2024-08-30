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
package com.tc.objectserver.search;

import com.tc.object.ObjectID;
import com.tc.objectserver.metadata.MetaDataProcessingContext;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;

class BaseSearchEventContext implements SearchEventContext {

  private static final int                INDEX_PER_CACHE     = TCPropertiesImpl
                                                                  .getProperties()
                                                                  .getInt(TCPropertiesConsts.SEARCH_LUCENE_INDEXES_PER_CACHE);
  private static final int                SEDA_SEARCH_THREADS = TCPropertiesImpl.getProperties()
                                                                  .getInt(TCPropertiesConsts.L2_SEDA_SEARCH_THREADS);

  private final MetaDataProcessingContext metaDataContext;
  private final String                    cacheName;
  private final ObjectID                  segmentOid;

  public BaseSearchEventContext(ObjectID segmentOid, String cacheName, MetaDataProcessingContext metaDataContext) {
    this.segmentOid = segmentOid;
    this.cacheName = cacheName;
    this.metaDataContext = metaDataContext;
  }

  @Override
  public final Object getKey() {
    // Pick the start thread index using cache name
    int threadStart = Math.abs(cacheName.hashCode()) % SEDA_SEARCH_THREADS;
    // Pick the next n threads for n indexes using segment id
    int indexSegment = (int) (Math.abs(segmentOid.toLong()) % INDEX_PER_CACHE);
    return (threadStart + indexSegment) % SEDA_SEARCH_THREADS;
  }

  public MetaDataProcessingContext getMetaDataProcessingContext() {
    return metaDataContext;
  }

  public String getCacheName() {
    return cacheName;
  }

  public ObjectID getSegmentOid() {
    return segmentOid;
  }
}
