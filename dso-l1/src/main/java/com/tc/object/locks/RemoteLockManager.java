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

import com.tc.abortable.AbortedOperationException;
import com.tc.net.ClientID;
import com.tc.object.ClearableCallback;

import java.util.Collection;

public interface RemoteLockManager extends ClearableCallback {
  public ClientID getClientID();

  public void lock(LockID lock, ThreadID thread, ServerLockLevel level);

  public void tryLock(LockID lock, ThreadID thread, ServerLockLevel level, long timeout);

  public void unlock(LockID lock, ThreadID thread, ServerLockLevel level);

  public void wait(LockID lock, ThreadID thread, long waitTime);

  public void interrupt(LockID lock, ThreadID thread);

  public void recallCommit(LockID lock, Collection<ClientServerExchangeLockContext> lockState, boolean batch);

  public void flush(LockID lock) throws AbortedOperationException;

  public boolean asyncFlush(LockID lock, LockFlushCallback callback);

  public void query(LockID lock, ThreadID thread);

  public void waitForServerToReceiveTxnsForThisLock(LockID lock) throws AbortedOperationException;

  public void shutdown();

  public boolean isShutdown();

}
