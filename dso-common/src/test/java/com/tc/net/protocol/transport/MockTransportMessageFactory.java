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
package com.tc.net.protocol.transport;

import com.tc.net.core.TCConnection;
import com.tc.util.concurrent.NoExceptionLinkedQueue;

public class MockTransportMessageFactory implements TransportHandshakeMessageFactory {

  public TransportHandshakeMessage    syn;
  public TransportHandshakeMessage    ack;
  public TransportHandshakeMessage    synAck;

  public final NoExceptionLinkedQueue createSynCalls    = new NoExceptionLinkedQueue();
  public final NoExceptionLinkedQueue createAckCalls    = new NoExceptionLinkedQueue();
  public final NoExceptionLinkedQueue createSynAckCalls = new NoExceptionLinkedQueue();

  @Override
  public TransportHandshakeMessage createSyn(ConnectionID connectionId, TCConnection source, short stackLayerFlags,
                                             int callbackPort) {
    createSynCalls.put(new Object[] { connectionId, source });
    return this.syn;
  }

  @Override
  public TransportHandshakeMessage createAck(ConnectionID connectionId, TCConnection source) {
    createAckCalls.put(new CallContext(connectionId, null, source, null, null));
    return this.ack;
  }

  @Override
  public TransportHandshakeMessage createSynAck(ConnectionID connectionId, TCConnection source,
                                                boolean isMaxConnectionsExceeded, int maxConnections, int callbackPort) {
    return createSynAck(connectionId, null, source, isMaxConnectionsExceeded, maxConnections);
  }

  @Override
  public TransportHandshakeMessage createSynAck(ConnectionID connectionId, TransportHandshakeError errorContext,
                                                TCConnection source, boolean isMaxConnectionsExceeded,
                                                int maxConnections) {
    createSynAckCalls.put(new CallContext(connectionId, errorContext, source, new Boolean(isMaxConnectionsExceeded),
                                          new Integer(maxConnections)));
    return this.synAck;
  }

  public static final class CallContext {
    private final ConnectionID            connectionId;
    private final TCConnection            source;
    private final Boolean                 isMaxConnectionsExceeded;
    private final Integer                 maxConnections;
    private final TransportHandshakeError errorContext;

    public CallContext(ConnectionID connectionId, TransportHandshakeError errorContext, TCConnection source,
                       Boolean isMaxConnectionsExceeded, Integer maxConnections) {
      this.connectionId = connectionId;
      this.errorContext = errorContext;
      this.source = source;
      this.isMaxConnectionsExceeded = isMaxConnectionsExceeded;
      this.maxConnections = maxConnections;
    }

    public TransportHandshakeError getErrorContext() {
      return this.errorContext;
    }

    public ConnectionID getConnectionId() {
      return connectionId;
    }

    public Boolean getIsMaxConnectionsExceeded() {
      return isMaxConnectionsExceeded;
    }

    public Integer getMaxConnections() {
      return maxConnections;
    }

    public TCConnection getSource() {
      return source;
    }
  }

}
