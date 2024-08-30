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
package com.terracotta.toolkit.util;

import com.tc.platform.PlatformService;
import com.tc.util.Assert;

public class ToolkitObjectStatusImpl implements ToolkitObjectStatus {
  private volatile boolean      isDestroyed;
  private final PlatformService service;

  public ToolkitObjectStatusImpl(PlatformService platformService) {
    service = platformService;
  }

  public void setDestroyed() {
    Assert.assertFalse(isDestroyed);
    this.isDestroyed = true;
  }

  @Override
  public int getCurrentRejoinCount() {
    return service.getRejoinCount();
  }

  @Override
  public boolean isDestroyed() {
    return isDestroyed;
  }

  @Override
  public boolean isRejoinInProgress() {
    return service.isRejoinInProgress();
  }

}
