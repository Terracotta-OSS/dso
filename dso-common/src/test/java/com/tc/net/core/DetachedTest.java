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
package com.tc.net.core;

import com.tc.net.protocol.NullProtocolAdaptor;
import com.tc.test.TCTestCase;

import java.net.Socket;
import java.nio.channels.SocketChannel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DetachedTest extends TCTestCase {

  // see: DEV-7965
  public void testReadAndWriteFromClosedPipeSocket() throws Exception {
    // mock the socket and its channel
    SocketChannel socketChannel = mock(SocketChannel.class);
    Socket socket = mock(Socket.class);
    when(socketChannel.socket()).thenReturn(socket);

    // create the TCConnectionImpl
    TCConnectionManagerImpl connectionManager = new TCConnectionManagerImpl();
    NullProtocolAdaptor adaptor = new NullProtocolAdaptor();
    SocketParams socketParams = new SocketParams();
    CoreNIOServices coreNIOServices = new CoreNIOServices("mock core nio thread", null, socketParams);
    TCConnectionImpl tcConnection = new TCConnectionImpl(null, adaptor, socketChannel, connectionManager, coreNIOServices, socketParams, null);

    // detach the connection -> this creates the pipe socket, then close it
    Socket pipeSocket = tcConnection.detach();
    pipeSocket.close();

    // check that write returns fine
    assertEquals(0, tcConnection.doWrite());
    // check that read returns fine
    assertEquals(0, tcConnection.doRead());
  }

}
