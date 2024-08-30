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
package com.terracotta.toolkit.config.cache;

import org.junit.Test;
import org.terracotta.toolkit.builder.ToolkitStoreConfigBuilder;
import org.terracotta.toolkit.config.Configuration;

import junit.framework.TestCase;

public class InternalCacheConfigurationTypeTest extends TestCase {

  @Test
  public void testMaxBytesLocalOffHeap() {
    Configuration configuration = new ToolkitStoreConfigBuilder().maxBytesLocalOffheap(Long.MAX_VALUE).build();
    InternalCacheConfigurationType.MAX_BYTES_LOCAL_OFFHEAP.validateLegalValue(configuration
        .getObjectOrNull(InternalCacheConfigurationType.MAX_BYTES_LOCAL_OFFHEAP.getConfigString()));
  }

  @Test
  public void testMaxBytesLocalOnHeap() {
    Configuration configuration = new ToolkitStoreConfigBuilder().maxBytesLocalHeap(Long.MAX_VALUE).build();
    InternalCacheConfigurationType.MAX_BYTES_LOCAL_HEAP.validateLegalValue(configuration
        .getObjectOrNull(InternalCacheConfigurationType.MAX_BYTES_LOCAL_HEAP.getConfigString()));

  }

}
