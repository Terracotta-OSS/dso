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
package com.tc.objectserver.locks;

import com.tc.net.NodeID;
import com.tc.object.locks.ClientServerExchangeLockContext;
import com.tc.object.locks.LockID;
import com.tc.object.locks.ServerLockLevel;
import com.tc.object.locks.ThreadID;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;

import java.util.Collection;

public class LockResponseContextFactory {
  private final static int     LOCK_LEASE_TIME   = TCPropertiesImpl
                                                     .getProperties()
                                                     .getInt(
                                                             TCPropertiesConsts.L2_LOCKMANAGER_GREEDY_LEASE_LEASETIME_INMILLS);
  private final static boolean LOCK_LEASE_ENABLE = TCPropertiesImpl
                                                     .getProperties()
                                                     .getBoolean(TCPropertiesConsts.L2_LOCKMANAGER_GREEDY_LEASE_ENABLED);

  public static LockResponseContext createLockRejectedResponseContext(final LockID lockID, final NodeID nodeID,
                                                                      final ThreadID threadID,
                                                                      final ServerLockLevel level) {
    return new LockResponseContext(lockID, nodeID, threadID, level, LockResponseContext.LOCK_NOT_AWARDED);
  }

  public static LockResponseContext createLockAwardResponseContext(final LockID lockID, final NodeID nodeID,
                                                                   final ThreadID threadID, final ServerLockLevel level) {
    LockResponseContext lrc = new LockResponseContext(lockID, nodeID, threadID, level, LockResponseContext.LOCK_AWARD);
    return lrc;
  }

  public static LockResponseContext createLockRecallResponseContext(final LockID lockID, final NodeID nodeID,
                                                                    final ThreadID threadID, final ServerLockLevel level) {
    if (LOCK_LEASE_ENABLE) {
      return new LockResponseContext(lockID, nodeID, threadID, level, LockResponseContext.LOCK_RECALL, LOCK_LEASE_TIME);
    } else {
      return new LockResponseContext(lockID, nodeID, threadID, level, LockResponseContext.LOCK_RECALL);
    }
  }

  public static LockResponseContext createLockWaitTimeoutResponseContext(final LockID lockID, final NodeID nodeID,
                                                                         final ThreadID threadID,
                                                                         final ServerLockLevel level) {
    return new LockResponseContext(lockID, nodeID, threadID, level, LockResponseContext.LOCK_WAIT_TIMEOUT);
  }

  public static LockResponseContext createLockQueriedResponseContext(
                                                                     final LockID lockID,
                                                                     final NodeID nodeID,
                                                                     final ThreadID threadID,
                                                                     final ServerLockLevel level,
                                                                     Collection<ClientServerExchangeLockContext> contexts,
                                                                     int numberOfPendingRequests) {
    return new LockResponseContext(lockID, nodeID, threadID, level, contexts, numberOfPendingRequests,
                                   LockResponseContext.LOCK_INFO);
  }
}
