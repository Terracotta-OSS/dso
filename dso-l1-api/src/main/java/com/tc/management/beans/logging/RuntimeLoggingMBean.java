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
package com.tc.management.beans.logging;

import com.tc.management.TerracottaMBean;

import javax.management.NotificationEmitter;

/**
 * MBean for manipulating client logging at runtime, as setup through the configuration.
 */

public interface RuntimeLoggingMBean extends TerracottaMBean, NotificationEmitter {
  public static final String DISTRIBUTED_METHOD_DEBUG_EVENT_TYPE = "tc.logging.runtime.DistributedMethodDebug";
  public static final String FIELD_CHANGE_DEBUG_EVENT_TYPE       = "tc.logging.runtime.FieldChangeDebug";
  public static final String LOCK_DEBUG_EVENT_TYPE               = "tc.logging.runtime.LockDebug";
  public static final String NON_PORTABLE_DUMP_EVENT_TYPE        = "tc.logging.runtime.NonPortableDump";
  public static final String WAIT_NOTIFY_DEBUG_EVENT_TYPE        = "tc.logging.runtime.WaitNotifyDebug";
  public static final String NEW_OBJECT_DEBUG_EVENT_TYPE         = "tc.logging.runtime.NewObjectDebug";
  public static final String NAMED_LOADER_DEBUG_EVENT_TYPE       = "tc.logging.runtime.NamedLoaderDebug";
  public static final String FLUSH_DEBUG_EVENT_TYPE              = "tc.logging.runtime.FlushDebug";
  public static final String FAULT_DEBUG_EVENT_TYPE              = "tc.logging.runtime.FaultDebug";

  void setNonPortableDump(boolean nonPortableDump);

  boolean getNonPortableDump();

  void setLockDebug(boolean lockDebug);

  boolean getLockDebug();

  void setFieldChangeDebug(boolean fieldChangeDebug);

  boolean getFieldChangeDebug();

  void setWaitNotifyDebug(boolean waitNotifyDebug);

  boolean getWaitNotifyDebug();

  void setDistributedMethodDebug(boolean distributedMethodDebug);

  boolean getDistributedMethodDebug();

  void setNewObjectDebug(boolean newObjectDebug);

  boolean getNewObjectDebug();

  void setNamedLoaderDebug(boolean namedLoaderDebug);

  boolean getNamedLoaderDebug();
  
  void setFlushDebug(boolean flushDebug);

  boolean getFlushDebug();

  void setFaultDebug(boolean faultDebug);

  boolean getFaultDebug();
}
