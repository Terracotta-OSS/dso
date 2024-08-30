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
package com.tc.client;

import com.tc.abortable.AbortableOperationManager;
import com.tc.lang.TCThreadGroup;
import com.tc.license.ProductID;
import com.tc.net.core.security.TCSecurityManager;
import com.tc.object.DistributedObjectClient;
import com.tc.object.config.DSOClientConfigHelper;
import com.tc.object.config.PreparedComponentsFromL2Connection;
import com.tc.object.loaders.ClassProvider;
import com.tc.platform.rejoin.RejoinManagerInternal;
import com.tc.util.UUID;
import com.tc.util.factory.AbstractFactory;
import com.tcclient.cluster.DsoClusterInternal;

import java.util.Map;

public abstract class AbstractClientFactory extends AbstractFactory {
  private static String FACTORY_SERVICE_ID            = "com.tc.client.ClientFactory";
  private static Class  STANDARD_CLIENT_FACTORY_CLASS = StandardClientFactory.class;

  public static AbstractClientFactory getFactory() {
    return (AbstractClientFactory) getFactory(FACTORY_SERVICE_ID, STANDARD_CLIENT_FACTORY_CLASS);
  }

  public abstract DistributedObjectClient createClient(DSOClientConfigHelper config, TCThreadGroup threadGroup,
                                                       ClassProvider classProvider,
                                                       PreparedComponentsFromL2Connection connectionComponents,
                                                       DsoClusterInternal dsoCluster,
                                                       TCSecurityManager securityManager,
                                                       AbortableOperationManager abortableOperationManager,
                                                       RejoinManagerInternal rejoinManager, UUID uuid,
                                                       ProductID productId);

  public abstract TCSecurityManager createClientSecurityManager(Map<String, Object> env);
}
