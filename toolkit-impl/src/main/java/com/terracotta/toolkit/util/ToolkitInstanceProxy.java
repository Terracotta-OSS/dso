/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
package com.terracotta.toolkit.util;

import org.terracotta.toolkit.ToolkitFeature;
import org.terracotta.toolkit.ToolkitObjectType;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.feature.FeatureNotSupportedException;
import org.terracotta.toolkit.object.ToolkitObject;
import org.terracotta.toolkit.rejoin.RejoinException;

import com.google.common.base.Preconditions;
import com.terracotta.toolkit.nonstop.NonStopConfigurationLookup;
import com.terracotta.toolkit.nonstop.NonStopContext;
import com.terracotta.toolkit.nonstop.NonStopInvocationHandler;
import com.terracotta.toolkit.nonstop.NonStopLockImpl;
import com.terracotta.toolkit.nonstop.NonStopSubTypeInvocationHandler;
import com.terracotta.toolkit.nonstop.ToolkitObjectLookup;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static com.google.common.base.Preconditions.checkArgument;

public abstract class ToolkitInstanceProxy {

  private static final Method TOOLKIT_FEATURE_IS_ENABLED_METHOD;
  static {
    Method m = null;
    try {
      m = ToolkitFeature.class.getMethod("isEnabled", new Class[0]);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
    if (m == null) { throw new AssertionError("isEnabled() method not found in ToolkitFeature"); }
    TOOLKIT_FEATURE_IS_ENABLED_METHOD = m;
  }

  public static <T> T newDestroyedInstanceProxy(final String name, final Class<T> clazz) {
    InvocationHandler handler = new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        throw new IllegalStateException("The toolkit instance with name '" + name + "' (instance of " + clazz.getName()
                                        + ") has already been destroyed");
      }
    };

    return newToolkitProxy(clazz, handler);
  }

  public static <T> T newRejoinInProgressProxy(final String name, final Class<T> clazz) {
    InvocationHandler handler = new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        throw new RejoinException("The toolkit instance with name '" + name + "' (instance of " + clazz.getName()
                                  + ") is not usable at the moment as rejoin is in progress");
      }
    };

    return newToolkitProxy(clazz, handler);
  }

  public static <T extends ToolkitObject> T newNonStopProxy(final String name,
                                                            final ToolkitObjectType toolkitObjectType,
                                                            final NonStopContext context, final Class<T> clazz,
                                                            final ToolkitObjectLookup toolkitObjectLookup) {
    NonStopConfigurationLookup nonStopConfigurationLookup = new NonStopConfigurationLookup(context, toolkitObjectType,
                                                                                           name);

    InvocationHandler handler = new NonStopInvocationHandler<T>(context, nonStopConfigurationLookup,
                                                                toolkitObjectLookup);

    return newToolkitProxy(clazz, handler);
  }


  public static <T extends ToolkitObject> T newNonStopProxy(final NonStopConfigurationLookup nonStopConfigurationLookup,
                                                            final NonStopContext context,
                                                            final ToolkitObjectLookup toolkitObjectLookup,
                                                            final Class<?> ... clazz) {
    checkArgument(clazz.length >= 1, "Need at least 1 class to be specified");
    InvocationHandler handler = new NonStopInvocationHandler<T>(context, nonStopConfigurationLookup,
                                                                toolkitObjectLookup);

    return (T) newToolkitProxy(clazz, handler);
  }


  public static <T> T newNonStopSubTypeProxy(final NonStopConfigurationLookup nonStopConfigurationLookup,
                                             final NonStopContext context, final T delegate, final Class<T> clazz) {
    if (clazz.equals(ToolkitLock.class)) {
      ToolkitObjectLookup<ToolkitLock> lookup = new ToolkitObjectLookup<ToolkitLock>() {

        @Override
        public ToolkitLock getInitializedObject() {
          return (ToolkitLock) delegate;
        }

        @Override
        public ToolkitLock getInitializedObjectOrNull() {
          return (ToolkitLock) delegate;
        }
      };
      return (T) new NonStopLockImpl(context, nonStopConfigurationLookup, lookup);
    }
    InvocationHandler handler = new NonStopSubTypeInvocationHandler<T>(context, nonStopConfigurationLookup, delegate,
                                                                       clazz);

    return newToolkitProxy(clazz, handler);
  }

  public static <T extends ToolkitFeature> T newFeatureNotSupportedProxy(final Class<T> clazz) {
    InvocationHandler handler = new InvocationHandler() {
      @Override
      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.equals(TOOLKIT_FEATURE_IS_ENABLED_METHOD)) { return false; }
        throw new FeatureNotSupportedException("Feature specified by '" + clazz.getName() + "' is not supported!");
      }
    };
    return newToolkitProxy(clazz, handler);
  }

  public static <T> T newToolkitProxy(Class<T> clazz, InvocationHandler handler) {
    return clazz.cast(newToolkitProxy(new Class<?>[] { clazz }, handler));
  }

  public static Object newToolkitProxy(Class<?>[] clazzes, InvocationHandler handler) {
    return Proxy.newProxyInstance(ToolkitInstanceProxy.class.getClassLoader(), clazzes, handler);
  }
}
