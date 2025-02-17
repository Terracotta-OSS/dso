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
package com.tc.objectserver.locks;

import com.tc.object.locks.LockID;

import java.io.Serializable;
import java.util.Arrays;

public class LockMBeanImpl implements LockMBean, Serializable {
  private final LockID                  lockID;
  private final ServerLockContextBean[] contexts;

  public LockMBeanImpl(LockID lockID, ServerLockContextBean[] contexts) {
    this.lockID = lockID;
    this.contexts = contexts;
  }

  @Override
  public ServerLockContextBean[] getContexts() {
    return contexts;
  }

  @Override
  public LockID getLockID() {
    return lockID;
  }

  @Override
  public String toString() {
    return "LockMBeanImpl [contexts=" + Arrays.toString(contexts) + ", lockID=" + lockID + "]";
  }

}
