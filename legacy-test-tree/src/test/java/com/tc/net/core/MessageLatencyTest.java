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

import com.tc.bytes.TCByteBuffer;
import com.tc.bytes.TCByteBufferFactory;
import com.tc.net.TCSocketAddress;
import com.tc.net.protocol.GenericNetworkMessage;
import com.tc.net.protocol.GenericNetworkMessageSink;
import com.tc.net.protocol.GenericProtocolAdaptor;
import com.tc.util.runtime.Os;

import java.util.Arrays;

import junit.framework.TestCase;

/**
 * @author teck
 */
public class MessageLatencyTest extends TestCase {
  private static final int     NUM_MSGS   = 10000;
  private static final double  PERCENTILE = 95;

  private TCConnectionManager  connMgr;
  private SimpleServer         server;
  private final Object         lock       = new Object();
  private final RecordTimeSink serversink = new RecordTimeSink(true);
  private final RecordTimeSink clientsink = new RecordTimeSink(false);

  @Override
  protected void setUp() throws Exception {
    connMgr = new TCConnectionManagerImpl();
    server = new SimpleServer(serversink);
    server.start();
  }

  @Override
  protected void tearDown() throws Exception {
    connMgr.shutdown();
    server.stop();
  }

  public static void main(String args[]) throws Exception {
    MessageLatencyTest test = new MessageLatencyTest();
    test.setUp();
    test.testLatency();
    test.tearDown();
  }

  public void testLatency() throws Exception {
    final TCConnection conn = connMgr.createConnection(new GenericProtocolAdaptor(clientsink));
    conn.connect(new TCSocketAddress(server.getServerAddr().getPort()), 3000);

    for (int i = 0; i < NUM_MSGS; i++) {
      TCByteBuffer data = TCByteBufferFactory.getInstance(false, 8);

      final long sent = System.currentTimeMillis();
      data.putLong(sent);
      data.position(0);

      final GenericNetworkMessage msg = new GenericNetworkMessage(conn, data);

      synchronized (lock) {
        conn.putMessage(msg);
        lock.wait();
      }
    }

    Arrays.sort(serversink.recvTimes);
    Arrays.sort(clientsink.recvTimes);

    final int withinPercentile = (int) (NUM_MSGS * (PERCENTILE / 100));

    long serversum = 0;
    for (int i = 0; i < withinPercentile; i++) {
      serversum += serversink.recvTimes[i];
    }

    long clientsum = 0;
    for (int i = 0; i < withinPercentile; i++) {
      clientsum += clientsink.recvTimes[i];
    }

    // Account for System.currentTimeMillis() resolution suckage on windows
    final double benchmark = Os.isWindows() ? 2.0 : 1.0;
    final double serveravg = ((double) serversum / (double) withinPercentile);
    final double clientavg = ((double) clientsum / (double) withinPercentile);

    System.out.println("Client->Server Average Latency: " + serveravg + " milliseconds");
    System.out.println("Server->Client Average Latency: " + clientavg + " milliseconds");

    assertTrue("Server Avergage latency is not in acceptable range (" + serveravg + " > " + benchmark + ")",
               (serveravg <= benchmark));
    assertTrue("Client Avergage latency is not in acceptable range (" + clientavg + " > " + benchmark + ")",
               (clientavg <= benchmark));
  }

  class RecordTimeSink implements GenericNetworkMessageSink {
    final boolean respond;
    int           ctr         = 0;
    long          recvTimes[] = new long[NUM_MSGS];

    RecordTimeSink(boolean respond) {
      this.respond = respond;
    }

    @Override
    public void putMessage(GenericNetworkMessage msg) {
      long recvTime = System.currentTimeMillis();
      long sentTime = msg.getPayload()[0].getLong();

      long diff = recvTime - sentTime;

      if ((ctr % 100) == 0) {
        System.out.println("Latency was " + diff + " milliseconds");
      }

      recvTimes[ctr++] = diff;

      TCByteBuffer data = TCByteBufferFactory.getInstance(false, 8);

      final long sent = System.currentTimeMillis();
      data.putLong(sent);
      data.position(0);

      if (respond) {
        final GenericNetworkMessage response = new GenericNetworkMessage(msg.getSource(), data);
        msg.getSource().putMessage(response);
      } else {
        synchronized (lock) {
          lock.notify();
        }
      }
    }
  }

}