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
package com.terracotta.toolkit.object.serialization;

public class ThreadContextAwareClassLoader extends ClassLoader {

  public ThreadContextAwareClassLoader(ClassLoader parent) {
    super(parent);
  }

  // Should it be findClass here ??
  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    // Check whether it's already loaded
    Class loadedClass = findLoadedClass(name);
    if (loadedClass != null) { return loadedClass; }

    // Try to load from thread context classloader, if it exists
    try {
      ClassLoader tccl = Thread.currentThread().getContextClassLoader();
      return Class.forName(name, false, tccl);
    } catch (ClassNotFoundException e) {
      // Swallow exception - does not exist in tccl
    }

    // If not found locally, use normal parent delegation
    return super.loadClass(name);
  }

}
