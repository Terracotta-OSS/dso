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
package com.tc.l2.objectserver;

import org.junit.Before;
import org.junit.Test;

import com.tc.async.api.Sink;
import com.tc.async.api.Stage;
import com.tc.net.ClientID;
import com.tc.net.ServerID;
import com.tc.object.tx.ServerTransactionID;
import com.tc.object.tx.TransactionID;
import com.tc.objectserver.context.ServerTransactionCompleteContext;
import com.tc.objectserver.core.api.ServerConfigurationContext;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServerTransactionFactoryTest {

  private Sink lwmSink;
  private ServerTransactionFactory serverTransactionFactory;

  @Before
  public void setUp() throws Exception {
    lwmSink = mock(Sink.class);
    serverTransactionFactory = new ServerTransactionFactory(ServerID.NULL_ID);
    Stage stage = when(mock(Stage.class).getSink()).thenReturn(lwmSink).getMock();
    ServerConfigurationContext serverConfigurationContext = when(
        mock(ServerConfigurationContext.class).getStage(
            ServerConfigurationContext.TRANSACTION_LOWWATERMARK_STAGE)).thenReturn(stage).getMock();
    serverTransactionFactory.initializeContext(serverConfigurationContext);
  }

  @Test
  public void testRemoveCompletedServerTransaction() throws Exception {
    ServerTransactionID stxID = new ServerTransactionID(ServerID.NULL_ID, new TransactionID(1));
    serverTransactionFactory.transactionCompleted(stxID);
    verify(lwmSink).add(new ServerTransactionCompleteContext(stxID));
  }

  @Test
  public void testNoRemoveNonServerInitiatedTransaction() throws Exception {
    ServerTransactionID stxID = new ServerTransactionID(new ClientID(0), new TransactionID(1));
    serverTransactionFactory.transactionCompleted(stxID);
    verify(lwmSink, never()).add(any(ServerTransactionCompleteContext.class));
  }
}