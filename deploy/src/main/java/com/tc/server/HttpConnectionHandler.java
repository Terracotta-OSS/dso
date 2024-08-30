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
package com.tc.server;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.bytes.TCByteBuffer;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.net.protocol.HttpConnectionContext;

import java.net.Socket;

public class HttpConnectionHandler extends AbstractEventHandler {

  private static final TCLogger     logger = TCLogging.getLogger(HttpConnectionContext.class);

  private final TerracottaConnector terracottaConnector;

  public HttpConnectionHandler(TerracottaConnector terracottaConnector) {
    this.terracottaConnector = terracottaConnector;
    //
  }

  @Override
  public void handleEvent(EventContext context) {
    HttpConnectionContext connContext = (HttpConnectionContext) context;

    Socket s = connContext.getSocket();
    TCByteBuffer buffer = connContext.getBuffer();
    byte[] data = new byte[buffer.limit()];
    buffer.get(data);
    try {
      terracottaConnector.handleSocketFromDSO(s, data);
    } catch (Exception e) {
      logger.error(e);
    }
  }

}
