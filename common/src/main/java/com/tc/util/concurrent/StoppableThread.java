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
 * This class isn't very exciting, nor is it best (or only) way to implement a (safe) stoppable thread. It is useful to
 * subclass StoppableThread if need to support stop functionality and you don't want to assume that it's okay to just
 * interrupt() the thread at any given moment. In my opinion, if the code you're running in the thread isn't entirely in
 * your control, you probably don't want to randomly interrupt it. For threads that spend most of their time blocking on
 * something, simply use a timeout and periodically check stopRequested() to see if it is time to stop. <br>
 * <br>
 * README: You have to actually check stopRequested() someplace in your run() method for this thread to be "stoppable".
 */
public class StoppableThread extends Thread implements LifeCycleState {

  private volatile boolean stopRequested = false;

  public StoppableThread() {
    super();
  }

  public StoppableThread(Runnable target) {
    super(target);
  }

  public StoppableThread(String name) {
    super(name);
  }

  public StoppableThread(ThreadGroup group, Runnable target) {
    super(group, target);
  }

  public StoppableThread(Runnable target, String name) {
    super(target, name);
  }

  public StoppableThread(ThreadGroup group, String name) {
    super(group, name);
  }

  public StoppableThread(ThreadGroup group, Runnable target, String name) {
    super(group, target, name);
  }

  public StoppableThread(ThreadGroup group, Runnable target, String name, long stackSize) {
    super(group, target, name, stackSize);
  }

  @Override
  public boolean isStopRequested() {
    return stopRequested;
  }

  public void requestStop() {
    this.stopRequested = true;
  }

  @Override
  public boolean stopAndWait(long timeout) {
    requestStop();
    try {
      join(timeout);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    return !isAlive();
  }

}
