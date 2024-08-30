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
package com.terracotta.toolkit.concurrent.locks;

import org.terracotta.toolkit.ToolkitObjectType;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;
import org.terracotta.toolkit.internal.concurrent.locks.ToolkitLockTypeInternal;

import com.tc.platform.PlatformService;

public class ToolkitReadWriteLockImpl implements ToolkitReadWriteLock {
  private final String                      name;
  private final UnnamedToolkitReadWriteLock delegate;

  public ToolkitReadWriteLockImpl(PlatformService platformService, String name) {
    this.name = name;
    this.delegate = ToolkitLockingApi.createUnnamedReadWriteLock(ToolkitObjectType.READ_WRITE_LOCK, name,
                                                                 platformService, ToolkitLockTypeInternal.WRITE);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public ToolkitLock readLock() {
    return delegate.readLock();
  }

  @Override
  public ToolkitLock writeLock() {
    return delegate.writeLock();
  }
}
