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

import com.tc.logging.TCLogger;
import com.tc.net.core.TCConnectionManager;

public class TestConnectionHealthCheckerImpl extends ConnectionHealthCheckerImpl {

  public TestConnectionHealthCheckerImpl(HealthCheckerConfig healthCheckerConfig, TCConnectionManager connManager) {
    super(healthCheckerConfig, connManager);
  }

  @Override
  protected HealthCheckerMonitorThreadEngine getHealthMonitorThreadEngine(HealthCheckerConfig config,
                                                                          TCConnectionManager connectionManager,
                                                                          TCLogger loger) {
    return new TestHealthCheckerMonitorThreadEngine(config, connectionManager, loger);
  }

  class TestHealthCheckerMonitorThreadEngine extends HealthCheckerMonitorThreadEngine {

    public TestHealthCheckerMonitorThreadEngine(HealthCheckerConfig healthCheckerConfig,
                                                TCConnectionManager connectionManager, TCLogger logger) {
      super(healthCheckerConfig, connectionManager, logger);
    }

    @Override
    protected ConnectionHealthCheckerContext getHealthCheckerContext(MessageTransportBase transport,
                                                                     HealthCheckerConfig conf,
                                                                     TCConnectionManager connManager) {

      return new TestConnectionHealthCheckerContextImpl(transport, conf, connManager);
    }

  }
}
