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

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.objectserver.persistence.ClusterStatePersistor;
import com.tc.util.ProductInfo;
import com.tc.util.version.Version;
import com.tc.util.version.VersionCompatibility;

/**
 * @author tim
 */
public class ServerPersistenceVersionChecker {
  private static final TCLogger LOGGER = TCLogging.getLogger(ServerPersistenceVersionChecker.class);
  private final ClusterStatePersistor clusterStatePersistor;
  private final ProductInfo productInfo;
  private final VersionCompatibility versionCompatibility;

  public ServerPersistenceVersionChecker(final ClusterStatePersistor clusterStatePersistor) {
    this(clusterStatePersistor, ProductInfo.getInstance(), new VersionCompatibility());
  }

  ServerPersistenceVersionChecker(final ClusterStatePersistor clusterStatePersistor, final ProductInfo productInfo,
                                  final VersionCompatibility versionCompatibility) {
    this.clusterStatePersistor = clusterStatePersistor;
    this.productInfo = productInfo;
    this.versionCompatibility = versionCompatibility;
  }

  private boolean checkVersion(Version persisted, Version current) {
    if (persisted != null) { return versionCompatibility.isCompatibleServerPersistence(persisted, current); }
    return true;
  }

  public void checkAndSetVersion() {
    Version currentVersion = new Version(productInfo.version());
    Version persistedVersion = clusterStatePersistor.getVersion();
    if (checkVersion(clusterStatePersistor.getVersion(), currentVersion)) {
      
      // only move persisted version forward
      if (persistedVersion == null || currentVersion.compareTo(persistedVersion) > 0) {
        clusterStatePersistor.setVersion(currentVersion);
      }
    } else {
      LOGGER.error("Incompatible data format detected. Found data for version " + persistedVersion +" expecting data for version " + currentVersion + ".");
      LOGGER.error("Verify that the correct data is in the server data path, and try again.");
      throw new IllegalStateException("Incompatible data format version. Got " + persistedVersion + " expected " + currentVersion);
    }
  }
}
