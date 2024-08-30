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

import com.terracotta.toolkit.object.TCToolkitObject;

/**
 * A Terracotta Root. <tt>ClusteredObject</tt>s added to this root are added to the clustered object graph of this root
 * and becomes available in the cluster
 */
public interface ToolkitTypeRoot<T extends TCToolkitObject> {

  /**
   * Adds a <tt>ClusteredObject</tt> to this root, identifiable by <tt>name</tt>
   */
  void addClusteredObject(String name, T clusteredObject);

  /**
   * Gets a <tt>ClusteredObject</tt> identified by <tt>name</tt>
   */
  T getClusteredObject(String name);

  /**
   * Removes the <tt>ClusteredObject</tt> identified by <tt>name</tt> from this root
   */
  void removeClusteredObject(String name);

}
