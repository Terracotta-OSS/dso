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
package com.tc.objectserver.managedobject;

import com.google.common.eventbus.EventBus;
import com.tc.objectserver.core.api.ManagedObjectState;

/**
 * Base class for implementations of ManagedObjectState implementations.
 */
public abstract class AbstractManagedObjectState implements ManagedObjectState {

  public final EventBus getOperationEventBus() {
    return getStateFactory().getOperationEventBus();
  }

  public final ManagedObjectChangeListener getListener() {
    return getStateFactory().getListener();
  }

  public final ManagedObjectStateFactory getStateFactory() {
    return ManagedObjectStateFactory.getInstance();
  }

  /**
   * This is only for testing, its highly inefficient
   */
  @Override
  public final boolean equals(Object o) {
    if (o == null) return false;
    if (this == o) return true;
    if (getClass().getName().equals(o.getClass().getName())) { return basicEquals((AbstractManagedObjectState) o); }
    return false;
  }

  protected abstract boolean basicEquals(AbstractManagedObjectState o);

  @Override
  public int hashCode() {
    return super.hashCode();
  }

}
