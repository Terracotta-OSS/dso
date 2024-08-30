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
package com.tc.object;

import com.tc.abortable.AbortableOperationManager;
import com.tc.abortable.AbortedOperationException;
import com.tc.net.GroupID;
import com.tc.object.tx.RemoteTransactionManager;
import com.tc.util.AbortedOperationUtil;

/**
 * @author tim
 */
public class RemoteResourceManagerImpl implements RemoteResourceManager {
  private static final int                MAX_THROTTLE_MS          = 2000; // TODO: Make this configurable?

  private final AbortableOperationManager abortableOperationManager;
  private final RemoteTransactionManager  remoteTransactionManager;
  private volatile boolean                throttleStateInitialized = false;
  private volatile boolean                throwException           = false;
  private volatile long                   throttleTime             = 0;

  public RemoteResourceManagerImpl(RemoteTransactionManager mgr, AbortableOperationManager abortableOperationManager) {
    this.abortableOperationManager = abortableOperationManager;
    this.remoteTransactionManager = mgr;
  }

  @Override
  public synchronized void handleThrottleMessage(final GroupID groupID, final boolean exception, final float throttle) {
    throwException = exception;
    throttleTime = (long) (throttle * MAX_THROTTLE_MS);
    if ( throttleTime > 0 ) {
      this.remoteTransactionManager.throttleProcessing(true);
    } else {
      this.remoteTransactionManager.throttleProcessing(false);
    }
    throttleStateInitialized = true;
    notifyAll();
  }

  @Override
  public void throttleIfMutationIfNecessary(final ObjectID parentObject) throws AbortedOperationException {
    if (!throttleStateInitialized) {
      synchronized (this) {
        boolean interrupted = false;
        while (!throttleStateInitialized) {
          try {
            wait();
          } catch (InterruptedException e) {
            AbortedOperationUtil.throwExceptionIfAborted(abortableOperationManager);
            interrupted = true;
          }
        }
        if (interrupted) {
          Thread.currentThread().interrupt();
        }
      }
    }

    if (throwException) {
      throw new OutOfResourceException("Server is full.");
    } else if (throttleTime > 0) {
      try {
        Thread.sleep(throttleTime);
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    }
  }
}
