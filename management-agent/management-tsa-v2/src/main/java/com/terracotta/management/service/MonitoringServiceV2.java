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
package com.terracotta.management.service;

import org.terracotta.management.ServiceExecutionException;
import org.terracotta.management.resource.ResponseEntityV2;

import com.terracotta.management.resource.StatisticsEntityV2;

import java.net.URI;
import java.util.Collection;
import java.util.Set;

/**
 * An interface for service implementations providing TSA statistics monitoring facilities.
 *
 * @author Ludovic Orban
 */
public interface MonitoringServiceV2 {

  /**
   * Get the statistics of the specified client.
   *
   * @param clientIds A set of client IDs, null meaning all of them.
   * @param attributes the attributes to include in the response, all know ones will be returned if null.
   * @return a collection of {@link StatisticsEntityV2} objects.
   * @throws ServiceExecutionException
   */
  ResponseEntityV2<StatisticsEntityV2> getClientStatistics(Set<String> clientIds, Set<String> attributes, Set<String> clientProductIds) throws ServiceExecutionException;

  /**
   * Get the statistics of the specified server.
   *
   * @param serverNames A set of server names, null meaning all of them.
   * @param attributes the attributes to include in the response, all know ones will be returned if null.
   * @return a collection of {@link StatisticsEntityV2} objects.
   * @throws ServiceExecutionException
   */
  ResponseEntityV2<StatisticsEntityV2> getServerStatistics(Set<String> serverNames, Set<String> attributes) throws ServiceExecutionException;

  ResponseEntityV2<StatisticsEntityV2> getServerStatistics(Set<String> serverNames, Set<String> attributes,
                                                           Set<String> agentIds, URI uri) throws ServiceExecutionException;
  /**
   * Get the DGC statistics.
   *
   * @param serverNames A set of server names, null meaning all of them.
   * @return a {@link Collection} object of {@link StatisticsEntityV2} objects representing the DGC statistics,
   * one {@link StatisticsEntityV2} per DGC iteration.
   * @throws ServiceExecutionException
   */
  ResponseEntityV2<StatisticsEntityV2> getDgcStatistics(Set<String> serverNames) throws ServiceExecutionException;

}
