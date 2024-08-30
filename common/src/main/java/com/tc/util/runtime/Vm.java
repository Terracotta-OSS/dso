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
package com.tc.util.runtime;

import com.tc.util.Conversion;

import java.lang.reflect.Method;

/**
 * Utility class for understanding the current JVM version. Access the VM
 * version information by looking at {@link #VERSION} directly or calling the
 * static helper methods.
 */
public class Vm {

  /**
   * Version info is parsed from system properties and stored here.
   */
  public static final VmVersion VERSION;
  static {
    VERSION = new VmVersion(System.getProperties());
  }

  private Vm() {
    // utility class
  }

  /**
   * True if IBM JDK
   * 
   * @return True if IBM JDK
   */
  public static boolean isIBM() {
    return VERSION.isIBM();
  }

  /**
   * Attempts to determine the max direct memory usable by the JVM.
   * If this cannot be determined, return a constant matching the startup script.
   *
   * @return max direct memory usable
   */
  public static long maxDirectMemory() {
    try {
      Class<?> vmClass = Class.forName("sun.misc.VM");
      Method maxDirectMemory = vmClass.getDeclaredMethod("maxDirectMemory");
      return (Long) maxDirectMemory.invoke(null);
    } catch (Exception e) {
      // Returning a constant as the code is no longer present in Java 9 and this is what we pass in startup script
      try {
        return Conversion.memorySizeAsLongBytes("1048576g");
      } catch (Conversion.MetricsFormatException e1) {
        throw new AssertionError("Unexpected failure in converting default");
      }
    }
  }

  public static boolean isPreJava9() {
    return System.getProperty("java.specification.version").contains(".");
  }
}
