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
package com.terracotta.toolkit.type;

import org.terracotta.toolkit.config.Configuration;

import com.terracotta.toolkit.factory.ToolkitObjectFactory;
import com.terracotta.toolkit.object.TCToolkitObject;
import com.terracotta.toolkit.rejoin.RejoinAwareToolkitObject;

public interface IsolatedToolkitTypeFactory<T extends RejoinAwareToolkitObject, S extends TCToolkitObject> {

  /**
   * Used to create the unclustered type after faulting in the TCClusteredObject
   */
  T createIsolatedToolkitType(ToolkitObjectFactory<T> factory, IsolatedClusteredObjectLookup<S> lookup, String name,
                              Configuration config, S tcClusteredObject);

  /**
   * Used to create the TCClusteredObject to back the type
   */
  S createTCClusteredObject(Configuration config);

}
