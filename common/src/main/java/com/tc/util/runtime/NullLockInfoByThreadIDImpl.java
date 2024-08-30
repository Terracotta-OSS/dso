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
package com.tc.util.runtime;

import com.tc.object.locks.ThreadID;

import java.util.ArrayList;

public class NullLockInfoByThreadIDImpl implements LockInfoByThreadID {

  @Override
  public ArrayList getHeldLocks(ThreadID threadID) {
    return new ArrayList();
  }

  @Override
  public ArrayList getPendingLocks(ThreadID threadID) {
    return new ArrayList();
  }

  @Override
  public ArrayList getWaitOnLocks(ThreadID threadID) {
    return new ArrayList();
  }

  @Override
  public void addLock(LockState lockState, ThreadID threadID, String lockID) {
    //
  }
}
