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
package com.tc.properties;

import com.tc.net.protocol.delivery.AbstractReconnectConfig;

public class L1ReconnectConfigImpl extends AbstractReconnectConfig {

  private static final String NAME = "L2->L1 Reconnect Config";

  public L1ReconnectConfigImpl() {
    super(TCPropertiesImpl.getProperties().getBoolean(TCPropertiesConsts.L2_L1RECONNECT_ENABLED), TCPropertiesImpl
        .getProperties().getInt(TCPropertiesConsts.L2_L1RECONNECT_TIMEOUT_MILLS), TCPropertiesImpl.getProperties()
        .getInt(TCPropertiesConsts.L2_L1RECONNECT_SENDQUEUE_CAP), TCPropertiesImpl.getProperties()
        .getInt(TCPropertiesConsts.L2_L1RECONNECT_MAX_DELAYEDACKS), TCPropertiesImpl.getProperties()
        .getInt(TCPropertiesConsts.L2_L1RECONNECT_SEND_WINDOW), NAME);
  }

  public L1ReconnectConfigImpl(boolean l1ReconnectEnabled, int l1ReconnectTimeout, int l1ReconnectSendQueueCap,
                               int l1ReconnectMaxDelayedAcks, int l1ReconnectSendWindow) {
    super(l1ReconnectEnabled, l1ReconnectTimeout, l1ReconnectSendQueueCap, l1ReconnectMaxDelayedAcks,
          l1ReconnectSendWindow, NAME);
  }

}
