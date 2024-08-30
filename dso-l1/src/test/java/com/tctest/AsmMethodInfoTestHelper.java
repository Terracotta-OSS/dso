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
package com.tctest;

import com.tc.util.Assert;

import java.util.HashMap;
import java.util.Map;

/**
 * @author steve
 */
public class AsmMethodInfoTestHelper {
  AsmMethodInfoTestHelper(int countStart) {
    this.count = countStart;
    myRoot = new HashMap();
  }

  private final Map myRoot; // = new HashMap();
  private long      count;

  public long test4(int i, Object foo) {
    synchronized (myRoot) {
      long start = System.currentTimeMillis();
      int s = myRoot.size();
      long c = count++;
      if (myRoot.containsKey(new Long(c))) {
        Assert.eval(false);
      }
      myRoot.put(new Long(c), new TestObj(new TestObj(null)));
      if (myRoot.size() != s + 1) System.out.println("Wrong size!:" + s + " new size:" + myRoot.size());
      Assert.eval(myRoot.size() == s + 1);
      // System.out.println("^^^TOTAL SIZE ADD:" + myRoot.size() + "^^^:" + this);
      return System.currentTimeMillis() - start;
    }
  }

  public long test5(int i, Object foo) {
    synchronized (myRoot) {
      long start = System.currentTimeMillis();
      int s = myRoot.size();
      myRoot.remove(new Long(count - 1));
      if (myRoot.size() != s - 1) System.out.println("Wrong size!:" + s + " new size:" + myRoot.size());
      Assert.eval(myRoot.size() == s - 1);
      // System.out.println("^^^TOTAL SIZE REMOVE:" + myRoot.size() + "^^^:" + this);
      return System.currentTimeMillis() - start;
    }
  }

  public static class TestObj {
    private TestObj       obj;
    private final String  string  = "Steve";
    private final int     integer = 22;
    private final boolean bool    = false;
    private final Map     map     = new HashMap();

    private TestObj() {
      //
    }

    public TestObj(TestObj obj) {
      this.obj = obj;
      for (int i = 0; i < 30; i++) {
        map.put(new Long(i), new TestObj());
      }
    }

    public Object getObject() {
      return this.obj;
    }

    public boolean check() {
      return string.equals("Steve") && Integer.valueOf(integer).equals(Integer.valueOf(22))
             && Boolean.valueOf(bool) == Boolean.FALSE;
    }
  }
}
