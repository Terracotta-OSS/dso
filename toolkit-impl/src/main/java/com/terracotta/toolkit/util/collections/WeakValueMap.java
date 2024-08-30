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

import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

public class WeakValueMap<V> {
  private final ReferenceQueue                     referenceQueue = new ReferenceQueue();
  private final Map<String, NamedWeakReference<V>> internalMap    = new HashMap<String, NamedWeakReference<V>>();

  WeakValueMap() {
    // make constructor package protected
  }

  public synchronized Set<String> keySet() {
    return Collections.unmodifiableSet(internalMap.keySet());
  }

  public synchronized V get(String name) {
    cleanupReferenceQueue();

    NamedWeakReference<V> weakReference = internalMap.get(name);
    if (weakReference == null) { return null; }

    return weakReference.get();
  }

  public synchronized Collection<V> values() {
    cleanupReferenceQueue();
    Set<V> currentValues = new HashSet<V>();
    for (NamedWeakReference<V> weakReference : internalMap.values()) {
      currentValues.add(weakReference.get());
    }
    return currentValues;
  }

  public synchronized V put(String name, V value) {
    cleanupReferenceQueue();

    Callable<Void> onGcCallable = null;
    if (value instanceof OnGCCallable) {
      onGcCallable = ((OnGCCallable) value).onGCCallable();
    }

    NamedWeakReference<V> reference = new NamedWeakReference(name, value, referenceQueue, onGcCallable);
    NamedWeakReference<V> oldReference = internalMap.put(name, reference);
    return oldReference == null ? null : oldReference.get();
  }

  public synchronized V remove(String name) {
    cleanupReferenceQueue();

    NamedWeakReference<V> oldReference = internalMap.remove(name);
    return oldReference == null ? null : oldReference.get();
  }

  void cleanupReferenceQueue() {
    while (true) {
      Object gcdObject = referenceQueue.poll();
      if (gcdObject == null) { return; }

      NamedWeakReference<V> weakReference = (NamedWeakReference) gcdObject;
      Callable<Void> onGCCallable = weakReference.getOnGcCallback();
      if (onGCCallable != null) {
        try {
          onGCCallable.call();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      internalMap.remove(weakReference.getName());
    }
  }

  private static class NamedWeakReference<V> extends WeakReference<V> {
    private final String         name;
    private final Callable<Void> onGcCallback;

    public NamedWeakReference(String name, V reference, ReferenceQueue referenceQueue, Callable<Void> onGcCallback) {
      super(reference, referenceQueue);
      this.name = name;
      this.onGcCallback = onGcCallback;
    }

    public String getName() {
      return name;
    }

    public Callable<Void> getOnGcCallback() {
      return onGcCallback;
    }
  }
}
