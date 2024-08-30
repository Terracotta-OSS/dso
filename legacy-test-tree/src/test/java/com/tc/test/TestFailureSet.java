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
package com.tc.test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TestFailureSet {
  private final List list = new ArrayList();

  public void put(TestFailure f) {
    synchronized (list) {
      list.add(f);
    }
  }

  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer("Test failures...\n");
    synchronized (list) {
      for (Iterator i = list.iterator(); i.hasNext();) {
        TestFailure f = (TestFailure) i.next();
        buf.append(f + "\n");
      }
    }
    return buf.toString();
  }

  public int size() {
    synchronized (list) {
      return list.size();
    }
  }
}