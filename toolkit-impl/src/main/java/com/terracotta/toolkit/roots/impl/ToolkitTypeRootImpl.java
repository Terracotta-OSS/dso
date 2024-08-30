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
package com.terracotta.toolkit.roots.impl;

import com.tc.abortable.AbortedOperationException;
import com.tc.net.GroupID;
import com.tc.object.LogicalOperation;
import com.tc.object.ObjectID;
import com.tc.object.TCObject;
import com.tc.object.bytecode.Manageable;
import com.tc.platform.PlatformService;
import com.terracotta.toolkit.abortable.ToolkitAbortableOperationException;
import com.terracotta.toolkit.object.TCToolkitObject;
import com.terracotta.toolkit.roots.ToolkitTypeRoot;

import java.util.HashMap;
import java.util.Map;

public class ToolkitTypeRootImpl<T extends TCToolkitObject> implements ToolkitTypeRoot<T>, Manageable {
  private transient volatile TCObject           tcManaged;
  private transient final Map<String, ObjectID> localCache = new HashMap<String, ObjectID>();
  private transient volatile GroupID            gid;
  private transient volatile Object             localResolveLock;

  private final PlatformService                 platformService;

  public ToolkitTypeRootImpl(PlatformService platformService) {
    this.platformService = platformService;
  }

  @Override
  public void addClusteredObject(String name, T manageable) {
    synchronized (localResolveLock) {
      platformService.lookupOrCreate(manageable, gid);
      // TODO: write a test
      localCache.put(name, manageable.__tc_managed().getObjectID());
      logicalInvokePut(name, manageable);
    }
  }

  public void applyAdd(String key, ObjectID o) {
    synchronized (localResolveLock) {
      localCache.put(key, o);
    }
  }

  public void applyRemove(String o) {
    synchronized (localResolveLock) {
      localCache.remove(o);
    }
  }

  private void logicalInvokePut(String name, T manageable) {
    tcManaged.logicalInvoke(LogicalOperation.PUT, new Object[] { name, manageable });
  }

  @Override
  public T getClusteredObject(String name) {
    synchronized (localResolveLock) {
      ObjectID value = localCache.get(name);
      if (value != null) { return faultValue(value); }
      return null;
    }
  }

  private T faultValue(ObjectID value) {
    try {
      return (T) platformService.lookupObject(value);
    } catch (AbortedOperationException e) {
      throw new ToolkitAbortableOperationException(e);
    }
  }

  @Override
  public void removeClusteredObject(String name) {
    if (name == null) { throw new NullPointerException("Name is null"); }
    synchronized (localResolveLock) {
      Object value = localCache.remove(name);
      if (value != null) {
        logicalInvokeRemove(name);
      }
    }
  }

  private void logicalInvokeRemove(String name) {
    tcManaged.logicalInvoke(LogicalOperation.REMOVE, new Object[] { name });
  }

  @Override
  public void __tc_managed(TCObject t) {
    tcManaged = t;
    gid = new GroupID(tcManaged.getObjectID().getGroupID());
    localResolveLock = tcManaged.getResolveLock();
  }

  @Override
  public TCObject __tc_managed() {
    return tcManaged;
  }

  @Override
  public boolean __tc_isManaged() {
    return tcManaged != null;
  }
}
