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
package com.tc.async.impl;

import com.tc.async.api.OrderedEventContext;
import com.tc.async.api.Sink;
import com.tc.logging.TCLogging;
import com.tc.test.TCTestCase;

import java.security.SecureRandom;
import java.util.ArrayList;

public class OrderedSinkTest extends TCTestCase {

  public void testBasic() throws Exception {
    MockSink des = new MockSink();
    Sink s = new OrderedSink(TCLogging.getLogger(OrderedSink.class), des);

    OrderedEventContext oc = new MyOrderedEventContext(1);
    s.add(oc);
    assertEvents(des, 1, 1);

    oc = new MyOrderedEventContext(3);
    s.add(oc);
    assertTrue(des.queue.isEmpty());

    oc = new MyOrderedEventContext(4);
    s.add(oc);
    assertTrue(des.queue.isEmpty());

    oc = new MyOrderedEventContext(2);
    s.add(oc);
    assertEvents(des, 2, 3);

    oc = new MyOrderedEventContext(2);
    boolean failed = false;
    try {
      s.add(oc);
      failed = true;
    } catch (AssertionError ae) {
      // Excepted
    }
    assertFalse(failed);
  }

  public void testComplex() throws Exception {
    MockSink des = new MockSink();
    Sink s = new OrderedSink(TCLogging.getLogger(OrderedSink.class), des);
    
    ArrayList l = createOrderedEvents(1000);
    SecureRandom r = new SecureRandom();

    while (!l.isEmpty()) {
      int idx = r.nextInt(l.size());
      OrderedEventContext oc = (OrderedEventContext) l.remove(idx);
      s.add(oc);
    }
    
    assertEvents(des, 1, 1000);
  }

  private ArrayList createOrderedEvents(int count) {
    ArrayList al = new ArrayList(count);
    for (int i = 1; i <= count; i++) {
      al.add(new MyOrderedEventContext(i));
    }
    return al;
  }

  private void assertEvents(MockSink des, int id, int count) {
    while (count-- > 0) {
      MyOrderedEventContext oc;
      try {
        oc = (MyOrderedEventContext) des.queue.take();
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
      assertEquals(id++, oc.getSequenceID());
    }
    assertTrue(des.queue.isEmpty());
  }

  private static class MyOrderedEventContext implements OrderedEventContext {

    long id;

    public MyOrderedEventContext(int i) {
      id = i;
    }

    @Override
    public long getSequenceID() {
      return id;
    }

  }
}
