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
package com.terracotta.toolkit.config;

import java.util.Arrays;

public final class ConfigUtil {
  private static final int[] EMPTY_INT_ARRAY = new int[0];

  private ConfigUtil() {
    // private
  }

  public static int[] distributeInStripes(int configAttrInt, int numStripes) {
    if (numStripes == 0) { return EMPTY_INT_ARRAY; }
    int[] rv = new int[numStripes];
    int least = configAttrInt / numStripes;
    Arrays.fill(rv, least);
    int remainder = configAttrInt % numStripes;
    for (int i = 0; i < remainder; i++) {
      rv[i]++;
    }
    return rv;
  }

}
