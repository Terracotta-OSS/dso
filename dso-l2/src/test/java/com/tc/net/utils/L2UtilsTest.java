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
package com.tc.net.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Eugene Shelestovich
 */
public class L2UtilsTest {

  @Test
  public void testShouldCorrectlyCalculateOptimalThreadsCount() {
    assertEquals(6, L2Utils.calculateOptimalThreadsCount(4, 30, 70, 1.0));
    assertEquals(20, L2Utils.calculateOptimalThreadsCount(2, 90, 10, 1.0));
    assertEquals(1, L2Utils.calculateOptimalThreadsCount(1, 30, 70, 1.0));
    assertEquals(2, L2Utils.calculateOptimalThreadsCount(1, 0, 100, 1.0));
    assertEquals(22, L2Utils.calculateOptimalThreadsCount(16, 30, 70, 1.0));
    assertEquals(112, L2Utils.calculateOptimalThreadsCount(80, 30, 70, 1.0));
    assertEquals(84, L2Utils.calculateOptimalThreadsCount(80, 30, 70, 0.75));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShouldThrowIAEOnInvalidUtilization() {
    L2Utils.calculateOptimalThreadsCount(4, 30, 70, 0.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShouldThrowIAEOnInvalidCpusCount() {
    L2Utils.calculateOptimalThreadsCount(0, 30, 70, 1.0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testShouldThrowIAEOnInvalidCompute() {
    L2Utils.calculateOptimalThreadsCount(4, 30, 0, 1.0);
  }
}
