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

import com.tc.test.TCTestCase;
import com.tc.util.Assert;

public class OffheapObjectPropertiesTest extends TCTestCase {

  public void testBasic() throws Exception {
    runCase(OffheapObjectProperties.ONE_MB * 512, "512 MB");
    runCase(OffheapObjectProperties.ONE_MB * 1023, "1023 MB");
    runCase(OffheapObjectProperties.ONE_GB * 2, "2 GB");
    runCase(OffheapObjectProperties.ONE_GB * 512, "4 GB");
    runCase(OffheapObjectProperties.ONE_GB * 8, "8 GB");
    runCase(OffheapObjectProperties.ONE_GB * 50, "50 GB");
  }

  public void runCase(long memoryGiven, String memory) throws Exception {
    OffheapObjectProperties props = new OffheapObjectProperties(
                                                                memoryGiven
                                                                    * (int) (100.0 / OffheapObjectProperties.OBJECT_PERCENTAGE));
    long mem = (props.getObjectConcurrency() * props.getObjectInitialDataSize()) + props.getObjectTableSize() * 16;

    System.out.println(memory + " === " + props.toString());
    System.out.println(memory + " === Mem required " + mem);

    Assert.assertTrue(props.getObjectConcurrency() <= OffheapObjectProperties.OBJECT_CONCURRENCY_ABOVE_4GB);
    Assert.assertTrue(props.getObjectConcurrency() > 0);

    Assert.assertTrue(props.getObjectInitialDataSize() <= OffheapObjectProperties.OBJECT_MAX_INIT_DATA_SIZE);
    Assert.assertTrue(props.getObjectInitialDataSize() >= OffheapObjectProperties.OBJECT_MIN_INIT_DATA_SIZE);

    Assert.assertTrue(props.getObjectTableSize() <= OffheapObjectProperties.OBJECT_TABLE_SIZE_ABOVE_1GB);
    Assert.assertTrue(props.getObjectTableSize() >= OffheapObjectProperties.OBJECT_TABLE_SIZE_BELOW_1GB);

    Assert.assertTrue(mem < memoryGiven);
  }
}