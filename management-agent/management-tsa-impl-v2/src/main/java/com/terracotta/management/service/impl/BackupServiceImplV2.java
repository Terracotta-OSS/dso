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

import com.terracotta.management.resource.BackupEntityV2;
import com.terracotta.management.service.BackupServiceV2;

import java.util.Set;

/**
 * @author Ludovic Orban
 */
public class BackupServiceImplV2 implements BackupServiceV2 {

  private final ServerManagementServiceV2 serverManagementService;

  public BackupServiceImplV2(ServerManagementServiceV2 serverManagementService) {
    this.serverManagementService = serverManagementService;
  }

  @Override
  public ResponseEntityV2<BackupEntityV2> getBackupStatus(Set<String> serverNames) throws ServiceExecutionException {
    return serverManagementService.getBackupsStatus(serverNames);
  }

  @Override
  public ResponseEntityV2<BackupEntityV2> backup(Set<String> serverNames, String backupName) throws ServiceExecutionException {
    return serverManagementService.backup(serverNames, backupName);
  }
}
