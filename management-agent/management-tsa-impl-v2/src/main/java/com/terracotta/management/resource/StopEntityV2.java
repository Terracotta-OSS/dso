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
package com.terracotta.management.resource;

import java.io.Serializable;

public class StopEntityV2 implements Serializable {
  private boolean forceStop;
  private boolean stopIfActive;
  private boolean stopIfPassive;
  private boolean restart;
  private boolean restartInSafeMode;

  public boolean isForceStop() {
    return forceStop;
  }

  public void setForceStop(boolean forceStop) {
    this.forceStop = forceStop;
  }


  public boolean isStopIfActive() {
    return stopIfActive;
  }

  public void setStopIfActive(boolean stopIfActive) {
    this.stopIfActive = stopIfActive;
  }

  public boolean isStopIfPassive() {
    return stopIfPassive;
  }

  public void setStopIfPassive(boolean stopIfPassive) {
    this.stopIfPassive = stopIfPassive;
  }

  public boolean isRestart() {
    return restart;
  }

  public void setRestart(boolean restart) {
    this.restart = restart;
  }

  public boolean isRestartInSafeMode() {
    return restartInSafeMode;
  }

  public void setRestartInSafeMode(boolean restartInSafeMode) {
    this.restartInSafeMode = restartInSafeMode;
  }
}
