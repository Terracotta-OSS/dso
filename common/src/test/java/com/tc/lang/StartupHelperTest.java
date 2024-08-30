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
package com.tc.lang;


import com.tc.logging.NullTCLogger;

import java.util.concurrent.atomic.AtomicReference;

import junit.framework.TestCase;

public class StartupHelperTest extends TestCase {

  public void testException() throws Throwable {
    final AtomicReference<Throwable> error = new AtomicReference(null);

    ThreadGroup group = new ThreadGroup("group") {
      @Override
      public void uncaughtException(Thread t, Throwable e) {
        error.set(e);
      }
    };

    final RuntimeException re = new RuntimeException("da bomb");

    StartupHelper helper = new StartupHelper(group, new StartupHelper.StartupAction() {
      @Override
      public void execute() throws Throwable {
        throw re;
      }
    });

    try {
      helper.startUp();
    } catch (RuntimeException e) {
      //
    }

    RuntimeException thrown = (RuntimeException) error.get();
    if (thrown == null) {
      fail("no exception delivered to group");
    }

    assertTrue(thrown == re);
  }

  public void testGroup() throws Throwable {
    final TCThreadGroup group = new TCThreadGroup(new ThrowableHandlerImpl(new NullTCLogger()));

    StartupHelper helper = new StartupHelper(group, new StartupHelper.StartupAction() {
      @Override
      public void execute() throws Throwable {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        if (tg != group) { throw new AssertionError("wrong thread group: " + tg); }
      }
    });

    helper.startUp();
  }

}
