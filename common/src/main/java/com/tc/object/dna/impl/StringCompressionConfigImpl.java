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
package com.tc.object.dna.impl;

import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;

public class StringCompressionConfigImpl implements StringCompressionConfig {

  @Override
  public boolean enabled() {
    return TCPropertiesImpl.getProperties()
        .getBoolean(TCPropertiesConsts.L1_TRANSACTIONMANAGER_STRINGS_COMPRESS_ENABLED);
  }

  @Override
  public boolean loggingEnabled() {
    return TCPropertiesImpl.getProperties()
        .getBoolean(TCPropertiesConsts.L1_TRANSACTIONMANAGER_STRINGS_COMPRESS_LOGGING_ENABLED);
  }

  @Override
  public int minSize() {
    return TCPropertiesImpl.getProperties().getInt(TCPropertiesConsts.L1_TRANSACTIONMANAGER_STRINGS_COMPRESS_MINSIZE);
  }

}
