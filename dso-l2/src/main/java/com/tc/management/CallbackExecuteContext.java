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
package com.tc.management;

import com.sun.jmx.remote.generic.SynchroCallback;
import com.tc.async.api.EventContext;
import com.tc.util.concurrent.TCFuture;

import javax.management.remote.message.Message;

public class CallbackExecuteContext implements EventContext {
  private final TCFuture future;
  private final Message request;
  private final ClassLoader threadContextLoader;
  private final SynchroCallback callback;

  public CallbackExecuteContext(ClassLoader threadContextLoader, SynchroCallback callback, Message request, TCFuture future) {
    this.threadContextLoader = threadContextLoader;
    this.callback = callback;
    this.request = request;
    this.future = future;
  }

  public TCFuture getFuture() {
    return future;
  }

  public Message getRequest() {
    return request;
  }

  public ClassLoader getThreadContextLoader() {
    return threadContextLoader;
  }

  public SynchroCallback getCallback() {
    return callback;
  }
}