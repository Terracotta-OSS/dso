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
package com.tc.util;

import junit.framework.TestCase;

public class UtilTest extends TestCase {

  public void test() {
    System.out.println(Util.enumerateArray(null));
    System.out.println(Util.enumerateArray(this)); // not an array
    System.out.println(Util.enumerateArray(new Object[] {}));
    System.out.println(Util.enumerateArray(new Object[] { null, "timmy" }));
    System.out.println(Util.enumerateArray(new char[] {}));
    System.out.println(Util.enumerateArray(new long[] { 42L }));
  }

}
