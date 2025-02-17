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
package com.tc.net.protocol.delivery;

import com.tc.properties.ReconnectConfig;

import java.util.Timer;

/**
 * Creates new instances of OnceAndOnlyOnceProtocolNetworkLayers. This is used so that a mock one may be injected into
 * the once and only once network stack harness for testing.
 */
public class OnceAndOnlyOnceProtocolNetworkLayerFactoryImpl implements OnceAndOnlyOnceProtocolNetworkLayerFactory {

  public static final String RESTORE_TIMERTHREAD_NAME = "OOO Connection Restore Timer";
  private Timer              restoreConnectTimer      = null;

  @Override
  public synchronized OnceAndOnlyOnceProtocolNetworkLayer createNewClientInstance(ReconnectConfig reconnectConfig) {
    OOOProtocolMessageFactory messageFactory = new OOOProtocolMessageFactory();
    OOOProtocolMessageParser messageParser = new OOOProtocolMessageParser(messageFactory);
    return new OnceAndOnlyOnceProtocolNetworkLayerImpl(messageFactory, messageParser, reconnectConfig, true);
  }

  @Override
  public synchronized OnceAndOnlyOnceProtocolNetworkLayer createNewServerInstance(ReconnectConfig reconnectConfig) {
    // ooo connection restore timers are needed only for servers
    if (restoreConnectTimer == null) {
      restoreConnectTimer = new Timer(RESTORE_TIMERTHREAD_NAME, true);
    }

    OOOProtocolMessageFactory messageFactory = new OOOProtocolMessageFactory();
    OOOProtocolMessageParser messageParser = new OOOProtocolMessageParser(messageFactory);
    return new OnceAndOnlyOnceProtocolNetworkLayerImpl(messageFactory, messageParser, reconnectConfig, false,
                                                       restoreConnectTimer);
  }
}
