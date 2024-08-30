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
package com.tc.object.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class TransparencyClassSpecUtil {

  private static final Map    anomalies     = Collections.synchronizedMap(new HashMap());

  private static final String IGNORE_CHECKS = "IGNORE_CHECKS";

  static {
    anomalies.put("org.apache.commons.collections.FastHashMap", IGNORE_CHECKS);
  }

  private TransparencyClassSpecUtil() {
    super();
  }

  public static boolean ignoreChecks(String className) {
    return IGNORE_CHECKS.equals(anomalies.get(className));
  }

}
