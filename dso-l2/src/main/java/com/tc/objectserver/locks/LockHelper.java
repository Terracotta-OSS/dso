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
package com.tc.objectserver.locks;

import com.tc.async.api.Sink;
import com.tc.object.locks.ServerLockContextStateMachine;
import com.tc.objectserver.locks.timer.LockTimer;
import com.tc.objectserver.locks.timer.TimerCallback;

public class LockHelper {
  private final LockTimer                     lockTimer;
  private final Sink                          lockSink;
  private final LockStore                     lockStore;
  private final ServerLockContextStateMachine contextStateMachine;
  private final TimerCallback                 timerCallback;

  public LockHelper(Sink lockSink, LockStore lockStore, TimerCallback timerCallback) {
    this.lockTimer = new LockTimer();
    this.lockSink = lockSink;
    this.lockStore = lockStore;
    this.timerCallback = timerCallback;
    this.contextStateMachine = new ServerLockContextStateMachine();
  }

  public LockTimer getLockTimer() {
    return lockTimer;
  }

  public Sink getLockSink() {
    return lockSink;
  }

  public LockStore getLockStore() {
    return lockStore;
  }

  public ServerLockContextStateMachine getContextStateMachine() {
    return contextStateMachine;
  }

  public TimerCallback getTimerCallback() {
    return timerCallback;
  }
}
