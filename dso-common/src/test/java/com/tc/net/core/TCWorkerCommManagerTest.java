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
package com.tc.net.core;

import com.tc.exception.ImplementMe;
import com.tc.logging.LogLevelImpl;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.net.ServerID;
import com.tc.net.TCSocketAddress;
import com.tc.net.protocol.NetworkStackHarnessFactory;
import com.tc.net.protocol.PlainNetworkStackHarnessFactory;
import com.tc.net.protocol.delivery.OOONetworkStackHarnessFactory;
import com.tc.net.protocol.delivery.OnceAndOnlyOnceProtocolNetworkLayerFactoryImpl;
import com.tc.net.protocol.tcm.ClientMessageChannel;
import com.tc.net.protocol.tcm.CommunicationsManager;
import com.tc.net.protocol.tcm.CommunicationsManagerImpl;
import com.tc.net.protocol.tcm.NetworkListener;
import com.tc.net.protocol.tcm.NullMessageMonitor;
import com.tc.net.protocol.tcm.TCMessageRouterImpl;
import com.tc.net.protocol.transport.ClientConnectionEstablisher;
import com.tc.net.protocol.transport.ClientMessageTransport;
import com.tc.net.protocol.transport.DefaultConnectionIdFactory;
import com.tc.net.protocol.transport.HealthCheckerConfigImpl;
import com.tc.net.protocol.transport.MessageTransport;
import com.tc.net.protocol.transport.NullConnectionPolicy;
import com.tc.net.protocol.transport.ReconnectionRejectedHandlerL1;
import com.tc.net.protocol.transport.TransportHandshakeErrorContext;
import com.tc.net.protocol.transport.TransportHandshakeErrorHandler;
import com.tc.net.protocol.transport.TransportHandshakeErrorNullHandler;
import com.tc.net.protocol.transport.TransportHandshakeMessage;
import com.tc.net.protocol.transport.TransportMessageFactoryImpl;
import com.tc.net.protocol.transport.TransportNetworkStackHarnessFactory;
import com.tc.net.protocol.transport.WireProtocolAdaptorFactoryImpl;
import com.tc.net.proxy.TCPProxy;
import com.tc.object.session.NullSessionManager;
import com.tc.properties.L1ReconnectConfigImpl;
import com.tc.properties.TCPropertiesImpl;
import com.tc.test.TCTestCase;
import com.tc.util.Assert;
import com.tc.util.CallableWaiter;
import com.tc.util.PortChooser;
import com.tc.util.concurrent.ThreadUtil;

import java.net.InetAddress;
import java.util.Collections;
import java.util.concurrent.Callable;

public class TCWorkerCommManagerTest extends TCTestCase {
  private static final int L1_RECONNECT_TIMEOUT = 15000;
  TCLogger          logger               = TCLogging.getLogger(TCWorkerCommManager.class);

  private ClientMessageTransport createClient(String clientName, int serverPort) {
    CommunicationsManager commsMgr = new CommunicationsManagerImpl(clientName + "CommsMgr", new NullMessageMonitor(),
                                                                   new TransportNetworkStackHarnessFactory(),
                                                                   new NullConnectionPolicy());

    final ConnectionInfo connInfo = new ConnectionInfo(TCSocketAddress.LOOPBACK_IP, serverPort);
    ClientConnectionEstablisher cce = new ClientConnectionEstablisher(
                                                                      commsMgr.getConnectionManager(),
                                                                      new ConnectionAddressProvider(
                                                                                                    new ConnectionInfo[] { connInfo }),
                                                                      0, 1000, ReconnectionRejectedHandlerL1.SINGLETON);

    return new ClientMessageTransport(cce, createHandshakeErrorHandler(), new TransportMessageFactoryImpl(),
                                      new WireProtocolAdaptorFactoryImpl(), TransportHandshakeMessage.NO_CALLBACK_PORT);
  }

  private NetworkStackHarnessFactory getNetworkStackHarnessFactory(boolean enableReconnect) {
    NetworkStackHarnessFactory networkStackHarnessFactory;
    if (enableReconnect) {
      networkStackHarnessFactory = new OOONetworkStackHarnessFactory(
                                                                     new OnceAndOnlyOnceProtocolNetworkLayerFactoryImpl(),
                                                                     new L1ReconnectConfigImpl(true,
                                                                                               L1_RECONNECT_TIMEOUT,
                                                                                               5000, 16, 32));
    } else {
      networkStackHarnessFactory = new PlainNetworkStackHarnessFactory();
    }
    return networkStackHarnessFactory;
  }

  @Override
  protected void setUp() throws Exception {
    logger.setLevel(LogLevelImpl.DEBUG);
    super.setUp();
  }

  public void testReaderandWriterCommThread() throws Exception {
    // comms manager with 4 worker comms
    CommunicationsManager commsMgr = new CommunicationsManagerImpl("Server-TestCommsMgr", new NullMessageMonitor(),
                                                                   new TransportNetworkStackHarnessFactory(),
                                                                   new NullConnectionPolicy(), 4);
    NetworkListener listener = commsMgr.createListener(new NullSessionManager(), new TCSocketAddress(0), true,
                                                       new DefaultConnectionIdFactory());
    listener.start(Collections.EMPTY_SET);
    int port = listener.getBindPort();

    ClientMessageTransport client1 = createClient("client1", port);
    ClientMessageTransport client2 = createClient("client2", port);
    ClientMessageTransport client3 = createClient("client3", port);
    ClientMessageTransport client4 = createClient("client4", port);

    client1.open();
    client2.open();
    client3.open();
    client4.open();

    waitForConnected(client1, client2, client3, client4);
    
    waitForWeight(commsMgr, 0, 1);
    waitForWeight(commsMgr, 1, 1);
    waitForWeight(commsMgr, 2, 1);
    waitForWeight(commsMgr, 3, 1);

    for (int i = 0; i < 4; i++) {
      CoreNIOServices workerI = ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWorkerComm(i);
      waitForRead(workerI);
      Assert.eval(workerI.getReaderComm().getTotalBytesWritten() <= 0);

      Assert.eval(workerI.getWriterComm().getTotalBytesRead() <= 0);
      waitForWritten(workerI);

      Assert.eval(workerI.getTotalBytesRead() > 0);
      Assert.eval(workerI.getTotalBytesWritten() > 0);

    }

    listener.stop(5000);
  }

  public void testWorkerCommDistributionAfterClose() throws Exception {
    // comms manager with 3 worker comms
    CommunicationsManager commsMgr = new CommunicationsManagerImpl("Server-TestCommsMgr", new NullMessageMonitor(),
                                                                   getNetworkStackHarnessFactory(false),
                                                                   new NullConnectionPolicy(), 3);
    NetworkListener listener = commsMgr.createListener(new NullSessionManager(), new TCSocketAddress(0), true,
                                                       new DefaultConnectionIdFactory());
    listener.start(Collections.EMPTY_SET);
    int port = listener.getBindPort();

    ClientMessageChannel client1 = createClientMsgCh(port, false);
    ClientMessageChannel client2 = createClientMsgCh(port, false);
    ClientMessageChannel client3 = createClientMsgCh(port, false);

    client1.open();
    client2.open();
    client3.open();

    waitForConnected(client1, client2, client3);

    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2));

    // case 1 :
    // two client closes their connections

    client1.close();
    client2.close();

    waitForTotalWeights(commsMgr, 3, 1);

    ClientMessageChannel client4 = createClientMsgCh(port, false);
    ClientMessageChannel client5 = createClientMsgCh(port, false);

    // two clients open new connection
    client4.open();
    client5.open();

    waitForConnected(client4, client5);

    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2));

    commsMgr.getConnectionManager().closeAllConnections(1000);

    waitForWeight(commsMgr, 0, 0);
    waitForWeight(commsMgr, 1, 0);
    waitForWeight(commsMgr, 2, 0);

    listener.stop(5000);
  }

  public void testWorkerCommDistributionAfterAddMoreWeight() throws Exception {
    // comms manager with 3 worker comms
    CommunicationsManager commsMgr = new CommunicationsManagerImpl("Server-TestCommsMgr", new NullMessageMonitor(),
                                                                   getNetworkStackHarnessFactory(false),
                                                                   new NullConnectionPolicy(), 3);
    NetworkListener listener = commsMgr.createListener(new NullSessionManager(), new TCSocketAddress(0), true,
                                                       new DefaultConnectionIdFactory());
    listener.start(Collections.EMPTY_SET);
    int port = listener.getBindPort();

    ClientMessageChannel client1 = createClientMsgCh(port, false);
    ClientMessageChannel client2 = createClientMsgCh(port, false);
    ClientMessageChannel client3 = createClientMsgCh(port, false);

    client1.open();
    waitForConnected(client1);

    TCConnection conns[] = commsMgr.getConnectionManager().getAllConnections();
    Assert.eval(conns.length == 1);

    client2.open();
    client3.open();

    waitForConnected(client2, client3);

    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2));

    conns[0].addWeight(MessageTransport.CONNWEIGHT_TX_HANDSHAKED);
    ClientMessageChannel client4 = createClientMsgCh(port, false);
    ClientMessageChannel client5 = createClientMsgCh(port, false);

    // four clients open new connection
    client4.open();
    client5.open();

    waitForConnected(client4, client5);

    Assert.assertEquals(2, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0));
    Assert.assertEquals(2, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1));
    Assert.assertEquals(2, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2));

    ClientMessageChannel client6 = createClientMsgCh(port, false);
    ClientMessageChannel client7 = createClientMsgCh(port, false);
    ClientMessageChannel client8 = createClientMsgCh(port, false);

    client6.open();
    client7.open();
    client8.open();

    waitForConnected(client6, client7, client8);

    Assert.assertEquals(3, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0));
    Assert.assertEquals(3, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1));
    Assert.assertEquals(3, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2));

    conns[0].addWeight(MessageTransport.CONNWEIGHT_TX_HANDSHAKED);
    Assert.assertEquals(4, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0));
    client1.close();

    waitForWeight(commsMgr, 0, 1);
    Assert.assertEquals(3, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1));
    Assert.assertEquals(3, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2));

  }

  private ClientMessageChannel createClientMsgCh(int port) {
    return createClientMsgCh(port, true);
  }

  private ClientMessageChannel createClientMsgCh(int port, boolean ooo) {

    CommunicationsManager clientComms = new CommunicationsManagerImpl("Client-TestCommsMgr", new NullMessageMonitor(),
                                                                      getNetworkStackHarnessFactory(ooo),
                                                                      new NullConnectionPolicy());

    ClientMessageChannel clientMsgCh = clientComms
        .createClientChannel(new NullSessionManager(),
                             -1,
                             "localhost",
                             port,
                             1000,
                             new ConnectionAddressProvider(
                                                           new ConnectionInfo[] { new ConnectionInfo("localhost", port) }));
    return clientMsgCh;
  }

  public void testWorkerCommDistributionAfterReconnect() throws Exception {
    // comms manager with 3 worker comms
    CommunicationsManager commsMgr = new CommunicationsManagerImpl("TestCommsMgr", new NullMessageMonitor(),
                                                                   new TCMessageRouterImpl(),
                                                                   getNetworkStackHarnessFactory(true),
                                                                   new NullConnectionPolicy(), 3,
                                                                   new HealthCheckerConfigImpl(TCPropertiesImpl
                                                                       .getProperties()
                                                                       .getPropertiesFor("l2.healthcheck.l1"),
                                                                                               "Test Server"),
                                                                   new ServerID(),
                                                                   new TransportHandshakeErrorNullHandler(),
                                                                   Collections.EMPTY_MAP, Collections.EMPTY_MAP, null);
    NetworkListener listener = commsMgr.createListener(new NullSessionManager(), new TCSocketAddress(0), true,
                                                       new DefaultConnectionIdFactory());
    listener.start(Collections.EMPTY_SET);
    int serverPort = listener.getBindPort();

    int proxyPort = new PortChooser().chooseRandomPort();
    TCPProxy proxy = new TCPProxy(proxyPort, InetAddress.getByName("localhost"), serverPort, 0, false, null);
    proxy.start();

    ClientMessageChannel client1 = createClientMsgCh(proxyPort);
    ClientMessageChannel client2 = createClientMsgCh(proxyPort);
    ClientMessageChannel client3 = createClientMsgCh(proxyPort);

    client1.open();
    client2.open();
    client3.open();

    waitForConnected(client1, client2, client3);

    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2));

    // case 1 : network problems .. both ends getting events
    proxy.stop();

    waitForWeight(commsMgr, 0, 0);
    waitForWeight(commsMgr, 1, 0);
    waitForWeight(commsMgr, 2, 0);

    proxy.start();

    waitForConnected(client1, client2, client3);

    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1));
    Assert.assertEquals(1, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2));

    // case 2: problem with the client side connections .. but server still thinks clients are connected
    proxy.closeClientConnections(true, false);

    System.out.println("XXX waiting for clients to reconnect");
    waitForWeight(commsMgr, 0, 1);
    waitForWeight(commsMgr, 1, 1);
    waitForWeight(commsMgr, 2, 1);

    // case 3: connecting three more clients through server ports

    ClientMessageChannel client4 = createClientMsgCh(serverPort);
    ClientMessageChannel client5 = createClientMsgCh(serverPort);
    ClientMessageChannel client6 = createClientMsgCh(serverPort);

    client4.open();
    client5.open();
    client6.open();

    waitForConnected(client4, client5, client6);

    Assert.assertEquals(2, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0));
    Assert.assertEquals(2, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1));
    Assert.assertEquals(2, ((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2));

    // case 4: closing all connections from server side
    System.out.println("XXX closing all client connections");
    commsMgr.getConnectionManager().closeAllConnections(5000);

    // all clients should reconnect and should be distributed fairly among the worker comms.

    // After connection close and reconnects, the weight balance depends on when comms get the close connection events
    System.out.println("XXX waiting for all clients reconnect");
    waitForTotalWeights(commsMgr, 3, 6);

    // case 5: server detecting long gcs and kicking out the clients
    proxy.setDelay(15 * 1000);

    System.out.println("XXX waiting for HC to kick out the clients those who connected thru proxy ports");
    waitForTotalWeights(commsMgr, 3, 3);

    proxy.setDelay(0);

    ThreadUtil.reallySleep(10000);
    System.out.println("XXX server after seeing client long GC will not open reconnect window for it");
    Assert.assertEquals(3, (((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(0))
                           + (((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(1))
                           + (((TCCommImpl) commsMgr.getConnectionManager().getTcComm()).getWeightForWorkerComm(2)));

    listener.stop(5000);
  }

  private static void waitForConnected(final ClientMessageChannel... channels) throws Exception {
    CallableWaiter.waitOnCallable(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        for (ClientMessageChannel channel : channels) {
          if (!channel.isConnected()) {
            return false;
          }
        }
        return true;
      }
    });
  }

  private static void waitForConnected(final ClientMessageTransport... transports) throws Exception {
    CallableWaiter.waitOnCallable(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        for (ClientMessageTransport transport : transports) {
          if (!transport.isConnected()) {
            return false;
          }
        }
        return true;
      }
    });
  }

  private static void waitForWeight(final CommunicationsManager communicationsManager, final int commId, final int weight) throws Exception {
    CallableWaiter.waitOnCallable(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return ((TCCommImpl)communicationsManager.getConnectionManager()
            .getTcComm()).getWeightForWorkerComm(commId) == weight;
      }
    });
  }

  private static void waitForTotalWeights(final CommunicationsManager communicationsManager, final int workers, final int weight) throws Exception {
    CallableWaiter.waitOnCallable(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        int total = 0;
        for (int i = 0; i < workers; i++) {
          total += ((TCCommImpl)communicationsManager.getConnectionManager()
              .getTcComm()).getWeightForWorkerComm(i);
        }
        return total == weight;
      }
    });
  }

  private static void waitForRead(final CoreNIOServices commThread) throws Exception {
    CallableWaiter.waitOnCallable(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return commThread.getReaderComm().getTotalBytesRead() > 0;
      }
    });
  }

  private static void waitForWritten(final CoreNIOServices commThread) throws Exception {
    CallableWaiter.waitOnCallable(new Callable<Boolean>() {
      @Override
      public Boolean call() throws Exception {
        return commThread.getWriterComm().getTotalBytesWritten() > 0;
      }
    });
  }

  private TransportHandshakeErrorHandler createHandshakeErrorHandler() {
    return new TransportHandshakeErrorHandler() {

      @Override
      public void handleHandshakeError(TransportHandshakeErrorContext e) {
        new ImplementMe(e.toString()).printStackTrace();
      }

    };
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
