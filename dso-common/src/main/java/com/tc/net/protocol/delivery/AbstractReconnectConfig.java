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
package com.tc.net.protocol.delivery;

import com.tc.exception.TCRuntimeException;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.properties.ReconnectConfig;

public class AbstractReconnectConfig implements ReconnectConfig {

  private final String          name;
  private final boolean         reconnectEnabled;
  private final int             reconnectTimeout;
  private final int             reconnectSendQueueCap;
  private final int             reconnectMaxDelayedAcks;
  private final int             reconnectSendWindow;
  private static final TCLogger logger = TCLogging.getLogger(AbstractReconnectConfig.class);

  public AbstractReconnectConfig(boolean reconnectEnabled, int reconnectTimeout, int reconnectSendQueueCap,
                                 int reconnectMaxDelayedAcks, int reconnectSendWindow, String name) {
    this.name = name;
    this.reconnectEnabled = reconnectEnabled;
    this.reconnectTimeout = reconnectTimeout;
    this.reconnectSendQueueCap = reconnectSendQueueCap;
    this.reconnectMaxDelayedAcks = reconnectMaxDelayedAcks;
    this.reconnectSendWindow = (reconnectSendWindow > 0 ? reconnectSendWindow : 0);
    validateConfig();
  }

  private void validateConfig() {

    if (reconnectMaxDelayedAcks <= 0) { throw new TCRuntimeException(
                                                                     name
                                                                         + " reconnectMaxDelayedAcks should be greater than 0"); }

    if (reconnectSendWindow <= 0) {
      logger.warn(name + " reconnectSendWindow is 0; Message Sender might not throttle for peer node respoonse");
    }

    if (reconnectMaxDelayedAcks >= reconnectSendWindow) { throw new TCRuntimeException(
                                                                                       name
                                                                                           + " : reconnectMaxDelayedAcks should be lesser than reconnectSendWindow"); }
  }

  @Override
  public boolean getReconnectEnabled() {
    return reconnectEnabled;
  }

  @Override
  public int getReconnectTimeout() {
    return reconnectTimeout;
  }

  @Override
  public int getSendQueueCapacity() {
    return reconnectSendQueueCap;
  }

  @Override
  public int getMaxDelayAcks() {
    return reconnectMaxDelayedAcks;
  }

  @Override
  public int getSendWindow() {
    return reconnectSendWindow;
  }

}
