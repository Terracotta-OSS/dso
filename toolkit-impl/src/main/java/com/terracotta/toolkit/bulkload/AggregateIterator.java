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
package com.terracotta.toolkit.bulkload;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * AggregateIterator is a iterator. It is use to iterate over set of iterator. Example BulkLoadCache iterator should
 * aggregate with LocalBufferMap Iterator.
 */
public class AggregateIterator<T> implements Iterator<T> {

  protected final Iterator<Iterator<T>> iterators;
  protected Iterator<T>                 currentIterator;

  private Iterator<T> getNextIterator() {
    return iterators.next();
  }

  public AggregateIterator(Collection<Iterator<T>> iterators) {
    this.iterators = iterators.iterator();
    while (this.iterators.hasNext()) {
      this.currentIterator = getNextIterator();
      if (this.currentIterator.hasNext()) { return; }
    }
  }

  @Override
  public boolean hasNext() {

    if (this.currentIterator == null) { return false; }
    boolean hasNext = false;

    if (this.currentIterator.hasNext()) {
      hasNext = true;
    } else {
      while (this.iterators.hasNext()) {
        this.currentIterator = getNextIterator();
        if (this.currentIterator.hasNext()) { return true; }
      }
    }

    return hasNext;
  }

  @Override
  public T next() {

    if (this.currentIterator == null) { throw new NoSuchElementException(); }

    if (this.currentIterator.hasNext()) {
      return this.currentIterator.next();

    } else {
      while (this.iterators.hasNext()) {
        this.currentIterator = getNextIterator();

        if (this.currentIterator.hasNext()) { return this.currentIterator.next(); }
      }
    }

    throw new NoSuchElementException();
  }

  @Override
  public void remove() {
    this.currentIterator.remove();
  }

}
