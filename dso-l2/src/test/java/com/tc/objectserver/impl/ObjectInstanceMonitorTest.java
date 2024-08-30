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
package com.tc.objectserver.impl;

import com.tc.objectserver.api.ObjectInstanceMonitor;

import java.util.Collections;
import java.util.Map;

import junit.framework.TestCase;

public class ObjectInstanceMonitorTest extends TestCase {

  public void test() {

    ObjectInstanceMonitor monitor = new ObjectInstanceMonitorImpl();
    assertEquals(Collections.EMPTY_MAP, monitor.getInstanceCounts());

    monitor.instanceCreated("timmy");
    monitor.instanceCreated("timmy");
    monitor.instanceCreated("timmy");

    Map counts;
    counts = monitor.getInstanceCounts();
    assertEquals(1, counts.size());
    assertEquals(new Integer(3), counts.get("timmy"));

    monitor.instanceCreated("timmy2");
    counts = monitor.getInstanceCounts();
    assertEquals(2, counts.size());
    assertEquals(new Integer(3), counts.get("timmy"));
    assertEquals(new Integer(1), counts.get("timmy2"));

    monitor.instanceDestroyed("timmy2");
    counts = monitor.getInstanceCounts();
    assertEquals(1, counts.size());
    assertEquals(new Integer(3), counts.get("timmy"));

    monitor.instanceDestroyed("timmy");
    counts = monitor.getInstanceCounts();
    assertEquals(1, counts.size());
    assertEquals(Integer.valueOf(2), counts.get("timmy"));

    try {
      monitor.instanceDestroyed("timmy2");
      fail();
    } catch (IllegalStateException ise) {
      // expected
    }

    try {
      monitor.instanceDestroyed("monitor has never seen this string before");
      fail();
    } catch (IllegalStateException ise) {
      // expected
    }

    try {
      monitor.instanceCreated(null);
      fail();
    } catch (IllegalArgumentException ise) {
      // expected
    }

    try {
      monitor.instanceDestroyed(null);
      fail();
    } catch (IllegalArgumentException ise) {
      // expected
    }

  }

}
