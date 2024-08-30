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
package com.tc.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A space efficient, grow'able list of ints -- not very fancy ;-)
 */
public class IntList {
  private static final int BLOCK        = 4096;
  private final List       arrays       = new ArrayList();
  private int[]            current;
  private int              currentIndex = 0;
  private int              size;

  public IntList() {
    next();
  }

  public void add(int i) {
    if (currentIndex == BLOCK) {
      next();
    }

    current[currentIndex++] = i;
    size++;
  }

  public int size() {
    return size;
  }

  public int[] toArray() {
    int[] rv = new int[size];
    int index = 0;
    int remaining = size;
    for (Iterator i = arrays.iterator(); i.hasNext();) {
      int len = Math.min(remaining, BLOCK);
      System.arraycopy(i.next(), 0, rv, index, len);
      remaining -= len;
      index += len;
    }

    return rv;
  }

  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append('{');
    int[] vals = toArray();
    for (int i = 0, n = vals.length, last = n - 1; i < n; i++) {
      sb.append(vals[i]);
      if (i != last) {
        sb.append(", ");
      }
    }
    sb.append('}');
    return sb.toString();
  }

  public int get(int index) {
    int whichArray = index == 0 ? 0 : index / BLOCK;
    return ((int[]) arrays.get(whichArray))[index % BLOCK];
  }

  private void next() {
    current = new int[BLOCK];
    currentIndex = 0;
    arrays.add(current);
  }

}
