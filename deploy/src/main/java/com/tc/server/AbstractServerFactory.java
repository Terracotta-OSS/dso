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
package com.tc.server;

import com.tc.config.schema.setup.L2ConfigurationSetupManager;
import com.tc.lang.TCThreadGroup;
import com.tc.util.factory.AbstractFactory;

public abstract class AbstractServerFactory extends AbstractFactory {
  private static String FACTORY_SERVICE_ID            = "com.tc.server.ServerFactory";
  private static Class  STANDARD_SERVER_FACTORY_CLASS = StandardServerFactory.class;

  public static AbstractServerFactory getFactory() {
    return (AbstractServerFactory) getFactory(FACTORY_SERVICE_ID, STANDARD_SERVER_FACTORY_CLASS);
  }

  public abstract TCServer createServer(L2ConfigurationSetupManager configurationSetupManager,
                                        TCThreadGroup threadGroup);
}
