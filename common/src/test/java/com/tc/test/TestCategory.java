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
package com.tc.test;

import java.util.HashMap;
import java.util.Map;

/**
 * Enumeration of possible test categories.
 */
public enum TestCategory {
  /**
   * A production test that has been vetted by QA.
   */
  PRODUCTION,

  /**
   * A test that has been broken and is quarantined.
   */
  QUARANTINED,

  /**
   * A test that is new and has not yet been vetted by QA.
   */
  TRIAGED,

  /**
   * A test that has not been categorized. In the current monkey staging process, uncategorized tests are treated as
   * triaged.
   */
  UNCATEGORIZED;

  private static final Map<String, TestCategory> stringToCategory = new HashMap();
  static {
    for (TestCategory category : values()) {
      stringToCategory.put(category.toString().toUpperCase(), category);
    }
  }

}
