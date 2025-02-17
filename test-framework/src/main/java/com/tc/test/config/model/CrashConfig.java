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
package com.tc.test.config.model;

/**
 * Controls the crashing of the servers in a test
 * 
 * @author rsingh
 */
public class CrashConfig {
  private long            serverCrashWaitTimeInSec = 60;
  private int             maxCrashCount            = Integer.MAX_VALUE;
  private ServerCrashMode crashMode                = ServerCrashMode.NO_CRASH;
  private boolean         shouldCleanDbOnCrash     = true;
  private long            initialDelayInSeconds    = 5;
  private boolean         ignoreUnexpectedL2Crash  = false;
  private boolean         autoStartCrasher         = true;

  /**
   * Get the initial delay time before starting to crash servers
   */
  public long getInitialDelayInSeconds() {
    return initialDelayInSeconds;
  }

  /**
   * Set the initial delay time before starting to crash servers
   */
  public void setInitialDelayInSeconds(long initialDelayInSeconds) {
    this.initialDelayInSeconds = initialDelayInSeconds;
  }

  /**
   * Wait time in seconds before a server is crahsed in a group
   * 
   * @return
   */
  public long getServerCrashWaitTimeInSec() {
    return serverCrashWaitTimeInSec;
  }

  /**
   * Sets the wait time before crashing a server
   * 
   * @param serverCrashWaitTimeInSec time in seconds to wait before a server is crashed
   */
  public void setServerCrashWaitTimeInSec(long serverCrashWaitTimeInSec) {
    this.serverCrashWaitTimeInSec = serverCrashWaitTimeInSec;
  }

  /**
   * Maximum number of server crashes allowed in the test, Default Unlimited
   */
  public int getMaxCrashCount() {
    return maxCrashCount;
  }

  /**
   * Sets the maximum number of server crashes in the test
   */
  public void setMaxCrashCount(int maxCrashCount) {
    this.maxCrashCount = maxCrashCount;
  }

  /**
   * returns the server crash mode, Default is NO_CRASH
   */
  public ServerCrashMode getCrashMode() {
    return crashMode;
  }

  /**
   * Sets the server crash mode
   */
  public void setCrashMode(ServerCrashMode crashMode) {
    this.crashMode = crashMode;
  }

  /**
   * @return true if the data directory of the server should be cleaned while crashing
   */
  public boolean shouldCleanDbOnCrash() {
    return shouldCleanDbOnCrash;
  }

  /**
   * @param shouldCleanDb cleans the db on crash if its set to true
   */
  public void setShouldCleanDbOnCrash(boolean shouldCleanDb) {
    this.shouldCleanDbOnCrash = shouldCleanDb;
  }

  /**
   * @param ignoreUnexpectedL2Crash true if the test framework should ignore an initiated L2 crash
   */
  public void setShouldIgnoreUnexpectedL2Crash(boolean ignoreUnexpectedL2Crash) {
    this.ignoreUnexpectedL2Crash = ignoreUnexpectedL2Crash;
  }

  /**
   * @return true if the framework should ignore unexpected L2 crashes (i.e. server going OOME).
   */
  public boolean shouldIgnoreUnexpectedL2Crash() {
    return ignoreUnexpectedL2Crash;
  }

  public boolean autoStartCrasher() {
    return autoStartCrasher;
  }

  public void setAutoStartCrasher(boolean autoStartCrasher) {
    this.autoStartCrasher = autoStartCrasher;
  }
}
