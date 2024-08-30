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
package com.terracotta.toolkit.nonstop;

import com.terracotta.toolkit.util.ToolkitInstanceProxy;

import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.terracotta.toolkit.util.ToolkitInstanceProxy.newToolkitProxy;

public class ExceptionOnTimeoutBehaviorResolver {
  private final ExceptionOnTimeoutInvocationHandler handler                     = new ExceptionOnTimeoutInvocationHandler();
  private final ConcurrentMap<Class, Object>        exceptionOnTimeoutBehaviors = new ConcurrentHashMap<Class, Object>();

  public <E> E resolve(Class<E> klazz) {
    Object rv = exceptionOnTimeoutBehaviors.get(klazz);
    if (rv == null) {
      Object newProxyInstance = newToolkitProxy(klazz, handler);
      Object oldProxyInstance = exceptionOnTimeoutBehaviors.putIfAbsent(klazz, newProxyInstance);
      rv = oldProxyInstance != null ? oldProxyInstance : newProxyInstance;
    }
    return (E) rv;
  }
}
