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
package com.terracotta.toolkit.object;

import org.terracotta.toolkit.config.Configuration;

import com.terracotta.toolkit.config.ConfigChangeListener;

import java.io.Serializable;

/**
 * A <tt>ClusteredObject</tt> that itself contains one or more other <tt>ClusteredObject</tt>s and a common
 * configuration
 */
public interface ToolkitObjectStripe<C extends TCToolkitObject> extends TCToolkitObject, Iterable<C> {
  /**
   * Returns the configuration associated with this {@link ToolkitObjectStripe}
   */
  Configuration getConfiguration();

  /**
   * Change config parameter and propagate it to the L2.
   */
  void setConfigField(String key, Serializable value);

  void addConfigChangeListener(ConfigChangeListener listener);
}
