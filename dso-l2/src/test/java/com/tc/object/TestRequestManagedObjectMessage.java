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
package com.tc.object;

import com.tc.net.ClientID;
import com.tc.net.NodeID;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.object.msg.RequestManagedObjectMessage;
import com.tc.util.ObjectIDSet;

public class TestRequestManagedObjectMessage implements RequestManagedObjectMessage {

  private ObjectIDSet removed;
  private ObjectIDSet objectIDs;

  public TestRequestManagedObjectMessage() {
    super();
  }

  @Override
  public ObjectRequestID getRequestID() {
    return null;
  }

  @Override
  public ObjectIDSet getRequestedObjectIDs() {
    return this.objectIDs;
  }

  public void setObjectIDs(ObjectIDSet IDs) {
    this.objectIDs = IDs;
  }

  @Override
  public ObjectIDSet getRemoved() {
    return this.removed;
  }

  public void setRemoved(ObjectIDSet rm) {
    this.removed = rm;
  }

  @Override
  public void initialize(ObjectRequestID rID, ObjectIDSet requestedObjectIDs, int requestDepth,
                         ObjectIDSet removeObjects) {
    //
  }

  @Override
  public void send() {
    //
  }

  @Override
  public MessageChannel getChannel() {
    return null;
  }

  @Override
  public NodeID getSourceNodeID() {
    return new ClientID(0);
  }

  @Override
  public int getRequestDepth() {
    return 400;
  }

  @Override
  public void recycle() {
    return;
  }

  @Override
  public String getRequestingThreadName() {
    return "TestThreadDummy";
  }

  @Override
  public LOOKUP_STATE getLookupState() {
    return LOOKUP_STATE.CLIENT;
  }

  @Override
  public ClientID getClientID() {
    return new ClientID(0);
  }

  @Override
  public Object getKey() {
    return null;
  }

}
