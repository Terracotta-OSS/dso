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
package com.terracotta.management.service.impl;

import org.terracotta.management.ServiceExecutionException;
import org.terracotta.management.resource.ResponseEntityV2;

import com.terracotta.management.resource.LogEntityV2;
import com.terracotta.management.resource.services.utils.TimeStringParser;
import com.terracotta.management.service.LogsServiceV2;

import java.util.Set;

/**
 * @author Ludovic Orban
 */
public class LogsServiceImplV2 implements LogsServiceV2 {

  private final ServerManagementServiceV2 serverManagementService;

  public LogsServiceImplV2(ServerManagementServiceV2 serverManagementService) {
    this.serverManagementService = serverManagementService;
  }

  @Override
  public ResponseEntityV2<LogEntityV2> getLogs(Set<String> serverNames) throws ServiceExecutionException {
    return serverManagementService.getLogs(serverNames, null);
  }

  @Override
  public ResponseEntityV2<LogEntityV2> getLogs(Set<String> serverNames, long sinceWhen) throws ServiceExecutionException {
    return serverManagementService.getLogs(serverNames, sinceWhen);
  }

  @Override
  public ResponseEntityV2<LogEntityV2> getLogs(Set<String> serverNames, String sinceWhen) throws ServiceExecutionException {
    if (sinceWhen == null) {
      return getLogs(serverNames);
    } else {
      try {
        return getLogs(serverNames, TimeStringParser.parseTime(sinceWhen));
      } catch (NumberFormatException nfe) {
        throw new ServiceExecutionException("Illegal time string: [" + sinceWhen + "]", nfe);
      }
    }
  }

}
