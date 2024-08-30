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

import org.terracotta.toolkit.internal.concurrent.locks.ToolkitLockTypeInternal;

import com.tc.object.locks.LockLevel;

// package protected class
class ToolkitLockDetail {

  private final Object                  lockId;
  private final ToolkitLockTypeInternal lockType;

  public static ToolkitLockDetail newLockDetail(String stringLockId, ToolkitLockTypeInternal lockType) {
    return new ToolkitLockDetail(stringLockId, lockType);
  }

  public static ToolkitLockDetail newLockDetail(long longLockId, ToolkitLockTypeInternal lockType) {
    return new ToolkitLockDetail(longLockId, lockType);
  }

  private ToolkitLockDetail(Object lockId, ToolkitLockTypeInternal lockType) {
    this.lockId = lockId;
    this.lockType = lockType;
  }

  public Object getLockId() {
    return lockId;
  }

  public ToolkitLockTypeInternal getToolkitInternalLockType() {
    return lockType;
  }

  public LockLevel getLockLevel() {
    return LockingUtils.translate(lockType);
  }

}
