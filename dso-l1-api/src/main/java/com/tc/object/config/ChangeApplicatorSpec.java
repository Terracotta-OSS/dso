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
package com.tc.object.config;

/**
 * Defines change applicators to apply for each class.  The change applicator
 * allows a module to replace a class definition if the module needs to swap in an
 * alternate version with some differing functionality in a cluster. 
 * 
 */
public interface ChangeApplicatorSpec {
  
  /**
   * Get the change applicator for a specified class
   * @param clazz The class
   * @return The change applicator if one exists, or null otherwise
   */
  public Class getChangeApplicator(Class clazz);
}
