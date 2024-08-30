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
package com.tc.util.concurrent;

/**
 * @see Timer
 * @see Runners
 */
public interface TaskRunner {

  /**
   * Constructs an unnamed timer to schedule tasks. Unlike {@link java.util.Timer},
   * all timers share a common thread pool, maintained by this {@link TaskRunner}.
   * <br>
   * Attempting to call this method after shutting down the task runner throws {@link IllegalStateException}.
   *
   * @return a new unnamed {@link Timer} instance
   */
  Timer newTimer();

  /**
   * Constructs a named timer to schedule tasks. Unlike {@link java.util.Timer},
   * all timers share a common thread pool, maintained by this {@link TaskRunner}.
   * <br>
   * Attempting to call this method after shutting down the task runner throws {@link IllegalStateException}.
   *
   * @return a new named {@link Timer} instance
   */
  Timer newTimer(String name);

  /**
   * Shuts down the timer and all its scheduled tasks. Currently running tasks are not affected.
   *
   * @param timer the timer to cancel
   */
  void cancelTimer(final Timer timer);

  /**
   * Attempts to stop all actively executing tasks and halts the
   * processing of waiting tasks.
   * <br>
   * There are no guarantees beyond best-effort attempts to stop
   * processing actively executing tasks.  For example, typical
   * implementations will cancel via {@link Thread#interrupt}, so any
   * task that fails to respond to interrupts may never terminate.
   */
  void shutdown();

}
