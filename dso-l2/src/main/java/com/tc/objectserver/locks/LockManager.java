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

import com.tc.net.ClientID;
import com.tc.object.locks.ClientServerExchangeLockContext;
import com.tc.object.locks.LockID;
import com.tc.object.locks.ServerLockLevel;
import com.tc.object.locks.ThreadID;
import com.tc.objectserver.locks.ServerLock.NotifyAction;

import java.util.Collection;

/**
 * The main server side LockManager interface
 */
public interface LockManager {

  /**
   * Called by the stage thread for acquiring lock.
   * 
   * @param lid - Id of the lock.
   * @param cid - Id of the client requesting
   * @param tid - Id of the thread requesting
   * @param level - read or write
   */
  void lock(LockID lid, ClientID cid, ThreadID tid, ServerLockLevel level);

  /**
   * Called by the stage thread for trying to acquire the lock.
   * 
   * @param lid - Id of the lock.
   * @param cid - Id of the client requesting
   * @param tid - Id of the thread requesting
   * @param level - read or write
   * @param timeout - in millis. The time for which the server should wait before refusing.
   */
  void tryLock(LockID lid, ClientID cid, ThreadID tid, ServerLockLevel level, long timeout);

  /**
   * Called by the stage thread for releasing the lock.
   * 
   * @param lid - Id of the lock.
   * @param cid - Id of the client requesting
   * @param tid - Id of the thread requesting
   */
  void unlock(LockID lid, ClientID cid, ThreadID tid);

  /**
   * Called by the stage thread for getting the global state of the lock.
   * 
   * @param lid - Id of the lock.
   * @param cid - Id of the client requesting
   * @param tid - Id of the thread requesting
   */
  void queryLock(LockID lid, ClientID cid, ThreadID tid);

  /**
   * Called by the stage thread to interrupt the particular waiter waiting on the specified lock id.
   * 
   * @param lid - Id of the lock.
   * @param cid - Id of the client requesting
   * @param tid - Id of the thread requesting
   */
  void interrupt(LockID lid, ClientID cid, ThreadID tid);

  /**
   * Called by the stage thread and called when a reply comes from the client after being asked to give up the greedy
   * lock.
   * 
   * @param lid - Id of the lock.
   * @param cid - Id of the client requesting
   * @param serverLockContexts - Contexts that were present on the client side
   */
  void recallCommit(LockID lid, ClientID cid, Collection<ClientServerExchangeLockContext> serverLockContexts);

  /**
   * Called by the stage thread to notify threads waiting on this lock.
   * 
   * @param lid - Id of the lock.
   * @param cid - Id of the client requesting
   * @param tid - Id of the thread requesting
   * @param action - all or one (notifyAll or just notify)
   * @param addNotifiedWaitersTo - list to add the waiters which were notified due to this call
   */
  NotifiedWaiters notify(LockID lid, ClientID cid, ThreadID tid, NotifyAction action,
                         NotifiedWaiters addNotifiedWaitersTo);

  /**
   * Called by the stage thread to wait on this lock.
   * 
   * @param lid - Id of the lock.
   * @param cid - Id of the client requesting
   * @param tid - Id of the thread requesting
   * @param timeout - in millis. The time for which the server should wait before refusing.
   */
  void wait(LockID lid, ClientID cid, ThreadID tid, long timeout);

  /**
   * This method is called during handshake when the client informs the server of its locks. This method will only be
   * called with holders context and waiters.
   * 
   * @param cid - Id of the client requesting
   * @param serverLockContexts
   */
  void reestablishState(ClientID cid, Collection<ClientServerExchangeLockContext> serverLockContexts);

  /**
   * On client disconnect this method will be called. This will clear all the state being stored for this particular
   * client.
   * 
   * @param cid - Id of the client requesting
   */
  void clearAllLocksFor(ClientID cid);

  /**
   * This method will be called once the handshake process for the client gets completed. This will start the wait
   * timers and start processing of the pending requests.
   */
  void start();
}
