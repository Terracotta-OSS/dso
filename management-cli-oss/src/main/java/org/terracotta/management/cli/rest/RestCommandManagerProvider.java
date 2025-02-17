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
package org.terracotta.management.cli.rest;

public class RestCommandManagerProvider {

  private static final String COMMAND_MANAGER_PROPERTY_KEY = "manager";
  private static RestCommandManager manager;

  private RestCommandManagerProvider() {
  }

  @SuppressWarnings("unchecked")
  public static RestCommandManager getCommandManager() {
    if (manager == null) {
      String clazz = System.getProperty(COMMAND_MANAGER_PROPERTY_KEY);
      Class<RestCommandManager> managerClazz;
      try {
        managerClazz = (Class<RestCommandManager>) Class.forName(clazz);
        manager = managerClazz.newInstance();
      } catch (ClassNotFoundException e) {
        e.printStackTrace();
      } catch (InstantiationException e) {
        e.printStackTrace();
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return manager;
  }

}
