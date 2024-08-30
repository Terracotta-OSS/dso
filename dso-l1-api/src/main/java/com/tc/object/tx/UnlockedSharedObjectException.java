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
package com.tc.object.tx;

import com.tc.exception.ExceptionWrapper;
import com.tc.exception.ExceptionWrapperImpl;

/**
 * Thrown when there is an attempt to access a shared object outside the scope of a shared lock.
 */
public class UnlockedSharedObjectException extends RuntimeException {

  public static final String            TROUBLE_SHOOTING_GUIDE = "http://www.terracotta.org/kit/reflector?kitID=default&pageID=usoe";

  private static final ExceptionWrapper wrapper                = new ExceptionWrapperImpl();

  private UnlockedSharedObjectException(final String message) {
    super(wrapper.wrap(message));
  }

  public UnlockedSharedObjectException(final String message, final String threadName, final String vmId) {
    this(UnlockedSharedObjectException.createDisplayableString(message, threadName, vmId));
  }

  public UnlockedSharedObjectException(final String message, final String threadName, final long vmId) {
    this(UnlockedSharedObjectException.createDisplayableString(message, threadName, Long.toString(vmId)));
  }

  public UnlockedSharedObjectException(final String message, final String threadName, final long vmId,
                                       final String details) {
    this(UnlockedSharedObjectException.createDisplayableString(message, threadName, Long.toString(vmId)) + "\n"
         + details);
  }

  private static String createDisplayableString(final String message, final String threadName, final String vmId) {
    return message + "\n\nCaused by Thread: " + threadName + " in VM(" + vmId + ")";
  }
}
