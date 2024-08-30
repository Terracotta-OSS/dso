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
package com.tc.objectserver.impl;

import com.tc.objectserver.api.BackupManager;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * @author tim
 */
public class NullBackupManager implements BackupManager {
  public static final NullBackupManager INSTANCE = new NullBackupManager();

  @Override
  public BackupStatus getBackupStatus(final String name) {
    return BackupStatus.UNKNOWN;
  }

  @Override
  public String getBackupFailureReason(String name) throws IOException {
    return null;
  }

  @Override
  public String getRunningBackup() {
    return null;
  }

  @Override
  public void backup(final String name) {
    throw new UnsupportedOperationException("Backups not supported for non-restartable mode.");
  }

  @Override
  public Map<String, BackupStatus> getBackupStatuses() {
    return Collections.emptyMap();
  }
}
