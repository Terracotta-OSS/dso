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
package com.tc.util;

import sun.reflect.ReflectionFactory;

import com.tc.exception.TCRuntimeException;

import java.lang.reflect.Constructor;

/**
 * A wrapper for unsafe usage in class like Atomic Variables, ReentrantLock, etc.
 */
@SuppressWarnings("restriction")
public class ReflectionUtil {
  private static final Constructor       refConstructor;
  private static final ReflectionFactory rf = ReflectionFactory.getReflectionFactory();

  static {
    try {
      refConstructor = Object.class.getDeclaredConstructor(new Class[0]);
    } catch (NoSuchMethodException e) {
      throw new TCRuntimeException(e);
    }
  }

  private ReflectionUtil() {
    // Disallow any object to be instantiated.
  }

  public static Constructor newConstructor(Class clazz) {
    return rf.newConstructorForSerialization(clazz, refConstructor);
  }

}
