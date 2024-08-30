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
package com.terracotta.toolkit.util.collections;

import com.terracotta.toolkit.collections.map.InternalToolkitMap;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class AggregateMapIterator<T> implements Iterator<T> {

  protected final Iterator<? extends InternalToolkitMap> listIterator;
  protected Iterator<T>                   currentIterator;

  public AggregateMapIterator(Iterator<? extends InternalToolkitMap> listIterator, Iterator additionalValues) {
    this.listIterator = listIterator;
    this.currentIterator = additionalValues;
    while (this.listIterator.hasNext()) {
      if (this.currentIterator.hasNext()) { return; }
      this.currentIterator = getNextIterator();
    }
  }

  private Iterator<T> getNextIterator() {
    return getClusterMapIterator(listIterator.next());
  }

  public abstract Iterator<T> getClusterMapIterator(InternalToolkitMap map);

  @Override
  public boolean hasNext() {

    if (this.currentIterator == null) { return false; }
    boolean hasNext = false;

    if (this.currentIterator.hasNext()) {
      hasNext = true;
    } else {
      while (this.listIterator.hasNext()) {
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
      while (this.listIterator.hasNext()) {
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
