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
package com.tc.test.config.model;

import java.io.Serializable;

/**
 * Pause Configuration for a Process to simluate Long GC Pauses. Use by TestFramwork. Especially by PauseManager.
 */
public final class PauseConfig implements Serializable {

  private final long pauseTime;
  private final long pauseInterval;
  private long       initialDelay = 5000; // in millis
  private int        maxPauses    = 30;

  public PauseConfig(long pauseTime, long pauseInterval) {
    this.pauseTime = pauseTime;
    this.pauseInterval = pauseInterval;
    if (pauseInterval <= pauseTime) {
      throw new IllegalArgumentException(  "pauseInterval "
          + pauseInterval
          + "should be greater than pauseTime "
          + pauseTime);
    }
  }

  public PauseConfig(long pauseTime) {
    super();
    this.pauseTime = pauseTime;
    this.pauseInterval = 0;
  }

  public long getPauseTime() {
    return pauseTime;
  }

  public long getPauseInterval() {
    return pauseInterval;
  }

  public int getMaxPauses() {
    return maxPauses;
  }

  public void setMaxPauses(int maxPauses) {
    this.maxPauses = maxPauses;
  }

  public long getInitialDelay() {
    return initialDelay;
  }

  public void setInitialDelay(long initialDelay) {
    this.initialDelay = initialDelay;
  }


}
