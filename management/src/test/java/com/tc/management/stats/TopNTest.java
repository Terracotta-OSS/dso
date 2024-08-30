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
package com.tc.management.stats;

import java.util.Collections;
import java.util.Iterator;

import junit.framework.TestCase;

public class TopNTest extends TestCase {

  public void testTopN() throws Exception {
    TopN topN = new TopN(3);
    for (int pos = -10; pos < 10; ++pos) {
      topN.evaluate(new Integer(pos));
    }
    Iterator pos = topN.iterator();
    assertEquals(new Integer(9), pos.next());
    assertEquals(new Integer(8), pos.next());
    assertEquals(new Integer(7), pos.next());
    assertFalse(pos.hasNext());
  }

  public void testTopNWithComparator() throws Exception {
    TopN topN = new TopN(Collections.reverseOrder(), 3);
    for (int pos = -10; pos < 10; ++pos) {
      topN.evaluate(new Integer(pos));
    }
    Iterator pos = topN.iterator();
    assertEquals(new Integer(-10), pos.next());
    assertEquals(new Integer(-9), pos.next());
    assertEquals(new Integer(-8), pos.next());
    assertFalse(pos.hasNext());
  }

}
