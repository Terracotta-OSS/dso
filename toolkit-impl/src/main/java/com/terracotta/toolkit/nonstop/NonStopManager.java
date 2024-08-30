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
package com.terracotta.toolkit.nonstop;

public interface NonStopManager {

  /**
   * begin a non stop operation with a timeout. Throws IllegalStateException if a nonstop operation is already started
   * for this thread
   */
  void begin(long timeout);

  /**
   * try to begin a non stop operation with a timeout. retruns false if a nonstop operation was already started
   */
  boolean tryBegin(long timeout);

  /**
   * Indicate that the non stop operation completed
   */
  void finish();

}