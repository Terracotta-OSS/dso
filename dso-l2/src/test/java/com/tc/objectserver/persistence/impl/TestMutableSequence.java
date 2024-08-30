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
package com.tc.objectserver.persistence.impl;

import com.tc.exception.ImplementMe;
import com.tc.util.Assert;
import com.tc.util.concurrent.NoExceptionLinkedQueue;
import com.tc.util.sequence.MutableSequence;

public class TestMutableSequence implements MutableSequence {

  public long                         sequence       = 0;
  public final NoExceptionLinkedQueue nextBatchQueue = new NoExceptionLinkedQueue();

  @Override
  public long next() {
    return ++sequence;
  }

  @Override
  public long current() {
    return sequence;
  }

  @Override
  public long nextBatch(long batchSize) {
    nextBatchQueue.put(new Object[] { Integer.valueOf((int) batchSize) });
    long ls = sequence;
    sequence += batchSize;
    return ls;
  }

  @Override
  public String getUID() {
    throw new ImplementMe();
  }

  @Override
  public void setNext(long next) {
    Assert.assertTrue(this.sequence <= next);
    sequence = next;
  }

}
