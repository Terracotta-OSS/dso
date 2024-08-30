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

public interface RuntimeOutputOptionsMBean extends TerracottaMBean, NotificationEmitter {
  public static final String AUTOLOCK_DETAILS_EVENT_TYPE = "tc.logging.runtime-output.AutoLockDetails";
  public static final String CALLER_EVENT_TYPE           = "tc.logging.runtime-output.Caller";
  public static final String FULL_STACK_EVENT_TYPE       = "tc.logging.runtime-output.FullStack";

  void setAutoLockDetails(boolean autolockDetails);

  boolean getAutoLockDetails();

  void setCaller(boolean caller);

  boolean getCaller();

  void setFullStack(boolean fullStack);

  boolean getFullStack();
}
