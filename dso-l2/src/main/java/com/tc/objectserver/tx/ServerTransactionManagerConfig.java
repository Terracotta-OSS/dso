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

public final class ServerTransactionManagerConfig {

  private final boolean loggingEnabled;
  private final boolean verboseLogging;
  private final boolean printStats;
  private final boolean printCommits;
  private final boolean printBroadcastStats;

  public ServerTransactionManagerConfig(TCProperties tcproperties) {
    this.loggingEnabled = tcproperties.getBoolean(TCPropertiesConsts.L2_TRANSACTIONMANAGER_LOGGING_ENABLED);
    this.verboseLogging = tcproperties.getBoolean(TCPropertiesConsts.L2_TRANSACTIONMANAGER_LOGGING_VERBOSE);
    this.printStats = tcproperties.getBoolean(TCPropertiesConsts.L2_TRANSACTIONMANAGER_LOGGING_PRINTSTATS);
    this.printCommits = tcproperties.getBoolean(TCPropertiesConsts.L2_TRANSACTIONMANAGER_LOGGING_PRINTCOMMITS);
    this.printBroadcastStats = tcproperties.getBoolean(TCPropertiesConsts.L2_TRANSACTIONMANAGER_LOGGING_PRINT_BROADCAST_STATS);
  }

  // Used in tests
  public ServerTransactionManagerConfig() {
    this.loggingEnabled = false;
    this.verboseLogging = false;
    this.printStats = false;
    this.printCommits = false;
    this.printBroadcastStats = false;
  }

  public boolean isLoggingEnabled() {
    return loggingEnabled;
  }

  public boolean isPrintStatsEnabled() {
    return printStats;
  }

  public boolean isVerboseLogging() {
    return verboseLogging;
  }

  public boolean isPrintCommitsEnabled() {
    return printCommits;
  }

  public boolean isPrintBroadcastStatsEnabled() {
    return printBroadcastStats;
  }
}
