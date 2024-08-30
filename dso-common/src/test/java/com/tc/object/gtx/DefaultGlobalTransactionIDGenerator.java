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
package com.tc.object.gtx;

import com.tc.object.tx.ServerTransactionID;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

public class DefaultGlobalTransactionIDGenerator implements GlobalTransactionIDGenerator {

  SortedSet gidSet  = new TreeSet();
  Map       sid2Gid = new HashMap();
  long      id      = 0;

  @Override
  public GlobalTransactionID getOrCreateGlobalTransactionID(ServerTransactionID serverTransactionID) {

    GlobalTransactionID gid = (GlobalTransactionID) sid2Gid.get(serverTransactionID);
    if (gid == null) {
      gid = new GlobalTransactionID(id++);
      sid2Gid.put(serverTransactionID, gid);
      gidSet.add(gid);
    }
    return gid;
  }

  @Override
  public GlobalTransactionID getLowGlobalTransactionIDWatermark() {
    if (gidSet.isEmpty()) {
      return GlobalTransactionID.NULL_ID;
    } else {
      GlobalTransactionID lowWaterMark = (GlobalTransactionID) gidSet.first();
      return lowWaterMark;
    }
  }
}
