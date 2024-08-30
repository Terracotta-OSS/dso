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
package com.tc.bytes;

import com.tc.util.Assert;

public class TCByteBufferTestUtil {

  public static void checkEquals(TCByteBuffer[] expected, TCByteBuffer[] actual) {
    System.out.println("expected length = " + expected.length + " actual length = " + actual.length);
    int j = 0;
    for (int i = 0; i < expected.length; i++) {
      while (expected[i].remaining() > 0) {
        byte expectedValue = expected[i].get();
        while (!actual[j].hasRemaining()) {
          j++;
          if (j >= actual.length) { throw new AssertionError("ran out of buffers: " + j); }
        }

        byte actualValue = actual[j].get();
        if (actualValue != expectedValue) {
          //
          throw new AssertionError("Data is not the same, " + actualValue + "!=" + expectedValue + " at expected[" + i
                                   + "] and actual[" + j + "]");
        }
      }
    }

    if (actual.length != 0) {
      Assert.assertEquals(actual.length, j + 1);
      Assert.assertEquals(0, actual[j].remaining());
    }
  }
}
