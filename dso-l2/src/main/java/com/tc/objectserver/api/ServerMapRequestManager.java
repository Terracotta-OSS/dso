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
package com.tc.objectserver.api;

import com.tc.net.ClientID;
import com.tc.object.ObjectID;
import com.tc.object.ServerMapGetValueRequest;
import com.tc.object.ServerMapRequestID;
import com.tc.objectserver.context.ServerMapGetAllSizeHelper;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.text.PrettyPrintable;

import java.util.Collection;

public interface ServerMapRequestManager extends PrettyPrintable {

  public void requestSize(ServerMapRequestID requestID, ClientID clientID, ObjectID mapID,
                          ServerMapGetAllSizeHelper helper);

  public void requestAllKeys(ServerMapRequestID requestID, ClientID clientID, ObjectID mapID);

  public void sendResponseFor(ObjectID mapID, ManagedObject managedObject);

  public void sendMissingObjectResponseFor(ObjectID mapID);

  public void requestValues(ClientID clientID, ObjectID mapID, Collection<ServerMapGetValueRequest> requests);

}
