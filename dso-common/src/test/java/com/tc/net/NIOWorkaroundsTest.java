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
package com.tc.net;

import java.util.Properties;

import junit.framework.TestCase;

public class NIOWorkaroundsTest extends TestCase {

  public void testSolaris10Workaround() {
    assertFalse(NIOWorkarounds.solaris10Workaround(makeProps("sun", "SunOS", "5.10", "1.5.0_11")));
    assertFalse(NIOWorkarounds.solaris10Workaround(makeProps("sun", "SunOS", "5.10", "1.5.0_09")));
    assertFalse(NIOWorkarounds.solaris10Workaround(makeProps("sun", "SunOS", "5.10", "1.5.0_08")));
    assertTrue(NIOWorkarounds.solaris10Workaround(makeProps("sun", "SunOS", "5.10", "1.5.0_07")));
    assertTrue(NIOWorkarounds.solaris10Workaround(makeProps("sun", "SunOS", "5.10", "1.5.0")));
    assertFalse(NIOWorkarounds.solaris10Workaround(makeProps("sun", "SunOS", "5.10", "1.6.0")));
    assertFalse(NIOWorkarounds.solaris10Workaround(makeProps("sun", "SunOS", "5.10", "1.7.0")));
    assertTrue(NIOWorkarounds.solaris10Workaround(makeProps("sun", "SunOS", "5.10", "1.4.2_11")));
    assertFalse(NIOWorkarounds.solaris10Workaround(makeProps("sun", "SunOS", "5.9", "1.4.2_11")));
    assertFalse(NIOWorkarounds.solaris10Workaround(makeProps("sun", "Linux", "5.10", "1.4.2_11")));
    assertFalse(NIOWorkarounds.solaris10Workaround(makeProps("sun", "Linux", "5.10", "1.5.0_09")));
    assertFalse(NIOWorkarounds.solaris10Workaround(makeProps("bea", "SunOS", "5.10", "1.5.0_09")));
  }

  private static Properties makeProps(String vendor, String osName, String osVersion, String javaVersion) {
    Properties props = new Properties();
    props.setProperty("java.vendor", vendor);
    props.setProperty("os.name", osName);
    props.setProperty("os.version", osVersion);
    props.setProperty("java.version", javaVersion);
    return props;
  }

}
