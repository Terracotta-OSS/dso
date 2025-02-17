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

import com.tc.net.ClientID;
import com.tc.object.ObjectID;
import com.tc.object.ObjectRequestID;
import com.tc.object.ObjectRequestServerContext;

import java.util.SortedSet;

public class ObjectRequestServerContextImpl implements ObjectRequestServerContext {

  private final ClientID            requestedNodeID;
  private final ObjectRequestID     objectRequestID;
  private final SortedSet<ObjectID> lookupIDs;
  private final String              requestingThreadName;
  private final int                 requestDepth;
  private final LOOKUP_STATE        lookupState;

  public ObjectRequestServerContextImpl(final ClientID requestNodeID, final ObjectRequestID objectRequestID,
                                        final SortedSet<ObjectID> lookupObjectIDs, final String requestingThreadName,
                                        final int requestDepth, final LOOKUP_STATE lookupState) {
    this.requestDepth = requestDepth;
    this.requestedNodeID = requestNodeID;
    this.objectRequestID = objectRequestID;
    this.lookupIDs = lookupObjectIDs;
    this.requestingThreadName = requestingThreadName;
    this.lookupState = lookupState;
  }

  /**
   * This is mutated outside, don't give a copy
   */
  @Override
  public SortedSet<ObjectID> getRequestedObjectIDs() {
    return this.lookupIDs;
  }

  @Override
  public int getRequestDepth() {
    return this.requestDepth;
  }

  @Override
  public ObjectRequestID getRequestID() {
    return this.objectRequestID;
  }

  @Override
  public ClientID getClientID() {
    return this.requestedNodeID;
  }

  @Override
  public String getRequestingThreadName() {
    return this.requestingThreadName;
  }

  @Override
  public LOOKUP_STATE getLookupState() {
    return this.lookupState;
  }

  @Override
  public Object getKey() {
    return this.requestedNodeID;
  }
}
