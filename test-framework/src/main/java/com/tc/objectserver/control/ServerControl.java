/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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

public interface ServerControl {

  public void mergeSTDOUT();

  public void mergeSTDERR();

  /**
   * Starts the shutdown sequence, but doesn't block.
   */
  public void attemptForceShutdown() throws Exception;

  /**
   * Starts the shutdown sequence, blocking until isRunning() is false.
   */
  public void shutdown() throws Exception;

  /**
   * Forces the server to exit, blocking until isRunning() is false.
   */
  public void crash() throws Exception;

  /**
   * Starts the server, blocking until isRunning() is true.
   */
  public void start() throws Exception;

  /**
   * Starts the server without waiting for it to start
   */
  public void startWithoutWait() throws Exception;

  /**
   * wait for the process termination
   */
  public int waitFor() throws Exception;

  /**
   * Returns true if the server responds.
   */
  public boolean isRunning();

  public void waitUntilShutdown() throws Exception;

  public int getTsaPort();

  public int getAdminPort();

  /**
   * Wait until the L2 is started (either ACTIVE or fully synced PASSIVE-STANDBY)
   */
  public void waitUntilL2IsActiveOrPassive() throws Exception;

  public void pauseServer(long pauseTimeMillis) throws InterruptedException;

  public void pauseServer() throws InterruptedException;

  public void unpauseServer() throws InterruptedException;
}
