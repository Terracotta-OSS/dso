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
package com.tc.management.beans.object;

import com.tc.management.TerracottaMBean;

import java.io.IOException;

public interface ServerDBBackupMBean extends TerracottaMBean {

  public static final String BACKUP_ENABLED    = "com.tc.management.beans.object.serverdbbackup.enabled";
  public static final String PERCENTAGE_COPIED = "com.tc.management.beans.object.serverdbbackup.percentagecopied";
  public static final String BACKUP_STARTED    = "com.tc.management.beans.object.serverdbbackup.backupstarted";
  public static final String BACKUP_COMPLETED  = "com.tc.management.beans.object.serverdbbackup.backupcompleted";
  public static final String BACKUP_FAILED     = "com.tc.management.beans.object.serverdbbackup.backupfailed";

  public String getDefaultPathForBackup();

  public boolean isBackupEnabled();

  public boolean isBackUpRunning();

  public void runBackUp() throws IOException;

  public void runBackUp(String path) throws IOException;

  public String getDbHome();
}
