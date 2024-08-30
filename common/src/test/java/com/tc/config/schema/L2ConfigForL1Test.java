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
package com.tc.config.schema;

import com.tc.test.EqualityChecker;
import com.tc.test.TCTestCase;

/**
 * Unit test for {@link L2ConfigForL1}.
 */
public class L2ConfigForL1Test extends TCTestCase {

  public void testL2Data() throws Exception {
    try {
      new L2ConfigForL1.L2Data(null, 20);
      fail("Didn't get NPE on no host");
    } catch (NullPointerException npe) {
      // ok
    }

    try {
      new L2ConfigForL1.L2Data("", 20);
      fail("Didn't get IAE on empty host");
    } catch (IllegalArgumentException iae) {
      // ok
    }

    try {
      new L2ConfigForL1.L2Data("   ", 20);
      fail("Didn't get IAE on blank host");
    } catch (IllegalArgumentException iae) {
      // ok
    }

    L2ConfigForL1.L2Data config = new L2ConfigForL1.L2Data("foobar", 20);
    assertEquals("foobar", config.host());
    assertEquals(20, config.tsaPort());

    EqualityChecker.checkArraysForEquality(
        new Object[] {
                      new L2ConfigForL1.L2Data("foobar", 20),
                      new L2ConfigForL1.L2Data("foobaz", 20),
                      new L2ConfigForL1.L2Data("foobar", 2),
                      new L2ConfigForL1.L2Data("foobar", 30) },
        new Object[] {
                      new L2ConfigForL1.L2Data("foobar", 20),
                      new L2ConfigForL1.L2Data("foobaz", 20),
                      new L2ConfigForL1.L2Data("foobar", 2),
                      new L2ConfigForL1.L2Data("foobar", 30) });
  }

}
