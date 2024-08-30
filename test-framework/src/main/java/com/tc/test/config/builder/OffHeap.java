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
package com.tc.test.config.builder;

/**
 * @author Ludovic Orban
 */
public class OffHeap {

  private boolean enabled;
  private String maxDataSize;

  public OffHeap() {
  }

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
  
  public OffHeap enabled(boolean enabled) {
    this.enabled = enabled;
    return this;
  }
  
  public String getMaxDataSize() {
    return maxDataSize;
  }

  public void setMaxDataSize(String maxDataSize) {
    this.maxDataSize = maxDataSize;
  }

  public OffHeap maxDataSize(String maxDataSize) {
    this.maxDataSize = maxDataSize;
    return this;
  }

}
