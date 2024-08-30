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
package com.terracotta.toolkit.mockl2.test;

import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.internal.TerracottaL1Instance;

import com.tc.exception.ImplementMe;
import com.terracotta.toolkit.TerracottaToolkit;
import com.terracotta.toolkit.ToolkitCacheManagerProvider;


public class ToolkitUnitTest {

  private final MockPlatformService platformService;

  public ToolkitUnitTest() {
    platformService = new MockPlatformService();
  }

  public Toolkit getToolKit() {
    Toolkit toolkit = new TerracottaToolkit(new TerracottaL1Instance() {
      @Override
      public void shutdown() {
        throw new ImplementMe();
      }
    }, new ToolkitCacheManagerProvider(), false, getClass().getClassLoader(), platformService);
    return toolkit;
  }

  public void addPlatformListener(MockPlatformListener listener) {
    platformService.addPlatformListener(listener);
  }

}
