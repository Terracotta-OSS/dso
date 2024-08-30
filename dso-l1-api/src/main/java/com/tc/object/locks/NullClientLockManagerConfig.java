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
package com.tc.object.locks;


public class NullClientLockManagerConfig implements ClientLockManagerConfig {

  private long timeoutInterval = ClientLockManagerConfig.DEFAULT_TIMEOUT_INTERVAL;
  
  public NullClientLockManagerConfig() {
    this.timeoutInterval = ClientLockManagerConfig.DEFAULT_TIMEOUT_INTERVAL; 
  }
  
  public NullClientLockManagerConfig(long timeoutInterval) {
    this.timeoutInterval = timeoutInterval;
  }
  
  @Override
  public long getTimeoutInterval() {
    return timeoutInterval;
  }
  
  public void setTimeoutInterval(long timeoutInterval) {
    this.timeoutInterval = timeoutInterval;
  }

  @Override
  public int getStripedCount() {
    return ClientLockManagerConfig.DEFAULT_STRIPED_COUNT;
  }

}
