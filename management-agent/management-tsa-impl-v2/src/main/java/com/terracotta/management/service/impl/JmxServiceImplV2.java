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

import com.terracotta.management.resource.MBeanEntityV2;
import com.terracotta.management.service.JmxServiceV2;

import java.util.Set;

/**
 * @author Ludovic Orban
 */
public class JmxServiceImplV2 implements JmxServiceV2 {

  private final ServerManagementServiceV2 serverManagementService;

  public JmxServiceImplV2(ServerManagementServiceV2 serverManagementService) {
    this.serverManagementService = serverManagementService;
  }

  @Override
  public ResponseEntityV2<MBeanEntityV2> queryMBeans(Set<String> serverNames, String query) throws ServiceExecutionException {
    return serverManagementService.queryMBeans(serverNames, query);
  }
}
