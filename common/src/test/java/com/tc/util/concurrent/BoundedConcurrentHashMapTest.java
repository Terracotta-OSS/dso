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
package com.tc.util.concurrent;

import com.tc.test.TCTestCase;
import com.tc.util.Assert;

import java.util.Iterator;
import java.util.Map.Entry;

public class BoundedConcurrentHashMapTest extends TCTestCase {
  public void testBasic() {
    BoundedConcurrentHashMap<String, Integer> boundedConcurrentHashMap = new BoundedConcurrentHashMap<String, Integer>(
                                                                                                                       1024L);

    for (int i = 0; i < 100; i++) {
      Assert.assertNull(boundedConcurrentHashMap.put("key" + i, i));
    }

    for (int i = 0; i < 100; i++) {
      Assert.assertNotNull(boundedConcurrentHashMap.get("key" + i));
      Assert.assertTrue(boundedConcurrentHashMap.get("key" + i).equals(Integer.valueOf(i)));
    }
  }

  public void testBounded() {
    final BoundedConcurrentHashMap<String, Integer> boundedConcurrentHashMap = new BoundedConcurrentHashMap<String, Integer>(
                                                                                                                             32L);

    System.err.println("testBounded Segment size is " + boundedConcurrentHashMap.segments.length);

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 64; i++) {
          Assert.assertNull(boundedConcurrentHashMap.put("key" + i, i));
          System.err.println("testBounded Put success " + i);
        }
      }
    };

    Thread t = new Thread(runnable);
    t.start();

    ThreadUtil.reallySleep(10000);
    Assert.assertTrue(t.isAlive());

    for (int i = 0; i < 64; i++) {
      Integer k = boundedConcurrentHashMap.remove("key" + i);
      Assert.assertTrue(k.intValue() == i);
      System.err.println("Remove success " + i);
      ThreadUtil.reallySleep(100);
    }

    Assert.assertEquals(0, boundedConcurrentHashMap.size());
    Assert.assertFalse(t.isAlive());
  }

  public void testBoundedIterator() {
    final BoundedConcurrentHashMap<String, Integer> boundedConcurrentHashMap = new BoundedConcurrentHashMap<String, Integer>(
                                                                                                                             32L);

    System.err.println("Segment size is " + boundedConcurrentHashMap.segments.length);

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 64; i++) {
          Assert.assertNull(boundedConcurrentHashMap.put("key" + i, i));
          System.err.println("testBoundedIterator Put success " + i);
        }
      }
    };

    Thread t = new Thread(runnable);
    t.start();

    ThreadUtil.reallySleep(1000);
    Assert.assertTrue(t.isAlive());

    while (boundedConcurrentHashMap.size() != 0 || t.isAlive()) {
      Iterator<Entry<String, Integer>> iterator = boundedConcurrentHashMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Entry<String, Integer> entry = iterator.next();
        iterator.remove();
        System.err.println("testBoundedIterator Remove success " + entry.getKey());
      }
    }

    ThreadUtil.reallySleep(1000);
    Assert.assertFalse(t.isAlive());
  }

  public void testBoundedClear() {
    final BoundedConcurrentHashMap<String, Integer> boundedConcurrentHashMap = new BoundedConcurrentHashMap<String, Integer>(
                                                                                                                             32L);

    System.err.println("Segment size is " + boundedConcurrentHashMap.segments.length);

    Runnable runnable = new Runnable() {
      @Override
      public void run() {
        for (int i = 0; i < 64; i++) {
          Assert.assertNull(boundedConcurrentHashMap.put("key" + i, i));
          System.err.println("testBoundedClear Put success " + i);
        }
      }
    };

    Thread t = new Thread(runnable);
    t.start();

    ThreadUtil.reallySleep(1000);
    Assert.assertTrue(t.isAlive());

    while (t.isAlive()) {
      boundedConcurrentHashMap.clear();
    }

    Assert.assertFalse(t.isAlive());
  }
}
