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
package com.terracotta.toolkit.collections.servermap.api;

public class ServerMapLocalStoreConfig {

  private final String  localStoreManagerName;
  private final String  localStoreName;
  private final boolean pinnedInLocalMemory;
  private final long    maxBytesLocalHeap;
  private final long    maxBytesLocalOffheap;
  private final int     maxCountLocalHeap;
  private final boolean overflowToOffheap;

  public ServerMapLocalStoreConfig(ServerMapLocalStoreConfigParameters parameters) {
    this.localStoreManagerName = parameters.getLocalStoreManagerName();
    this.localStoreName = parameters.getLocalStoreName();
    if (localStoreName == null || localStoreName.equals("")) {
      //
      throw new AssertionError("Name for the local store cannot be null or empty");
    }
    this.maxBytesLocalHeap = parameters.getMaxBytesLocalHeap();
    this.maxBytesLocalOffheap = parameters.getMaxBytesLocalOffheap();
    this.maxCountLocalHeap = parameters.getMaxCountLocalHeap();
    this.overflowToOffheap = parameters.isOverflowToOffheap();
    this.pinnedInLocalMemory = parameters.isPinnedInLocalMemory();
  }

  public String getLocalStoreManagerName() {
    return localStoreManagerName;
  }

  public String getLocalStoreName() {
    return localStoreName;
  }

  public long getMaxBytesLocalHeap() {
    return maxBytesLocalHeap;
  }

  public long getMaxBytesLocalOffheap() {
    return maxBytesLocalOffheap;
  }

  public int getMaxCountLocalHeap() {
    return maxCountLocalHeap;
  }

  public boolean isOverflowToOffheap() {
    return overflowToOffheap;
  }

  public boolean isPinnedInLocalMemory() {
    return pinnedInLocalMemory;
  }
}
