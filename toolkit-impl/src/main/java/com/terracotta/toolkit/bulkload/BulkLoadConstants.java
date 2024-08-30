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
package com.terracotta.toolkit.bulkload;

import com.tc.properties.TCProperties;
import com.tc.properties.TCPropertiesConsts;

public final class BulkLoadConstants {

  private final TCProperties  tcProperties;

  public BulkLoadConstants(TCProperties tcProperties) {
    this.tcProperties = tcProperties;
  }

  public boolean isLoggingEnabled() {
    return tcProperties.getBoolean(TCPropertiesConsts.TOOLKIT_BULKLOAD_LOGGING_ENABLED);
     
  }

  public int getBatchedPutsBatchBytes() {
    return tcProperties.getInt(TCPropertiesConsts.TOOLKIT_LOCAL_BUFFER_PUTS_BATCH_BYTE_SIZE);
  }

  public long getBatchedPutsBatchTimeMillis() {
    return tcProperties.getLong(TCPropertiesConsts.TOOLKIT_LOCAL_BUFFER_PUTS_BATCH_TIME_MILLIS);
  }

  public int getBatchedPutsThrottlePutsAtByteSize() {
    return tcProperties.getInt(TCPropertiesConsts.TOOLKIT_LOCAL_BUFFER_PUTS_THROTTLE_BYTE_SIZE);
  }

}
