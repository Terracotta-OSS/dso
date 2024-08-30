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
import org.terracotta.toolkit.nonstop.NonStopException;

import com.tc.abortable.AbortedOperationException;
import com.tc.object.locks.LockID;
import com.tc.object.locks.LockLevel;
import com.tc.platform.PlatformService;

public class ToolkitTransactionImpl implements ToolkitTransaction {
  private final PlatformService platformService;
  private final LockID          lockID;
  private final LockLevel       level;

  public ToolkitTransactionImpl(PlatformService platformService, LockID lockID, LockLevel level) {
    this.platformService = platformService;
    this.lockID = lockID;
    this.level = level;
  }

  @Override
  public void commit() {
    try {
      platformService.commitAtomicTransaction(lockID, level);
    } catch (AbortedOperationException e) {
      throw new NonStopException("commit timed out", e);
    }
  }

}
