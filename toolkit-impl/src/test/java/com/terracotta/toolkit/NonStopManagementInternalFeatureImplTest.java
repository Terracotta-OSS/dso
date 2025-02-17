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
package com.terracotta.toolkit;

import static org.mockito.Mockito.mock;

import org.mockito.Mockito;
import org.terracotta.toolkit.internal.feature.ToolkitManagementEvent;

import com.tc.management.TCManagementEvent;
import com.tc.platform.PlatformService;

import java.util.concurrent.ExecutorService;

import junit.framework.TestCase;

public class NonStopManagementInternalFeatureImplTest extends TestCase {

  public void test() {
    NonStopManagementInternalFeatureImpl feature = new NonStopManagementInternalFeatureImpl();

    try {
      feature.registerManagementService(null, null);
      fail();
    } catch (IllegalStateException ise) {
      // expected
    }

    try {
      feature.unregisterManagementService(null);
      fail();
    } catch (IllegalStateException ise) {
      // expected
    }

    feature.sendEvent(new ToolkitManagementEvent());

    PlatformService ps = mock(PlatformService.class);
    feature.setPlatformService(ps);
    Mockito.verify(ps, Mockito.times(1)).sendEvent(Mockito.any(TCManagementEvent.class));

    Object service = new Object();
    ExecutorService executor = mock(ExecutorService.class);
    Object serviceID = feature.registerManagementService(service, executor);
    Mockito.verify(ps, Mockito.times(1)).registerManagementService(service, executor);

    feature.unregisterManagementService(serviceID);
    Mockito.verify(ps, Mockito.times(1)).unregisterManagementService(serviceID);
  }

}
