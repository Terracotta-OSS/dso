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
package com.tc.net.core;

import junit.framework.TestCase;

public class ConnectionAddressIteratorTest extends TestCase {

  public final void testEmpty() {
    ConnectionAddressIterator i = new ConnectionAddressIterator(new ConnectionInfo[0]);
    assertFalse(i.hasNext());
    assertNull(i.next());
  }
  
  public final void testOne() {
    final ConnectionInfo[] cis = new ConnectionInfo[] { new ConnectionInfo("1", 1) };
    ConnectionAddressIterator i = new ConnectionAddressIterator(cis);
    assertTrue(i.hasNext());
    assertTrue(i.hasNext()); // multi calls to hasNext should not change state
    assertSame(cis[0], i.next());
    assertFalse(i.hasNext());
    assertFalse(i.hasNext());
    assertNull(i.next());
  }
  public final void testMany() {
    final ConnectionInfo[] cis = new ConnectionInfo[] { new ConnectionInfo("1", 1), new ConnectionInfo("2", 2), new ConnectionInfo("3", 3) };
    ConnectionAddressIterator iter = new ConnectionAddressIterator(cis);
    for (int i = 0; i < cis.length; i++) {
      assertTrue(iter.hasNext());
      assertSame(cis[i], iter.next());
    }
    assertFalse(iter.hasNext());
    assertNull(iter.next());
  }
}
