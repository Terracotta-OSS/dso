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
package com.terracotta.toolkit;

import org.terracotta.toolkit.internal.feature.ManagementInternalFeature;
import org.terracotta.toolkit.internal.feature.ToolkitManagementEvent;

import com.tc.management.TCManagementEvent;
import com.tc.platform.PlatformService;
import com.terracotta.toolkit.feature.EnabledToolkitFeature;

import java.util.concurrent.ExecutorService;

public class ManagementInternalFeatureImpl extends EnabledToolkitFeature implements ManagementInternalFeature {

  private final PlatformService platformService;

  public ManagementInternalFeatureImpl(PlatformService platformService) {
    this.platformService = platformService;
  }

  @Override
  public Object registerManagementService(Object service, ExecutorService executorService) {
    return platformService.registerManagementService(service, executorService);
  }

  @Override
  public void unregisterManagementService(Object serviceID) {
    platformService.unregisterManagementService(serviceID);
  }

  @Override
  public void sendEvent(ToolkitManagementEvent event) {
    platformService.sendEvent(new TCManagementEvent(event.getPayload(), event.getType()));
  }

}
