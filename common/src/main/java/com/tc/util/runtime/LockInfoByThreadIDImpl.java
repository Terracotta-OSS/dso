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
import java.util.LinkedHashMap;
import java.util.Map;

public class LockInfoByThreadIDImpl implements LockInfoByThreadID {

  Map heldLocks    = new LinkedHashMap();
  Map waitOnLocks  = new LinkedHashMap();
  Map pendingLocks = new LinkedHashMap();

  @Override
  public ArrayList getHeldLocks(ThreadID threadID) {
    return lockList((ArrayList) heldLocks.get(threadID));
  }

  @Override
  public ArrayList getWaitOnLocks(ThreadID threadID) {
    return lockList((ArrayList) waitOnLocks.get(threadID));
  }

  @Override
  public ArrayList getPendingLocks(ThreadID threadID) {
    return lockList((ArrayList) pendingLocks.get(threadID));
  }

  private ArrayList lockList(ArrayList lockList) {
    if (lockList == null) {
      return new ArrayList();
    } else {
      return lockList;
    }
  }

  @Override
  public void addLock(LockState lockState, ThreadID threadID, String value) {
    if (lockState == LockState.HOLDING) {
      addLockTo(heldLocks, threadID, value);
    } else if (lockState == LockState.WAITING_ON) {
      addLockTo(waitOnLocks, threadID, value);
    } else if (lockState == LockState.WAITING_TO) {
      addLockTo(pendingLocks, threadID, value);
    } else {
      throw new AssertionError("Unexpected Lock type : " + lockState);
    }
  }

  private void addLockTo(Map lockMap, ThreadID threadID, String value) {
    ArrayList lockArray = (ArrayList) lockMap.get(threadID);
    if (lockArray == null) {
      ArrayList al = new ArrayList();
      al.add(value);
      lockMap.put(threadID, al);
    } else {
      lockArray.add(value);
      lockMap.put(threadID, lockArray);
    }
  }
}
