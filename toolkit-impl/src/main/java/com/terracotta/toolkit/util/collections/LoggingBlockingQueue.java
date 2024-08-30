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

import com.tc.logging.TCLogger;

import java.util.Collection;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.lang.String.format;

public class LoggingBlockingQueue<E> implements BlockingQueue<E> {

  private final BlockingQueue<E> delegate;
  private final TCLogger logger;
  private final String message;
  private final int granularity;
  private final AtomicInteger thresholdCentroid;

  public LoggingBlockingQueue(BlockingQueue<E> delegate, int granularity, TCLogger logger, String message) {
    this.delegate = delegate;
    this.logger = logger;
    this.granularity = granularity;
    this.message = message;
    int size = delegate.size();
    this.thresholdCentroid = new AtomicInteger(size - (size % granularity));
  }

  private void grow() {
    while (true) {
      int centroid = thresholdCentroid.get();
      int size = delegate.size();
      if (size >= (centroid + granularity)) {
        int nextCentroid = size - (size % granularity);

        boolean advanced = thresholdCentroid.compareAndSet(centroid, nextCentroid);
        if (advanced) {
          logger.info(format(message, size));
        }
      } else {
        break;
      }
    }
  }

  private void shrink() {
    while (true) {
      int centroid = thresholdCentroid.get();
      int size = delegate.size();
      if (size <= (centroid - granularity)) {
        int nextCentroid = size - (size % granularity);

        boolean advanced = thresholdCentroid.compareAndSet(centroid, nextCentroid);
        if (advanced) {
          logger.info(format(message, size));
        }
      } else {
        break;
      }
    }
  }

  @Override
  public boolean add(E e) {
    if (delegate.add(e)) {
      grow();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean offer(E e) {
    if (delegate.offer(e)) {
      grow();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public void put(E e) throws InterruptedException {
    delegate.put(e);
    grow();
  }

  @Override
  public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
    if (delegate.offer(e, timeout, unit)) {
      grow();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public E take() throws InterruptedException {
    E taken = delegate.take();
    shrink();
    return taken;
  }

  @Override
  public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    E removed = delegate.poll(timeout, unit);
    if (removed != null) {
      shrink();
    }
    return removed;
  }

  @Override
  public int remainingCapacity() {
    return delegate.remainingCapacity();
  }

  @Override
  public boolean remove(Object o) {
    if (delegate.remove(o)) {
      shrink();
      return true;
    } else {
      return false;
    }
  }

  @Override
  public boolean contains(Object o) {
    return delegate.contains(o);
  }

  @Override
  public int drainTo(Collection<? super E> c) {
    int drained = delegate.drainTo(c);
    if (drained > 0) {
      shrink();
    }
    return drained;
  }

  @Override
  public int drainTo(Collection<? super E> c, int maxElements) {
    int drained = delegate.drainTo(c, maxElements);
    if (drained > 0) {
      shrink();
    }
    return drained;
  }

  @Override
  public E remove() {
    E removed = delegate.remove();
    shrink();
    return removed;
  }

  @Override
  public E poll() {
    E removed = delegate.poll();
    if (removed != null) {
      shrink();
    }
    return removed;
  }

  @Override
  public E element() {
    return delegate.element();
  }

  @Override
  public E peek() {
    return delegate.peek();
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public boolean isEmpty() {
    return delegate.isEmpty();
  }

  @Override
  public Iterator<E> iterator() {
    return delegate.iterator();
  }

  @Override
  public Object[] toArray() {
    return delegate.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return delegate.toArray(a);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    return delegate.containsAll(c);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    boolean modified = delegate.addAll(c);
    if (modified) {
      grow();
    }
    return modified;
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    boolean modified = delegate.removeAll(c);
    if (modified) {
      shrink();
    }
    return modified;
  }

  @Override
  public boolean removeIf(Predicate<? super E> filter) {
    boolean modified = delegate.removeIf(filter);
    if (modified) {
      shrink();
    }
    return modified;
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    boolean modified = delegate.retainAll(c);
    if (modified) {
      shrink();
    }
    return modified;
  }

  @Override
  public void clear() {
    delegate.clear();
    shrink();
  }

  @Override
  public boolean equals(Object o) {
    if (o instanceof LoggingBlockingQueue) {
      return delegate.equals(((LoggingBlockingQueue<?>) o).delegate);
    } else {
      return delegate.equals(o);
    }
  }

  @Override
  public int hashCode() {
    return delegate.hashCode();
  }

  @Override
  public Spliterator<E> spliterator() {
    return delegate.spliterator();
  }

  @Override
  public Stream<E> stream() {
    return delegate.stream();
  }

  @Override
  public Stream<E> parallelStream() {
    return delegate.parallelStream();
  }

  @Override
  public void forEach(Consumer<? super E> action) {
    delegate.forEach(action);
  }
}
