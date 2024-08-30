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
package com.terracotta.toolkit.roots;

import org.terracotta.toolkit.ToolkitObjectType;
import org.terracotta.toolkit.config.Configuration;
import org.terracotta.toolkit.internal.ToolkitInternal;
import org.terracotta.toolkit.object.ToolkitObject;

import com.terracotta.toolkit.factory.ToolkitObjectFactory;
import com.terracotta.toolkit.object.AbstractDestroyableToolkitObject;
import com.terracotta.toolkit.object.TCToolkitObject;

/**
 * An aggregate toolkit root which is itself not clustered, but creates and stores the toolkit type in a clustered
 * object graph. How the clustered graph is created and maintained exactly depends on the implementation.
 */
public interface AggregateToolkitTypeRoot<T extends ToolkitObject, S extends TCToolkitObject> {

  /**
   * Gets an already created instance identified by <tt>name</tt> or creates one if none exists.
   */
  T getOrCreateToolkitType(ToolkitInternal toolkit, ToolkitObjectFactory<T> factory, String name,
                           Configuration configuration);

  /**
   * Removes the toolkit type identified by <tt>name</tt>
   */
  void removeToolkitType(ToolkitObjectType toolkitObjectType, String name);

  /**
   * Apply destroy
   */
  void applyDestroy(String name);

  void destroy(AbstractDestroyableToolkitObject obj, ToolkitObjectType type);

  void lookupOrCreateRoots();

  void dispose(ToolkitObjectType toolkitObjectType, String name);
}
