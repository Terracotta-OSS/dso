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
package com.tc.object.locks;

import com.tc.net.NodeID;
import com.tc.object.handshakemanager.ClientHandshakeCallback;
import com.tc.object.session.SessionID;
import com.tc.text.PrettyPrintable;

import java.util.Collection;

public interface ClientLockManager extends TerracottaLocking, ClientHandshakeCallback, PrettyPrintable {
  /**
   * Called by a Terracotta thread to notify the given thread waiting on the lock.
   */
  public void notified(LockID lock, ThreadID thread);

  /**
   * Called by a Terracotta thread to request the return of a greedy lock previously awarded to the client.
   */
  public void recall(NodeID from, SessionID session, LockID lock, ServerLockLevel level, int lease);

  /**
   * Called by a Terracotta thread to request the return of a greedy lock previously awarded to the client.
   */
  public void recall(NodeID from, SessionID session, LockID lock, ServerLockLevel level, int lease, boolean batch);

  /**
   * Called by a Terracotta thread to award a per-thread or greedy lock to the client.
   */
  public void award(NodeID from, SessionID session, LockID lock, ThreadID thread, ServerLockLevel level);

  /**
   * Called by a Terracotta thread to indicate that the specified non-blocking try lock attempt has failed at the
   * server.
   */
  public void refuse(NodeID from, SessionID session, LockID lock, ThreadID thread, ServerLockLevel level);

  /**
   * Called by a Terracotta thread to return the result of a previous query operation on the RemoteLockManager.
   */
  public void info(LockID lock, ThreadID requestor, Collection<ClientServerExchangeLockContext> contexts);

  /**
   * Returns a complete dump (in pseudo-portable format) of the state of all locks.
   */
  public Collection<ClientServerExchangeLockContext> getAllLockContexts();

  boolean isLockAwardValid(LockID lock, long awardID);

  public long getAwardIDFor(final LockID lock);

}
