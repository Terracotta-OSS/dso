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
package com.tc.object.idprovider.impl;

import com.tc.net.GroupID;
import com.tc.object.ObjectID;
import com.tc.object.idprovider.api.ObjectIDProvider;
import com.tc.object.tx.ClientTransaction;
import com.tc.util.sequence.Sequence;

import java.util.SortedSet;
import java.util.TreeSet;

public class ObjectIDProviderImpl implements ObjectIDProvider {

  private final Sequence        sequence;
  private final SortedSet<Long> cachedObjectIds = new TreeSet<Long>();

  public ObjectIDProviderImpl(Sequence sequence) {
    this.sequence = sequence;
  }

  @Override
  public synchronized ObjectID next(ClientTransaction txn, Object pojo, GroupID gid) {
    long oidLong = -1;
    if (cachedObjectIds.size() > 0) {
      oidLong = this.cachedObjectIds.first();
      this.cachedObjectIds.remove(oidLong);
    } else {
      oidLong = this.sequence.next();
    }

    return new ObjectID(oidLong);
  }

  @Override
  public synchronized void reserve(int size, GroupID gid) {
    int sizeNeeded = size - cachedObjectIds.size();
    for (int i = 0; i < sizeNeeded; i++) {
      cachedObjectIds.add(this.sequence.next());
    }
  }
}