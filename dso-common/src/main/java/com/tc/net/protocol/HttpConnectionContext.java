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
package com.tc.net.protocol;

import com.tc.async.api.EventContext;
import com.tc.bytes.TCByteBuffer;

import java.net.Socket;

public class HttpConnectionContext implements EventContext {

  private final TCByteBuffer buffer;
  private final Socket       socket;

  public HttpConnectionContext(Socket socket, TCByteBuffer buffer) {
    this.socket = socket;
    this.buffer = buffer;
  }

  public TCByteBuffer getBuffer() {
    return buffer;
  }

  public Socket getSocket() {
    return socket;
  }

}
