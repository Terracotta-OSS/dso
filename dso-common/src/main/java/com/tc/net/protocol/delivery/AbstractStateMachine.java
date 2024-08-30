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
package com.tc.net.protocol.delivery;

import com.tc.util.Assert;

/**
 * 
 */
public abstract class AbstractStateMachine {
  private State   current;
  private boolean started = false;
  private boolean paused  = true;

  public abstract void execute(OOOProtocolMessage msg);

  public final synchronized boolean isStarted() {
    return started;
  }

  public final synchronized void start() {
    Assert.eval(!started);
    started = true;
    paused = true;
    switchToState(initialState());
  }

  public final synchronized void pause() {
    Assert.eval("started: " + started + ", paused: " + paused, started && !paused);
    basicPause();
    this.paused = true;
  }

  protected void basicPause() {
    // Override me
  }

  protected void basicResume() {
    // Override me
  }

  public final synchronized void resume() {
    Assert.eval("started: " + started + ", paused: " + paused, started && paused);
    this.paused = false;
    basicResume();
  }

  public final synchronized boolean isPaused() {
    return this.paused;
  }

  protected synchronized void switchToState(State state) {
    Assert.eval(state != null && isStarted());
    this.current = state;
    state.enter();
  }

  public synchronized final State getCurrentState() {
    return current;
  }

  protected abstract State initialState();

  public abstract void reset();

  @Override
  public String toString() {
    return "Started: " + isStarted() + "; Paused: " + isPaused();
  }
}
