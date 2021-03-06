/* 
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at 
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is 
 *      Terracotta, Inc., a Software AG company
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
