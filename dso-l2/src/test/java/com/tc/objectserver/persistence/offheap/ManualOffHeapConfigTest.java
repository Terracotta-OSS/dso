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
package com.tc.objectserver.persistence.offheap;

import org.junit.Assert;

import com.tc.properties.TCProperties;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;
import com.tc.test.TCTestCase;

/**
 *
 * @author mscott
 */
public class ManualOffHeapConfigTest extends TCTestCase {

  @Override
  public void setUp() {
    TCProperties props = TCPropertiesImpl.getProperties();
    props.setProperty(TCPropertiesConsts.L2_OFFHEAP_MIN_PAGE_SIZE, "4k");
    props.setProperty(TCPropertiesConsts.L2_OFFHEAP_MAX_PAGE_SIZE, "8M");
  }

  public void testHighMinPageSize() {
    TCProperties props = TCPropertiesImpl.getProperties();
    props.setProperty(TCPropertiesConsts.L2_OFFHEAP_MIN_PAGE_SIZE, "8M");
    OffHeapConfig config = new OffHeapConfig(true, "10G", true) {};
    Assert.assertTrue(config.getMinMapPageSize() <=config.getMaxMapPageSize());
  }

  public void testBrokenPageSizing() {
    TCProperties props = TCPropertiesImpl.getProperties();
    props.setProperty(TCPropertiesConsts.L2_OFFHEAP_MIN_PAGE_SIZE, "8M");
    props.setProperty(TCPropertiesConsts.L2_OFFHEAP_MAX_PAGE_SIZE, "1M");
    OffHeapConfig config = new OffHeapConfig(true, "10G", true) {};
    Assert.assertTrue(config.getMinMapPageSize() <=config.getMaxMapPageSize());
  }
}
