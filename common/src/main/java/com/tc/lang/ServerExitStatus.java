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
package com.tc.lang;

public interface ServerExitStatus {
  public static final short EXITCODE_STOP_REQUEST = 0;

  /**
   * RMP-309 : Error code to convey auto-restart of TC server needed
   */
  public static final short EXITCODE_RESTART_REQUEST = 11;

  /**
   * Error code to convey auto-restart of TC server needed in Safe Mode
   */
  public static final short EXITCODE_RESTART_IN_SAFE_MODE_REQUEST = 12;

  /**
   * Error codes during the Server start
   */
  public static final short EXITCODE_STARTUP_ERROR   = 2;

  /**
   * Error code on other fatal condition
   */
  public static final short EXITCODE_FATAL_ERROR     = 3;
}
