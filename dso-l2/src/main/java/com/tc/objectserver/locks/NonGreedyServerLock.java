/* 
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at 
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is 
 *      Terracotta, Inc., a Software AG company
 */
package com.tc.objectserver.locks;

import com.tc.object.locks.LockID;
import com.tc.object.locks.ServerLockContext;

import java.util.List;

public final class NonGreedyServerLock extends AbstractServerLock {
  public NonGreedyServerLock(LockID lockID) {
    super(lockID);
  }

  @Override
  protected void processPendingRequests(LockHelper helper) {
    ServerLockContext request = getNextRequestIfCanAward(helper);
    if (request == null) { return; }

    switch (request.getState().getLockLevel()) {
      case READ:
        add(request, helper);
        awardAllReads(helper, request);
        break;
      case WRITE:
        awardLock(helper, request);
        break;
    }
  }

  private void awardAllReads(LockHelper helper, ServerLockContext request) {
    List<ServerLockContext> contexts = removeAllPendingReadRequests(helper);

    for (ServerLockContext context : contexts) {
      awardLock(helper, context);
    }
  }
}
