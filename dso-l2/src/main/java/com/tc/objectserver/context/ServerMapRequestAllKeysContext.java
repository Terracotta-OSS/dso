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
package com.tc.objectserver.context;

import com.tc.async.api.Sink;
import com.tc.net.ClientID;
import com.tc.object.ObjectID;
import com.tc.object.ServerMapRequestID;
import com.tc.object.ServerMapRequestType;

public class ServerMapRequestAllKeysContext extends ServerMapRequestContext {

  private final ServerMapRequestID requestID;

  public ServerMapRequestAllKeysContext(final ServerMapRequestID requestID, final ClientID clientID,
                                        final ObjectID mapID, final Sink destinationSink) {
    super(clientID, mapID, destinationSink);
    this.requestID = requestID;
  }

  @Override
  public ServerMapRequestID getRequestID() {
    return this.requestID;
  }

  @Override
  public ServerMapRequestType getRequestType() {
    return ServerMapRequestType.GET_ALL_KEYS;
  }

}
