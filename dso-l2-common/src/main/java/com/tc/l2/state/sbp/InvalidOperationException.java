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
package com.tc.l2.state.sbp;

public class InvalidOperationException extends RuntimeException {
  public InvalidOperationException() {
  }

  public InvalidOperationException(final String message) {
    super(message);
  }

  public InvalidOperationException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public InvalidOperationException(final Throwable cause) {
    super(cause);
  }
}
