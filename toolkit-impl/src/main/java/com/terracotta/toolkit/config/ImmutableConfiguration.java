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
package com.terracotta.toolkit.config;

import org.terracotta.toolkit.config.Configuration;

import java.io.Serializable;

public class ImmutableConfiguration extends UnclusteredConfiguration {

  public ImmutableConfiguration(Configuration configuration) {
    super(configuration);
  }

  @Override
  public final void internalSetConfigMapping(String name, Serializable value) {
    throw new UnsupportedOperationException("This configuration is immutable, cannot change '" + name + "' to " + value);
  }

}
