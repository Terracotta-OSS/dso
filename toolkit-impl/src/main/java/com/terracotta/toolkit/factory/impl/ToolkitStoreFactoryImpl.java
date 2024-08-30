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
package com.terracotta.toolkit.factory.impl;

import org.terracotta.toolkit.ToolkitObjectType;
import org.terracotta.toolkit.internal.ToolkitInternal;

import com.terracotta.toolkit.collections.map.ServerMap;
import com.terracotta.toolkit.collections.map.ToolkitCacheImpl;
import com.terracotta.toolkit.factory.ToolkitFactoryInitializationContext;
import com.terracotta.toolkit.roots.impl.ToolkitTypeConstants;

public class ToolkitStoreFactoryImpl extends AbstractPrimaryToolkitObjectFactory<ToolkitCacheImpl, ServerMap> {

  private ToolkitStoreFactoryImpl(ToolkitInternal toolkit, ToolkitFactoryInitializationContext context) {
    super(toolkit, context.getToolkitTypeRootsFactory().createAggregateDistributedTypeRoot(
        ToolkitTypeConstants.TOOLKIT_STORE_ROOT_NAME, new ToolkitStoreDistributedTypeFactory(
        context.getSearchFactory(), context.getServerMapLocalStoreFactory()), context.getPlatformService()));
  }

  public static ToolkitStoreFactoryImpl newToolkitStoreFactory(ToolkitInternal toolkit,
                                                               ToolkitFactoryInitializationContext context) {
    return new ToolkitStoreFactoryImpl(toolkit, context);
  }

  @Override
  public ToolkitObjectType getManufacturedToolkitObjectType() {
    return ToolkitObjectType.STORE;
  }

}
