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
package com.terracotta.toolkit.collections.map;

import org.terracotta.toolkit.ToolkitObjectType;
import org.terracotta.toolkit.rejoin.RejoinException;

import com.terracotta.toolkit.collections.StatusAwareIterator;
import com.terracotta.toolkit.util.ToolkitObjectStatus;

import java.util.Collection;
import java.util.Iterator;

public class SubTypeWrapperCollection<E> implements Collection<E> {
  private final Collection<E>         collection;

  protected final ToolkitObjectStatus status;
  private final int                   rejoinCount;
  protected final String              superTypeName;
  protected final ToolkitObjectType   toolkitObjectType;

  public SubTypeWrapperCollection(Collection<E> collection, ToolkitObjectStatus status, String superTypeName,
                                  ToolkitObjectType toolkitObjectType) {
    this.collection = collection;
    this.status = status;
    this.rejoinCount = status.getCurrentRejoinCount();
    this.superTypeName = superTypeName;
    this.toolkitObjectType = toolkitObjectType;
  }

  protected void assertStatus() {
    if (status.isDestroyed()) { throw new IllegalStateException(
                                                                "The object "
                                                                    + this.superTypeName
                                                                    + " of type "
                                                                    + this.toolkitObjectType
                                                                    + "  has already been destroyed, all SubTypes associated with are unusable "); }
    if (this.rejoinCount != status.getCurrentRejoinCount()) { throw new RejoinException(
                                                                                        "The SubTypes associated with "
                                                                                            + this.superTypeName
                                                                                            + " of type "
                                                                                            + this.toolkitObjectType
                                                                                            + " are not usable anymore afer rejoin!"); }
  }

  @Override
  public boolean add(E e) {
    assertStatus();
    return collection.add(e);
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    assertStatus();
    return collection.addAll(c);
  }

  @Override
  public void clear() {
    assertStatus();
    collection.clear();
  }

  @Override
  public boolean contains(Object o) {
    assertStatus();
    return collection.contains(o);
  }

  @Override
  public boolean containsAll(Collection c) {
    assertStatus();
    return collection.containsAll(c);
  }

  @Override
  public boolean isEmpty() {
    assertStatus();
    return collection.isEmpty();
  }

  @Override
  public Iterator iterator() {
    assertStatus();
    return new StatusAwareIterator(collection.iterator(), status);
  }

  @Override
  public boolean remove(Object o) {
    assertStatus();
    return collection.remove(o);
  }

  @Override
  public boolean removeAll(Collection c) {
    assertStatus();
    return collection.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection c) {
    assertStatus();
    return collection.retainAll(c);
  }

  @Override
  public int size() {
    assertStatus();
    return collection.size();
  }

  @Override
  public Object[] toArray() {
    assertStatus();
    return collection.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    assertStatus();
    return collection.toArray(a);
  }

  @Override
  public String toString() {
    assertStatus();

    return toolkitObjectType + " : " + collection.toString();
  }

}
