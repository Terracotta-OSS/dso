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
package com.tc.management.remote.protocol;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

public class ProtocolProviderTest {

  @Test
  public void testJmxEnvSetsClassLoaderDelegatingToThreadContextClassLoader() throws Exception {
    Map env = new HashMap();
    ProtocolProvider.addTerracottaJmxProvider(env);

    assertThat(env.get("jmx.remote.default.class.loader"), is(notNullValue()));
    assertThat(env.get("jmx.remote.protocol.provider.class.loader"), is(notNullValue()));

    ClassLoader cl = (ClassLoader)env.get("jmx.remote.default.class.loader");
    assertSame(cl.loadClass("com.tc.management.remote.protocol.ProtocolProvider"), ProtocolProvider.class);

    try {
      cl.loadClass("bla.bla.Blah");
      fail("expected ClassNotFoundException");
    } catch (ClassNotFoundException e) {
      // expected
    }

    ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
    try {
      Thread.currentThread().setContextClassLoader(new ClassLoader() {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
          if (name.equals("bla.bla.Blah")) {
            return String.class;
          }
          return super.loadClass(name);
        }
      });

      assertSame(cl.loadClass("bla.bla.Blah"), String.class);
    } finally {
      Thread.currentThread().setContextClassLoader(oldContextClassLoader);
    }
  }

}
