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

import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.config.Configuration;
import org.terracotta.toolkit.internal.ToolkitInternal;

import com.tc.platform.PlatformService;
import com.terracotta.toolkit.factory.ToolkitObjectFactory;
import com.terracotta.toolkit.object.TCToolkitObject;
import com.terracotta.toolkit.object.ToolkitObjectStripe;

/**
 * A factory thats responsible for creating objects necessary for an AggregateToolkitType
 */
public interface DistributedToolkitTypeFactory<T extends DistributedToolkitType<S>, S extends TCToolkitObject> {

  /**
   * Create the actual <tt>AggregateToolkitType</tt> with the specified objects from stripes
   *
   * @param name
   * @param configMutationLock
   */
  T createDistributedType(ToolkitInternal toolkit, ToolkitObjectFactory factory,
                          DistributedClusteredObjectLookup<S> lookup, String name,
                          ToolkitObjectStripe<S>[] stripeObjects, Configuration configuration,
                          PlatformService platformService, ToolkitLock configMutationLock);

  /**
   * Create the stripe objects
   */
  ToolkitObjectStripe<S>[] createStripeObjects(String name, Configuration configuration, int numStripes,
                                               PlatformService platformService);

  /**
   * @throws IllegalArgumentException if the existingObject has conflicting configuration with the passed in parameter
   */
  void validateExistingLocalInstanceConfig(T existingObject, Configuration config) throws IllegalArgumentException;

  /**
   * Creates and returns a new Configuration for creating this distributed type first time in the <b>cluster</b>.
   * Populates values that already exists in the passed configuration. For missing values in the config, populates
   * default values
   */
  Configuration newConfigForCreationInCluster(Configuration userConfig);

  /**
   * Creates and returns a new Configuration for creating this distributed type first time in <b>current node</b>.
   * Populates values that already exists in the passed configuration. For missing values in the config, uses existing
   * values from existing cluster instance configuration
   */
  Configuration newConfigForCreationInLocalNode(String name, ToolkitObjectStripe<S>[] existingStripedObjects,
                                                Configuration userConfig);

  /**
   * @throws IllegalArgumentException if the configuration passed has Illegal Values which are not Supported.
   */
  void validateConfig(Configuration config) throws IllegalArgumentException;

}
