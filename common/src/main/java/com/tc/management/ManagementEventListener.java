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

import java.util.Map;

/**
 * Management events listener interface.
 *
 * @author Ludovic Orban
 */
public interface ManagementEventListener {

  static String CONTEXT_SOURCE_NODE_NAME = "CONTEXT_SOURCE_NODE_NAME";
  static String CONTEXT_SOURCE_JMX_ID = "CONTEXT_SOURCE_JMX_ID";
  static String CONTEXT_SOURCE_REMOTE_ADDRESS = "CONTEXT_SOURCE_REMOTE_ADDRESS";

  /**
   * Get the classloader from which to load classes of deserialized objects.
   *
   * @return the class loader that is going to be used to deserialize the event.
   */
  ClassLoader getClassLoader();

  /**
   * Called when an event is sent by a L1.
   *
   * @param event the event object.
   * @param context the event context.
   */
  void onEvent(TCManagementEvent event, Map<String, Object> context);

}
