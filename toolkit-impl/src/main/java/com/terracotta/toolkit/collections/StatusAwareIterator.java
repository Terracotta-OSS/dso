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
package com.terracotta.toolkit.collections;

import org.terracotta.toolkit.rejoin.RejoinException;

import com.terracotta.toolkit.util.ToolkitObjectStatus;

import java.util.Iterator;

public class StatusAwareIterator<E> implements Iterator<E> {

  private final Iterator<E>         iterator;
  private final ToolkitObjectStatus status;
  private final int                 currentRejoinCount;

  public StatusAwareIterator(Iterator<E> iterator, ToolkitObjectStatus status) {
    this.iterator = iterator;
    this.status = status;
    this.currentRejoinCount = this.status.getCurrentRejoinCount();
  }

  private void assertStatus() {
    if (status.isDestroyed()) { throw new IllegalStateException(
                                                                "Can not perform operation because object has been destroyed"); }
    if (this.currentRejoinCount != status.getCurrentRejoinCount()) { throw new RejoinException(
                                                                                               "Can not performe operation because rejoin happened."); }
  }

  @Override
  public boolean hasNext() {
    assertStatus();
    return iterator.hasNext();
  }

  @Override
  public E next() {
    assertStatus();
    return iterator.next();
  }

  @Override
  public void remove() {
    assertStatus();
    iterator.remove();
  }

}
