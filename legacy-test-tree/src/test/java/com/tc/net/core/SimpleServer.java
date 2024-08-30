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

import com.tc.net.TCSocketAddress;
import com.tc.net.protocol.EchoSink;
import com.tc.net.protocol.GenericNetworkMessageSink;
import com.tc.net.protocol.GenericProtocolAdaptor;
import com.tc.net.protocol.ProtocolAdaptorFactory;
import com.tc.net.protocol.TCProtocolAdaptor;
import com.tc.net.protocol.transport.HealthCheckerConfigImpl;

import java.io.IOException;

/**
 * A simple server instance that accepts GenericNetwork messages and delivers them to a sink
 * 
 * @author teck
 */
public class SimpleServer {
  private final GenericNetworkMessageSink sink;
  private final TCConnectionManager       connMgr;
  private final int                       port;
  private TCListener                      lsnr;

  public SimpleServer(GenericNetworkMessageSink sink) {
    this(sink, 0, 0);
  }

  public SimpleServer(GenericNetworkMessageSink sink, int port) {
    this(sink, port, 0);
  }

  public SimpleServer(GenericNetworkMessageSink sink, int port, int serverThreadCount) {
    this.sink = sink;
    this.port = port;
    this.connMgr = new TCConnectionManagerImpl("TestConnMgr", serverThreadCount,
                                                new HealthCheckerConfigImpl("DefaultConfigForActiveConnections"), null);
  }

  public TCConnectionManager getConnectionManager() {
    return this.connMgr;
  }

  public void start() throws IOException {
    TCSocketAddress addr = new TCSocketAddress(TCSocketAddress.WILDCARD_ADDR, port);

    ProtocolAdaptorFactory factory = new ProtocolAdaptorFactory() {
      @Override
      public TCProtocolAdaptor getInstance() {
        GenericProtocolAdaptor rv = new GenericProtocolAdaptor(sink);
        return rv;
      }
    };

    lsnr = connMgr.createListener(addr, factory, 4096, true);
  }

  public TCSocketAddress getServerAddr() {
    return lsnr.getBindSocketAddress();
  }

  public void stop() {
    if (lsnr != null) {
      lsnr.stop();
    }

    connMgr.shutdown();
  }

  private static void usage() {
    System.err.println("usage: SimpleServer <port> <verify>");
    System.exit(1);
  }

  public static void main(String args[]) throws Exception {
    if (args.length > 2) {
      usage();
    }

    int p = 0;
    boolean verify = false;

    if (args.length > 0) {
      p = Integer.parseInt(args[0]);
    }

    if (args.length > 1) {
      verify = Boolean.valueOf(args[1]).booleanValue();
    }

    SimpleServer server = new SimpleServer(new EchoSink(verify), p);
    server.start();
    System.out.println("Server started at: " + server.getServerAddr());

    Thread.sleep(Long.MAX_VALUE);
  }

}