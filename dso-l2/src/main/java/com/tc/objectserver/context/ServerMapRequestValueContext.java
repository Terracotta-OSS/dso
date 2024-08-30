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
package com.tc.objectserver.context;

import com.tc.async.api.Sink;
import com.tc.net.ClientID;
import com.tc.object.ObjectID;
import com.tc.object.ServerMapGetValueRequest;
import com.tc.object.ServerMapRequestID;
import com.tc.object.ServerMapRequestType;

import java.util.Collection;

public class ServerMapRequestValueContext extends ServerMapRequestContext {

  private final Collection<ServerMapGetValueRequest> getValueRequests;

  public ServerMapRequestValueContext(final ClientID clientID, final ObjectID mapID,
                                      final Collection<ServerMapGetValueRequest> getValueRequests,
                                      final Sink destinationSink) {
    super(clientID, mapID, destinationSink);
    this.getValueRequests = getValueRequests;
  }

  public Collection<ServerMapGetValueRequest> getValueRequests() {
    return this.getValueRequests;
  }

  @Override
  public String toString() {
    return super.toString() + " [ value requests : " + this.getValueRequests + "]";
  }

  @Override
  public ServerMapRequestType getRequestType() {
    return ServerMapRequestType.GET_VALUE_FOR_KEY;
  }

  @Override
  public ServerMapRequestID getRequestID() {
    return ServerMapRequestID.NULL_ID;
  }
}
