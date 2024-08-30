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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * The purpose of this class to execute a startup action (ie. "start the server", or "start the client", etc) in a
 * thread in the specified thread group. The side effect of doing this is that any more threads spawned by the startup
 * action will inherit the given thread group. It is somewhat fragile, and sometimes impossible (see java.util.Timer) to
 * be explicit about the thread group when spawning threads
 */
public class StartupHelper {

  private final StartupAction action;
  private final ThreadGroup threadGroup;

  public StartupHelper(ThreadGroup threadGroup, StartupAction action) {
    this.threadGroup = threadGroup;
    this.action = action;
  }

  public void startUp() {
    ExecutorService executor = Executors.newSingleThreadExecutor(r -> new Thread(threadGroup, r));
    try {
      Future<?> submit = executor.submit(() -> {
        try {
          action.execute();
        } catch (Throwable t) {
          threadGroup.uncaughtException(Thread.currentThread(), t);
          throw new RuntimeException(t);
        }
      });
      try {
        submit.get();
      } catch (InterruptedException exception) {
        submit.cancel(true);
      } catch (ExecutionException e) {
        throw new RuntimeException(e.getCause());
      }
    } finally {
      executor.shutdown();
    }
  }

  public interface StartupAction {
    void execute() throws Throwable;
  }

}
