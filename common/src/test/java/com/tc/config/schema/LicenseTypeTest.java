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
 * Unit test for {@link LicenseType}.
 */
public class LicenseTypeTest extends TCTestCase {

  public void testAll() throws Exception {
    Object[] arr1 = new Object[] { LicenseType.TRIAL, LicenseType.PRODUCTION, LicenseType.NONE };
    Object[] arr2 = new Object[] { LicenseType.TRIAL, LicenseType.PRODUCTION, LicenseType.NONE };

    EqualityChecker.checkArraysForEquality(arr1, arr2);

    assertFalse(LicenseType.TRIAL.equals(null));
    assertFalse(LicenseType.TRIAL.equals("trial"));
    assertFalse(LicenseType.TRIAL.equals("foo"));

    LicenseType.TRIAL.toString();
    LicenseType.PRODUCTION.toString();
    LicenseType.NONE.toString();
  }

}
