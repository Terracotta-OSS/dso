/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
package com.tc.management;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;

/**
 * Holder of the {@link RemoteManagement} instance.
 */
public class TerracottaRemoteManagement {

  private static final TCLogger LOGGER = TCLogging.getLogger(TerracottaRemoteManagement.class);

  private static volatile RemoteManagement remoteManagement;

  public static void setRemoteManagementInstance(RemoteManagement instance) {
    if (instance != null && remoteManagement != null) {
      throw new IllegalStateException("Instance already loaded");
    }
    remoteManagement = instance;
  }

  public static RemoteManagement getRemoteManagementInstance() {
    if (remoteManagement == null) {
      return new RemoteManagement() {
        @Override
        public void registerEventListener(ManagementEventListener listener) {
        }

        @Override
        public void unregisterEventListener(ManagementEventListener listener) {
        }

        @Override
        public void sendEvent(TCManagementEvent event) {
          LOGGER.warn("Trying to send a management event while the RemoteManagement instance was not set");
        }
      };
    }
    return remoteManagement;
  }

}
