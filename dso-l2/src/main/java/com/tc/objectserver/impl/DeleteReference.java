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
package com.tc.objectserver.impl;

import com.tc.object.ObjectID;
import com.tc.objectserver.core.api.ManagedObject;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 *
 * @author mscott
 */
public class DeleteReference implements ManagedObjectReference {
  
  private final ObjectID id;
  private final AtomicBoolean marked = new AtomicBoolean(true);

  public DeleteReference(ObjectID id) {
    this.id = id;
  }
  
  @Override
  public ObjectID getObjectID() {
    return id;
  }

  @Override
  public void setRemoveOnRelease(boolean removeOnRelease) {

  }

  @Override
  public boolean isRemoveOnRelease() {
    return true;
  }

  @Override
  public boolean markReference() {
    return false;
  }

  @Override
  public boolean unmarkReference() {
    return marked.compareAndSet(true, false);
  }

  @Override
  public boolean isReferenced() {
    return true;
  }

  @Override
  public boolean isNew() {
    return false;
  }

  @Override
  public ManagedObject getObject() {
    return null;
  }
  
}
