/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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

import java.util.concurrent.CyclicBarrier;


public class BitChangeNotReflected extends TCTestCase {

  private static final byte INITIAL    = 0x01;
  private static final int  LOOP_COUNT = 50000;

  public void testBitChangeNotReflected() throws Exception {
    BitData data = new BitData();
    Object lock = new Object();
    CyclicBarrier barrier = new CyclicBarrier(2);
    Thread t1 = new Changer(data, lock, barrier);
    Thread t2 = new Reader(data, lock, barrier);
    t1.start();
    t2.start();
    try {
      t1.join();
      t2.join();
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte shiftRightRotate(byte f) {
    byte f1 = (byte) (f >> 1);
    return (byte) (f1 == 0x00 ? 0x80 : f1);
  }

  static class Changer extends Thread {

    private final BitData       data;
    private final Object        lock;
    private final CyclicBarrier barrier;

    public Changer(BitData data, Object lock, CyclicBarrier barrier) {
      this.data = data;
      this.lock = lock;
      this.barrier = barrier;
    }

    @Override
    public void run() {
      try {
        run2();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    public void run2() throws Exception {
      int count = LOOP_COUNT;
      while (count-- > 0) {
        barrier.await();
        synchronized (lock) {
          data.shiftRightRotate();
        }
      }
    }

  }

  static class Reader extends Thread {

    private final BitData       data;
    private final Object        lock;
    private final CyclicBarrier barrier;

    public Reader(BitData data, Object lock, CyclicBarrier barrier) {
      this.data = data;
      this.lock = lock;
      this.barrier = barrier;
    }

    @Override
    public void run() {
      try {
        run2();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    public void run2() throws Exception {
      byte local = INITIAL;
      int count = LOOP_COUNT;
      while (count-- > 0) {
        local = shiftRightRotate(local);
        synchronized (lock) {
          data.isEqual(local);
        }
        barrier.await();
      }

    }

  }

  static class BitData {
    int  i;
    byte flag = INITIAL;

    void shiftRightRotate() {
      flag = BitChangeNotReflected.shiftRightRotate(flag);
    }

    boolean isEqual(byte f) {
      return (flag == f);
    }

    byte getFlag() {
      return flag;
    }

  }

}
