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
package com.terracotta.toolkit.express;

import junit.framework.TestCase;

public class AppClassLoaderTest extends TestCase {
  Loader loader1 = new Loader("1", Object.class);
  Loader loader2 = new Loader("2", System.class);

  public void testBasic() throws Exception {
    AppClassLoader loader = new AppClassLoader(getClass().getClassLoader());

    loader.addLoader(loader1);
    loader.addLoader(loader2);

    Class<?> c = loader.loadClass(AppClassLoaderTest.class.getName());
    assertNotNull(c);
    assertEquals(getClass().getClassLoader(), c.getClassLoader());

    c = loader.loadClass("1");
    assertNotNull(c);
    assertEquals(Object.class, c);

    c = loader.loadClass("2");
    assertNotNull(c);
    assertEquals(System.class, c);

    try {
      loader.loadClass("com.spongebob.square.pants.ReallyReallyShouldNotExist");
      fail();
    } catch (ClassNotFoundException cnfe) {
      // expected
    }

    loader1 = null;
    gc();

    try {
      loader.loadClass("1");
      fail();
    } catch (ClassNotFoundException cnfe) {
      // expected
    }

    c = loader.loadClass("2");
    assertNotNull(c);
    assertEquals(System.class, c);
  }

  private static void gc() {
    for (int i = 0; i < 3; i++) {
      System.gc();
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        //
      }
    }
  }

  private static class Loader extends ClassLoader {
    private final String   myName;
    private final Class<?> myClass;

    public Loader(String name, Class<?> myClass) {
      super(null);
      this.myName = name;
      this.myClass = myClass;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
      if (this.myName.equals(name)) { return myClass; }
      throw new ClassNotFoundException();
    }
  }

}
