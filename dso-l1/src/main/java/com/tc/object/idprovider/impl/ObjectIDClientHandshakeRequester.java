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
package com.tc.object.idprovider.impl;

import com.tc.net.GroupID;
import com.tc.net.NodeID;
import com.tc.object.handshakemanager.ClientHandshakeCallback;
import com.tc.object.msg.ClientHandshakeMessage;
import com.tc.util.sequence.BatchSequenceReceiver;

public class ObjectIDClientHandshakeRequester implements ClientHandshakeCallback {

  private final BatchSequenceReceiver sequence;
  private final GroupID               requestTo;

  public ObjectIDClientHandshakeRequester(BatchSequenceReceiver sequence) {
    this(sequence, GroupID.ALL_GROUPS);
  }

  public ObjectIDClientHandshakeRequester(BatchSequenceReceiver sequence, GroupID requestTo) {
    this.sequence = sequence;
    this.requestTo = requestTo;
  }

  @Override
  public void cleanup() {
    // nothing to do
  }

  @Override
  public void initializeHandshake(NodeID thisNode, NodeID remoteNode, ClientHandshakeMessage handshakeMessage) {
    if (GroupID.ALL_GROUPS.equals(requestTo) || remoteNode.equals(requestTo)) {
      handshakeMessage.setIsObjectIDsRequested(sequence.isBatchRequestPending());
    } else {
      handshakeMessage.setIsObjectIDsRequested(false);
    }
  }

  @Override
  public void pause(NodeID remoteNode, int disconnected) {
    // NOP
  }

  @Override
  public void unpause(NodeID remoteNode, int disconnected) {
    // NOP
  }

  @Override
  public void shutdown(boolean fromShutdownHook) {
    // NOP
  }

}
