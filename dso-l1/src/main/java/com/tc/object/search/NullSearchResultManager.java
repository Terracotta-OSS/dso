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
package com.tc.object.search;

import com.tc.net.GroupID;
import com.tc.net.NodeID;
import com.tc.object.msg.ClientHandshakeMessage;
import com.tc.object.session.SessionID;
import com.tc.search.SearchQueryResults;
import com.tc.search.SearchRequestID;
import com.terracottatech.search.IndexQueryResult;
import com.terracottatech.search.NVPair;

import java.util.List;
import java.util.Set;

public class NullSearchResultManager implements SearchResultManager {

  @Override
  public void addResponse(SessionID sessionID, SearchRequestID requestID, GroupID group,
                          List<IndexQueryResult> queryResults,
                          NodeID nodeID) {
    //
  }

  @Override
  public void addErrorResponse(SessionID sessionID, SearchRequestID requestID, GroupID group, String errorMessage,
                               NodeID nodeID) {
    //
  }

  @Override
  public SearchQueryResults<IndexQueryResult> loadResults(String cachename, SearchRequestID reqId, List queryStack,
                                                          boolean includeKeys, boolean includeValues,
                                                          Set<String> attributeSet, List<NVPair> sortAttributeMap,
                                                          List<NVPair> aggregators, int maxResults, int start,
                                                          int pageSize, GroupID from) {
    return null;
  }

  @Override
  public void pause(NodeID remoteNode, int disconnected) {
    //
  }

  @Override
  public void unpause(NodeID remoteNode, int disconnected) {
    //
  }

  @Override
  public void initializeHandshake(NodeID thisNode, NodeID remoteNode, ClientHandshakeMessage handshakeMessage) {
    //
  }

  @Override
  public void shutdown(boolean fromShutdownHook) {
    //
  }

  @Override
  public void cleanup() {
    //
  }

  @Override
  public void releaseResults(SearchRequestID request) {
    //
  }

  @Override
  public int getOpenResultSetCount() {
    return 0;
  }

  @Override
  public void resultSetReceived(SearchRequestID id, long size) {
    //
  }
}
