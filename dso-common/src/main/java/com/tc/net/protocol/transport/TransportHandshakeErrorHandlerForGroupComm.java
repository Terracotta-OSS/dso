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
package com.tc.net.protocol.transport;

import com.tc.logging.CustomerLogging;
import com.tc.logging.TCLogger;

public class TransportHandshakeErrorHandlerForGroupComm implements TransportHandshakeErrorHandler {

  private static final TCLogger consoleLogger = CustomerLogging.getConsoleLogger();

  @Override
  public void handleHandshakeError(TransportHandshakeErrorContext e) {
    // print error message on console
    if (e.getErrorType() == TransportHandshakeError.ERROR_STACK_MISMATCH) consoleLogger.error(e.getMessage());
    else consoleLogger.error(e);
    // top layer at TCGroupMemberDiscoveryStatic to terminate connection
  }

}
