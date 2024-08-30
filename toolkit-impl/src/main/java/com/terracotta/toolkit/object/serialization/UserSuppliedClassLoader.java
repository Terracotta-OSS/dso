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

public class UserSuppliedClassLoader extends ClassLoader {

  private final ClassLoader userLoader;
  private final ClassLoader toolkitLoader;

  public UserSuppliedClassLoader(ClassLoader userLoader, ClassLoader toolkitLoader) {
    this.userLoader = userLoader;
    this.toolkitLoader = toolkitLoader;
  }

  @Override
  public Class<?> loadClass(String name) throws ClassNotFoundException {
    try {
      return userLoader.loadClass(name);
    } catch (ClassNotFoundException cnfe) {
      //
    }

    return toolkitLoader.loadClass(name);
  }

}
