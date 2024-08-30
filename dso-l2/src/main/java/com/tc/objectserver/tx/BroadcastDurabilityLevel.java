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
package com.tc.objectserver.tx;

import com.tc.properties.TCProperties;
import com.tc.properties.TCPropertiesConsts;

/**
 * @author tim
 */
public enum BroadcastDurabilityLevel {
  NONE(false, false), RELAYED(true, false), DISK(true, true);

  private final boolean waitForRelay;
  private final boolean waitForCommit;

  BroadcastDurabilityLevel(final boolean waitForRelay, final boolean waitForCommit) {
    this.waitForRelay = waitForRelay;
    this.waitForCommit = waitForCommit;
  }

  public boolean isWaitForRelay() {
    return waitForRelay;
  }

  public boolean isWaitForCommit() {
    return waitForCommit;
  }

  public static BroadcastDurabilityLevel getFromProperties(TCProperties tcProperties) {
    String v = tcProperties.getProperty(TCPropertiesConsts.L2_TRANSACTIONMANAGER_BROADCAST_DURABILITY_LEVEL, true);
    return v == null ? RELAYED : valueOf(v.toUpperCase());
  }
}
