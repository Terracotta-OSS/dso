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
package com.tc.exception;

/**
 * Thrown when someone tries to call an unimplemented feature.
 */
public class ImplementMe extends TCRuntimeException {

  private static final String PRETTY_TEXT = "You've attempted to use an unsupported feature in this Terracotta product. Please consult "
                                            + "the product documentation, or email support@terracottatech.com for assistance.";

  /**
   * Construct new with default text
   */
  public ImplementMe() {
    this(PRETTY_TEXT);
  }

  /**
   * Construct with specified text
   * @param message The message
   */
  public ImplementMe(String message) {
    super(message);
  }

  /**
   * Construct with exception and use default text
   * @param cause The cause
   */
  public ImplementMe(Throwable cause) {
    super(PRETTY_TEXT, cause);
  }

  /**
   * Construct with specified message and cause
   * @param message Specified message
   * @param cause Cause
   */
  public ImplementMe(String message, Throwable cause) {
    super(message, cause);
  }

}
