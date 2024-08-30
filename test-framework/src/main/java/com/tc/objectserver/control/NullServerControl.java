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
package com.tc.objectserver.control;


public class NullServerControl implements ServerControl {

  private boolean isRunning;

  @Override
  public synchronized void attemptForceShutdown() throws Exception {
    isRunning = false;
  }

  @Override
  public synchronized void shutdown() throws Exception {
    isRunning = false;
  }

  @Override
  public synchronized void crash() throws Exception {
    isRunning = false;
  }

  @Override
  public synchronized void start() throws Exception {
    this.isRunning = true;
  }

  @Override
  public void startWithoutWait() throws Exception {
    this.isRunning = true;
  }

  @Override
  public synchronized boolean isRunning() {
    return isRunning;
  }

  @Override
  public void mergeSTDOUT() {
    return;
  }

  @Override
  public void mergeSTDERR() {
    return;
  }

  @Override
  public void waitUntilShutdown() {
    return;
  }

  @Override
  public int getTsaPort() {
    return 0;
  }

  @Override
  public int getAdminPort() {
    return 0;
  }

  @Override
  public int waitFor() throws Exception {
    return 1;
  }

  @Override
  public void waitUntilL2IsActiveOrPassive() throws Exception {
    //
  }

  @Override
  public void pauseServer(long pauseTimeMillis) throws InterruptedException {
    //
  }

  @Override
  public void pauseServer() throws InterruptedException {

  }

  @Override
  public void unpauseServer() throws InterruptedException {

  }
}
