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
package com.tc.objectserver.persistence.impl;

import com.tc.async.api.EventContext;
import com.tc.async.api.Sink;
import com.tc.l2.api.L2Coordinator;
import com.tc.l2.api.ReplicatedClusterStateManager;
import com.tc.objectserver.core.impl.TestServerConfigurationContext;
import com.tc.objectserver.handler.GlobalTransactionIDBatchRequestHandler;
import com.tc.objectserver.handler.GlobalTransactionIDBatchRequestHandler.GlobalTransactionIDBatchRequestContext;
import com.tc.test.TCTestCase;
import com.tc.util.sequence.BatchSequenceReceiver;
import org.mockito.ArgumentMatcher;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.AdditionalMatchers.and;

public class GlobalTransactionIDBatchRequestHandlerTest extends TCTestCase {

  private ReplicatedClusterStateManager          clusterStateManager;
  private GlobalTransactionIDBatchRequestHandler provider;
  private TestMutableSequence                    persistentSequence;
  private Sink                                   requestBatchSink;

  @Override
  public void setUp() throws Exception {
    persistentSequence = spy(new TestMutableSequence());
    requestBatchSink = mock(Sink.class);
    clusterStateManager = mock(ReplicatedClusterStateManager.class);

    provider = new GlobalTransactionIDBatchRequestHandler(persistentSequence);
    provider.setRequestBatchSink(requestBatchSink);

    TestServerConfigurationContext scc = new TestServerConfigurationContext();
    scc.l2Coordinator = mock(L2Coordinator.class);
    when(scc.l2Coordinator.getReplicatedClusterStateManager()).thenReturn(clusterStateManager);
    provider.initializeContext(scc);
  }

  public void testRequestBatch() throws Exception {
    BatchSequenceReceiver receiver = mock(BatchSequenceReceiver.class);
    provider.requestBatch(receiver, 5);
    verify(requestBatchSink).add((EventContext) argThat(new ArgumentMatcher<GlobalTransactionIDBatchRequestContext>() {
      @Override
      public boolean matches(final GlobalTransactionIDBatchRequestContext o) {
        return receiver.equals(o.getReceiver()) && o.getBatchSize() == 5;
      }
    }));
  }

  public void testHandleRequest() throws Exception {
    BatchSequenceReceiver receiver = mock(BatchSequenceReceiver.class);
    GlobalTransactionIDBatchRequestContext context = new GlobalTransactionIDBatchRequestContext(receiver, 5);
    provider.handleEvent(context);
    verify(clusterStateManager).publishNextAvailableGlobalTransactionID(5);
    verify(persistentSequence).nextBatch(5);
    verify(receiver).setNextBatch(0, 5);
  }
}
