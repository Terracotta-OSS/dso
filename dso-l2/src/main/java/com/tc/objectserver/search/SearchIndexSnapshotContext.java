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
import com.terracottatech.search.QueryID;

public class SearchIndexSnapshotContext extends BaseSearchEventContext {

  private final QueryID snapshotId;
  private final boolean isClose;

  public QueryID getSnapshotId() {
    return snapshotId;
  }

  public boolean isClose() {
    return isClose;
  }

  public SearchIndexSnapshotContext(ObjectID segmentOid, String cacheName, QueryID query, boolean isClose,
                                    MetaDataProcessingContext metaDataContext) {
    super(segmentOid, cacheName, metaDataContext);
    snapshotId = query;
    this.isClose = isClose;
  }

}
