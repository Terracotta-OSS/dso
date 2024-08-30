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
package com.terracotta.toolkit.search;

import org.terracotta.toolkit.search.SearchException;
import org.terracotta.toolkit.search.SearchQueryResultSet;
import org.terracotta.toolkit.search.ToolkitSearchQuery;

import com.tc.search.SearchRequestID;


/**
 * Objects of this type can be used to execute toolkit search queries
 */
public interface SearchExecutor {

  /**
   * Execute given search query
   * 
   * @param query query to run
   * @param queryId unique id for this query (within this client)
   * @return search query results
   */
  SearchQueryResultSet executeQuery(ToolkitSearchQuery query, SearchRequestID queryId) throws SearchException;

}
