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
package com.terracotta.toolkit.atomic;

import org.terracotta.toolkit.atomic.ToolkitTransaction;
import org.terracotta.toolkit.atomic.ToolkitTransactionController;
import org.terracotta.toolkit.atomic.ToolkitTransactionType;
import org.terracotta.toolkit.nonstop.NonStopException;

import com.tc.abortable.AbortedOperationException;
import com.tc.object.locks.LockID;
import com.tc.object.locks.LockLevel;
import com.tc.object.locks.StringLockID;
import com.tc.platform.PlatformService;
import com.terracotta.toolkit.feature.EnabledToolkitFeature;

public class ToolkitTransactionFeatureImpl extends EnabledToolkitFeature implements ToolkitTransactionController {
  private static final String   ATOMIC_LOCK_NAME = "atomic-concurrent-lock";
  private static final String   DELIMITER        = "|";
  private final PlatformService platformService;

  public ToolkitTransactionFeatureImpl(PlatformService platformService) {
    this.platformService = platformService;
  }

  @Override
  public ToolkitTransaction beginTransaction(ToolkitTransactionType type) {
    LockID lockID = getLockID(type);
    LockLevel level = getLockLevel(type);
    try {
      platformService.beginAtomicTransaction(lockID, level);
    } catch (AbortedOperationException e) {
      throw new NonStopException("begin timed out", e);
    }
    return new ToolkitTransactionImpl(platformService, lockID, level);
  }

  private LockID getLockID(ToolkitTransactionType type) {
    if (type == ToolkitTransactionType.SYNC) {
      // ATOMIC SYNC_WRITE LOCK ID unique for each thread.
      return new StringLockID(ATOMIC_LOCK_NAME + DELIMITER + platformService.getUUID() + DELIMITER
                              + Thread.currentThread().getId());
    } else {
      return new StringLockID(ATOMIC_LOCK_NAME);
    }
  }

  private LockLevel getLockLevel(ToolkitTransactionType type) {
    if (type == ToolkitTransactionType.SYNC) {
      return LockLevel.SYNCHRONOUS_WRITE;
    } else {
      return LockLevel.CONCURRENT;
    }
  }

}
