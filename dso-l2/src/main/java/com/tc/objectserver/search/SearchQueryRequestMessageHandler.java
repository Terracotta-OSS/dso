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

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventContext;
import com.tc.object.msg.SearchQueryRequestMessage;
import com.tc.object.msg.SearchResultsRequestMessage;
import com.tc.objectserver.core.api.ServerConfigurationContext;

/**
 * All search queries are processed through this handler.
 * 
 * @author Nabib El-Rahman
 */
public class SearchQueryRequestMessageHandler extends AbstractEventHandler {

  private SearchRequestManager searchRequestManager;

  @Override
  public void handleEvent(EventContext context) {
    if (context instanceof SearchQueryRequestMessage) {
      SearchQueryRequestMessage msg = (SearchQueryRequestMessage) context;
      this.searchRequestManager.queryRequest(msg);
    } else if (context instanceof SearchResultsRequestMessage) {
      SearchResultsRequestMessage msg = (SearchResultsRequestMessage) context;
      this.searchRequestManager.resultsRequest(msg);
    } else {
      throw new AssertionError("Unknown context " + context);
    }
  }

  @Override
  protected void initialize(ConfigurationContext context) {
    super.initialize(context);
    ServerConfigurationContext serverContext = (ServerConfigurationContext) context;
    this.searchRequestManager = serverContext.getSearchRequestManager();
  }

}
