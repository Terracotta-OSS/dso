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

import com.tc.test.TCTestCase;

/**
 * Unit test for {@link ToStringStringifier}.
 */
public class ToStringStringifierTest extends TCTestCase {

  private static class MyObj {
    private final String value;

    public MyObj(String value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return "XXX" + this.value + "YYY";
    }
  }

  public void testNull() {
    // Make sure we can disambiguate (new String("null")) and null.
    String nullAsString = ToStringStringifier.INSTANCE.toString(null);
    assertFalse(nullAsString.equals("null"));
    assertTrue(nullAsString.trim().length() > 0);
  }

  public void testStringification() {
    assertEquals("XXXYYY", ToStringStringifier.INSTANCE.toString(new MyObj("")));
    assertEquals("XXX   YYY", ToStringStringifier.INSTANCE.toString(new MyObj("   ")));
    assertEquals("XXXaaabbbYYY", ToStringStringifier.INSTANCE.toString(new MyObj("aaabbb")));
  }

}