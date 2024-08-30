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

import com.tc.net.ClientID;
import com.tc.object.locks.ClientServerExchangeLockContext;
import com.tc.object.locks.LockID;
import com.tc.object.locks.StringLockID;
import com.tc.object.locks.ThreadID;
import com.tc.object.locks.ServerLockContext.State;
import com.tc.objectserver.locks.NotifiedWaiters;

import java.util.HashSet;
import java.util.Set;

import junit.framework.TestCase;

public class NotifiedWaitersTest extends TestCase {

  public void testBasics() throws Exception {
    ClientID clientID1 = new ClientID(1);
    ClientID clientID2 = new ClientID(2);

    Set forChannel1 = new HashSet();
    Set forChannel2 = new HashSet();

    LockID lockID = new StringLockID("me");
    ThreadID txID1 = new ThreadID(1);
    ThreadID txID2 = new ThreadID(2);
    ThreadID txID3 = new ThreadID(3);

    NotifiedWaiters ns = new NotifiedWaiters();

    ClientServerExchangeLockContext lr1 = new ClientServerExchangeLockContext(lockID, clientID1, txID1, State.WAITER);
    forChannel1.add(lr1);
    ns.addNotification(lr1);

    ClientServerExchangeLockContext lr2 = new ClientServerExchangeLockContext(lockID, clientID1, txID2, State.WAITER);
    forChannel1.add(lr2);
    ns.addNotification(lr2);

    ClientServerExchangeLockContext lr3 = new ClientServerExchangeLockContext(lockID, clientID2, txID3, State.WAITER);
    forChannel2.add(lr3);
    ns.addNotification(lr3);

    assertEquals(forChannel1, ns.getNotifiedFor(clientID1));
    assertEquals(forChannel2, ns.getNotifiedFor(clientID2));

    ns = new NotifiedWaiters();
    assertTrue(ns.isEmpty());
    ns.getNotifiedFor(new ClientID(1));
    assertTrue(ns.isEmpty());
  }

}
