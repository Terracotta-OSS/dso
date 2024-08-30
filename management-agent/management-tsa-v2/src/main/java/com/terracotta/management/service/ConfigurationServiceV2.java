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

import com.terracotta.management.resource.ConfigEntityV2;

import java.util.Set;

/**
 * An interface for service implementations providing TSA config facilities.
 *
 * @author Ludovic Orban
 */
public interface ConfigurationServiceV2 {

  /**
   * Get a collection {@link com.terracotta.management.resource.ConfigEntityV2} objects each representing a server
   * config. Only requested servers are included, or all of them if serverNames is null.
   *
   * @param serverNames A set of server names, null meaning all of them.
   * @return a collection {@link com.terracotta.management.resource.ConfigEntityV2} objects.
   * @throws ServiceExecutionException
   */
  ResponseEntityV2<ConfigEntityV2> getServerConfigs(Set<String> serverNames) throws ServiceExecutionException;

  /**
   * Get a collection {@link com.terracotta.management.resource.ConfigEntityV2} objects each representing a client
   * config. Only requested clients are included, or all of them if clientIds is null.
   *
   * @param clientIds A set of client IDs, null meaning all of them.
   * @return a collection {@link com.terracotta.management.resource.ConfigEntityV2} objects.
   * @throws ServiceExecutionException
   */
  ResponseEntityV2<ConfigEntityV2> getClientConfigs(Set<String> clientIds, Set<String> clientProductIds) throws ServiceExecutionException;

}
