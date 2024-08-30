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

import com.tc.abortable.AbortedOperationException;
import com.tc.net.GroupID;
import com.tc.net.NodeID;
import com.tc.object.handshakemanager.ClientHandshakeCallback;
import com.tc.object.session.SessionID;
import com.tc.search.SearchQueryResults;
import com.tc.search.SearchRequestID;
import com.terracottatech.search.IndexQueryResult;
import com.terracottatech.search.NVPair;
import com.terracottatech.search.aggregator.Aggregator;

import java.util.List;
import java.util.Set;

/**
 *
 *
 */
public interface RemoteSearchRequestManager extends ClientHandshakeCallback {

  public SearchQueryResults query(String cachename, List queryStack, boolean includeKeys, boolean includeValues,
                                  Set<String> attributeSet, List<NVPair> sortAttributeMap, List<NVPair> aggregators,
                                  int maxResults, int firstValueBatchSize, SearchRequestID reqId, int resultSetLimit)
      throws AbortedOperationException;

  public SearchQueryResults query(String cachename, List queryStack, Set<String> attributeSet,
                                  Set<String> groupByAttributes, List<NVPair> sortAttributeMap,
                                  List<NVPair> aggregators, int maxResults, int firstValueBatchSize,
                                  SearchRequestID reqId)
      throws AbortedOperationException;

  public void addResponseForQuery(final SessionID sessionID, final SearchRequestID requestID, GroupID groupIDFrom,
                                  final List<IndexQueryResult> queryResults, final long totalResultCount,
                                  final List<Aggregator> aggregators, final NodeID nodeID, boolean anyCriteriaMatched);

  public void addErrorResponseForQuery(final SessionID sessionID, final SearchRequestID requestID, GroupID groupIDFrom,
                                       final String errorMessage, final NodeID nodeID);

}
