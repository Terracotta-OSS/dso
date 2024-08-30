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
package com.terracotta.management.service;

/**
 * @author Ludovic Orban
 */
public interface TimeoutService {

  /**
   * Get the call timeout previously set. If none was set, the default one is returned.
   *
   * @return the call timeout.
   */
  long getCallTimeout();

  /**
   * Get the connection timeout previously set. If none was set, the default one is returned.
   *
   * @return the connection timeout.
   */
  long getConnectionTimeout();

  /**
   * Set the call timeout for the current thread.
   *
   * @param timeout the call timeout.
   */
  void setCallTimeout(long timeout);

  /**
   * Clear the call timeout for the current thread.
   */
  void clearCallTimeout();
}
