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
package com.tc.object.idprovider.impl;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.object.msg.ObjectIDBatchRequestMessage;
import com.tc.object.msg.ObjectIDBatchRequestMessageFactory;
import com.tc.object.msg.ObjectIDBatchRequestResponseMessage;
import com.tc.util.Assert;
import com.tc.util.sequence.BatchSequenceProvider;
import com.tc.util.sequence.BatchSequenceReceiver;

/**
 * Manages object id requests to servers
 */
public class RemoteObjectIDBatchSequenceProvider extends AbstractEventHandler implements BatchSequenceProvider {
  private final ObjectIDBatchRequestMessageFactory mf;
  private volatile BatchSequenceReceiver           receiver;

  public RemoteObjectIDBatchSequenceProvider(ObjectIDBatchRequestMessageFactory mf) {
    this.mf = mf;
  }

  public void setBatchSequenceReceiver(BatchSequenceReceiver receiver) {
    this.receiver = receiver;
  }

  @Override
  public void requestBatch(BatchSequenceReceiver r, int size) {
    Assert.assertTrue(receiver == r);
    ObjectIDBatchRequestMessage m = mf.newObjectIDBatchRequestMessage();
    m.initialize(size);
    m.send();
  }

  @Override
  public void handleEvent(EventContext context) {
    ObjectIDBatchRequestResponseMessage m = (ObjectIDBatchRequestResponseMessage) context;
    receiver.setNextBatch(m.getBatchStart(), m.getBatchEnd());
  }

}