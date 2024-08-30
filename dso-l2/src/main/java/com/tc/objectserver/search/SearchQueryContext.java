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

import com.tc.async.api.EventContext;
import com.tc.net.ClientID;
import com.tc.net.GroupID;
import com.tc.search.SearchRequestID;
import com.terracottatech.search.NVPair;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Context holding search query search information.
 * 
 * @author Nabib El-Rahman
 */
public class SearchQueryContext implements EventContext {

  private final ClientID        clientID;
  private final SearchRequestID requestID;
  private final GroupID         groupIDFrom;
  private final String          cacheName;
  private final LinkedList      queryStack;
  private final boolean         includeKeys;
  private final boolean         includeValues;
  private final Set<String>     attributeSet;
  private final List<NVPair>    sortAttributes;
  private final List<NVPair>    aggregators;
  private final int             maxResults;
  private final int             batchSize;
  private final boolean         prefetchFirstBatch;

  public SearchQueryContext(ClientID clientID, SearchRequestID requestID, GroupID groupIDFrom, String cacheName,
                            LinkedList queryStack, boolean includeKeys, boolean includeValues,
                            Set<String> attributeSet, List<NVPair> sortAttributes, List<NVPair> aggregators,
                            int maxResults, int batchSize, boolean prefetchFirstBatch) {
    this.clientID = clientID;
    this.requestID = requestID;
    this.groupIDFrom = groupIDFrom;
    this.cacheName = cacheName;
    this.queryStack = queryStack;
    this.includeKeys = includeKeys;
    this.includeValues = includeValues;
    this.attributeSet = attributeSet;
    this.sortAttributes = sortAttributes;
    this.aggregators = aggregators;
    this.maxResults = maxResults;
    this.batchSize = batchSize;
    this.prefetchFirstBatch = prefetchFirstBatch;
  }

  /**
   * Query stack.
   * 
   * @return LinkedList linkedList
   */
  public LinkedList getQueryStack() {
    return this.queryStack;
  }

  /**
   * Cachename/Index name.
   * 
   * @return String string
   */
  public String getCacheName() {
    return this.cacheName;
  }

  /**
   * Return clientID.
   * 
   * @return ClientID clientID
   */
  public ClientID getClientID() {
    return this.clientID;
  }

  /**
   * SearchRequestID requestID.
   * 
   * @return SearchRequestID requestID
   */
  public SearchRequestID getRequestID() {
    return this.requestID;
  }

  /**
   * GroupID that request originated from.
   */
  public GroupID getGroupIDFrom() {
    return this.groupIDFrom;
  }

  /**
   * Result set should include keys.
   * 
   * @return boolean true if should return keys.
   */
  public boolean includeKeys() {
    return includeKeys;
  }

  /**
   * Result set should include values
   * 
   * @return boolean true if should return values
   */
  public boolean includeValues() {
    return includeValues;
  }

  /**
   * Attribute keys, should return values with result set.
   * 
   * @return {@code Set<String>} attributes.
   */
  public Set<String> getAttributeSet() {
    return attributeSet;
  }

  /**
   * Sorted attributes, pair of attributes if ascending, true
   * 
   * @return sortAttributes.
   */
  public List<NVPair> getSortAttributes() {
    return sortAttributes;
  }

  /**
   * Attribute aggregators, returns a attribute-&gt;aggregator type pairs.
   * 
   * @return {@code List<NVPair>}
   */
  public List<NVPair> getAggregators() {
    return aggregators;
  }

  /**
   * Return maximum size of results.
   * 
   * @return integer
   */
  public int getMaxResults() {
    return this.maxResults;
  }

  /**
   * {@inheritDoc}
   */
  public Object getKey() {
    return clientID;
  }

  /**
   * {@inheritDoc}
   */
  public int getBatchSize() {
    return batchSize;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isPrefetchFirstBatch() {
    return prefetchFirstBatch;
  }

}
