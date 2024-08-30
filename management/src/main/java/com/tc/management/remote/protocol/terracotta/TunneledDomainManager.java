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
package com.tc.management.remote.protocol.terracotta;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.management.TunneledDomainUpdater;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.object.config.DSOMBeanConfig;

public class TunneledDomainManager implements TunneledDomainUpdater {

  private static final TCLogger       LOGGER = TCLogging.getLogger(TunneledDomainManager.class);

  private final MessageChannel        channel;

  private final DSOMBeanConfig        config;

  private final TunnelingEventHandler tunnelingEventHandler;

  public TunneledDomainManager(final MessageChannel channel, final DSOMBeanConfig config,
                               final TunnelingEventHandler teh) {
    this.channel = channel;
    this.config = config;
    this.tunnelingEventHandler = teh;
  }

  public void sendCurrentTunneledDomains() {
    if (tunnelingEventHandler.isTunnelingReady()) {
      LOGGER
          .info("Sending current registered tunneled domains to L2 server to set up the tunneled connections for the mbeans that match.");
      TunneledDomainsChanged message = (TunneledDomainsChanged) channel
          .createMessage(TCMessageType.TUNNELED_DOMAINS_CHANGED_MESSAGE);
      message.initialize(this.config.getTunneledDomains());
      message.send();
    } else {
      LOGGER.info("Tunneling isn't ready, not sending the tunneled mbean domains.");
    }
  }
}
