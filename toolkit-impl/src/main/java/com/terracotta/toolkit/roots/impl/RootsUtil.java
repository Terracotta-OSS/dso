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
package com.terracotta.toolkit.roots.impl;

import org.terracotta.toolkit.internal.concurrent.locks.ToolkitLockTypeInternal;

import com.tc.net.GroupID;
import com.tc.platform.PlatformService;
import com.terracotta.toolkit.concurrent.locks.ToolkitLockingApi;

public final class RootsUtil {

  private RootsUtil() {
    // private
  }

  public static interface RootObjectCreator<T> {
    T create();
  }

  public static <T> T lookupOrCreateRootInGroup(PlatformService platformService, GroupID gid, String name,
                                                RootObjectCreator<T> creator) {
    ToolkitLockingApi.lock(name, ToolkitLockTypeInternal.READ, platformService);
    try {
      Object root = platformService.lookupRoot(name, gid);
      if (root != null) { return (T) root; }
    } finally {
      ToolkitLockingApi.unlock(name, ToolkitLockTypeInternal.READ, platformService);
    }

    ToolkitLockingApi.lock(name, ToolkitLockTypeInternal.WRITE, platformService);
    try {
      return (T) platformService.lookupOrCreateRoot(name, creator.create(), gid);
    } finally {
      ToolkitLockingApi.unlock(name, ToolkitLockTypeInternal.WRITE, platformService);
    }
  }
}
