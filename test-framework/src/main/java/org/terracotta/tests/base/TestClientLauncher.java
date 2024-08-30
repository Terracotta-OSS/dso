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
package org.terracotta.tests.base;

import java.lang.reflect.Constructor;
import java.util.Arrays;

public class TestClientLauncher {

  /**
   * @param args
   */
  @SuppressWarnings("unchecked")
  public static void main(String[] args) {
    String clientClassName = args[0];
    // Client Class must implement Runnable and has a constructor with
    // arguments String[]
    try {
      Class<? extends Runnable> clientClass = (Class<? extends Runnable>) TestClientLauncher.class.getClassLoader()
          .loadClass(clientClassName);

      String[] clientArgs = Arrays.copyOfRange(args, 1, args.length);
      Constructor<Runnable> constructor;
      constructor = (Constructor<Runnable>) clientClass.getConstructor(String[].class);
      Runnable newInstance = constructor.newInstance(new Object[] { clientArgs });
      newInstance.run();
    } catch (NoSuchMethodException e) {
      System.out.println("Class " + clientClassName
                         + " should have one constructor having argument and array of String( String[]) . ");
      e.printStackTrace();
      throw new AssertionError("Exception while launching test client : " + e.getMessage());
    } catch (Exception e) {
      e.printStackTrace();
      throw new AssertionError("Exception while launching test client : " + e.getMessage());
    }

  }
}
