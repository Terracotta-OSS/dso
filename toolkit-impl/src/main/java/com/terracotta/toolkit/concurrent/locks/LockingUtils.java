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

public class LockingUtils {

  public static LockLevel translate(final ToolkitLockTypeInternal lockType) {
    switch (lockType) {
      case WRITE:
        return LockLevel.WRITE;
      case READ:
        return LockLevel.READ;
      case SYNCHRONOUS_WRITE:
        return LockLevel.SYNCHRONOUS_WRITE;
      case CONCURRENT:
        return LockLevel.CONCURRENT;

    }

    // don't do this as the "default" in the switch block so the compiler can catch errors
    throw new AssertionError("unknown lock type: " + lockType);
  }

  public static ToolkitLockTypeInternal translate(final LockLevel lockLevel) {
    switch (lockLevel) {
      case WRITE:
        return ToolkitLockTypeInternal.WRITE;
      case READ:
        return ToolkitLockTypeInternal.READ;
      case SYNCHRONOUS_WRITE:
        return ToolkitLockTypeInternal.SYNCHRONOUS_WRITE;
      case CONCURRENT:
        return ToolkitLockTypeInternal.CONCURRENT;
    }

    // don't do this as the "default" in the switch block so the compiler can catch errors
    throw new AssertionError("unknown lock level: " + lockLevel);
  }
}
