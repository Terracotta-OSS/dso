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
package com.tc.object.bytecode;

import com.tc.object.TCObject;

/**
 * Manageable interface for locks, etc.
 */
public interface Manageable {

  /**
   * Pass in TCObject peer for this object
   * 
   * @param t TCObject
   */
  public void __tc_managed(TCObject t);

  /**
   * Get TCObject for this object
   * 
   * @return The TCObject
   */
  public TCObject __tc_managed();

  /**
   * Check whether this object is managed
   * 
   * @return True if managed
   */
  public boolean __tc_isManaged();

}
