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
package com.tc.util.sequence;

import com.tc.util.SequencePublisher;

import java.util.concurrent.CopyOnWriteArrayList;

public class DGCSequenceProvider implements SequencePublisher {

  private final MutableSequence                      dgcSequence;
  private final CopyOnWriteArrayList<DGCIdPublisher> dgcIdListeners = new CopyOnWriteArrayList<DGCIdPublisher>();

  public DGCSequenceProvider(MutableSequence dgcSequence) {
    this.dgcSequence = dgcSequence;
  }

  public long currentIDValue() {
    return this.dgcSequence.current();
  }

  public void setNextAvailableDGCId(long nextId) {
    this.dgcSequence.setNext(nextId);
  }

  public long getNextId() {
    long nexId = this.dgcSequence.next();
    publishNextId(nexId + 1);
    return nexId;
  }

  private void publishNextId(long nexId) {
    for (DGCIdPublisher dgcIdPublisher : dgcIdListeners) {
      dgcIdPublisher.publishNextAvailableDGCID(nexId);
    }
  }

  @Override
  public void registerSequecePublisher(DGCIdPublisher dgcIdPublisher) {
    this.dgcIdListeners.add(dgcIdPublisher);
  }

}
