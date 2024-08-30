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

import com.tc.logging.TCLogger;

import java.util.concurrent.Callable;

/**
 * A {@link ThrowableHandler} for Terracotta Client which avoids {@link System#exit(int)} on inconsistent state of
 * Terracotta Client. This handler will shutdown Terracotta Client instead through l1ShutdownCallable.
 */
public class L1ThrowableHandler extends ThrowableHandlerImpl {
  private final Callable<Void> l1ShutdownCallable;

  public L1ThrowableHandler(TCLogger logger, Callable<Void> l1ShutdownCallable) {
    super(logger);
    this.l1ShutdownCallable = l1ShutdownCallable;
  }

  @Override
  protected synchronized void exit(int status) {
    try {
      l1ShutdownCallable.call();
    } catch (Exception e) {
      logger.error("Exception while shutting down Terracotta Client", e);
    }
  }

}
