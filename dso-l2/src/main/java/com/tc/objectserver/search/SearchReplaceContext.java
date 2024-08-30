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
import com.terracottatech.search.NVPair;
import com.terracottatech.search.ValueID;

import java.util.List;

public class SearchReplaceContext extends BaseSearchEventContext {

  private final List<NVPair> attributes;
  private final String       cacheKey;
  private final ValueID      cacheValue;
  private final ValueID      prevValue;

  public SearchReplaceContext(ObjectID segmentOid, String name, String cacheKey, ValueID cacheValue, ValueID prevValue,
                              List<NVPair> attributes, MetaDataProcessingContext context) {
    super(segmentOid, name, context);
    this.cacheKey = cacheKey;
    this.cacheValue = cacheValue;
    this.prevValue = prevValue;
    this.attributes = attributes;
  }

  public String getCacheKey() {
    return cacheKey;
  }

  public ValueID getCacheValue() {
    return cacheValue;
  }

  public List<NVPair> getAttributes() {
    return attributes;
  }

  public ValueID getPreviousValue() {
    return prevValue;
  }

}
