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
import org.terracotta.toolkit.config.Configuration;
import org.terracotta.toolkit.internal.ToolkitInternal;

import com.tc.platform.PlatformService;
import com.terracotta.toolkit.collections.DestroyableToolkitList;
import com.terracotta.toolkit.collections.ToolkitListImpl;
import com.terracotta.toolkit.factory.ToolkitFactoryInitializationContext;
import com.terracotta.toolkit.factory.ToolkitObjectFactory;
import com.terracotta.toolkit.roots.impl.ToolkitTypeConstants;
import com.terracotta.toolkit.type.IsolatedClusteredObjectLookup;
import com.terracotta.toolkit.type.IsolatedToolkitTypeFactory;

/**
 * An implementation of {@link ToolkitObjectFactory}
 */
public class ToolkitListFactoryImpl extends
    AbstractPrimaryToolkitObjectFactory<DestroyableToolkitList, ToolkitListImpl> {

  public ToolkitListFactoryImpl(ToolkitInternal toolkit, ToolkitFactoryInitializationContext context) {
    super(toolkit, context.getToolkitTypeRootsFactory()
        .createAggregateIsolatedTypeRoot(ToolkitTypeConstants.TOOLKIT_LIST_ROOT_NAME, new ListIsolatedTypeFactory(context.getPlatformService()),
                                         context.getPlatformService()));
  }

  @Override
  public ToolkitObjectType getManufacturedToolkitObjectType() {
    return ToolkitObjectType.LIST;
  }

  private static class ListIsolatedTypeFactory implements
      IsolatedToolkitTypeFactory<DestroyableToolkitList, ToolkitListImpl> {
    private final PlatformService platformService;

    ListIsolatedTypeFactory(PlatformService platformService) {
      this.platformService = platformService;
    }

    @Override
    public DestroyableToolkitList createIsolatedToolkitType(ToolkitObjectFactory<DestroyableToolkitList> factory,
                                                            IsolatedClusteredObjectLookup<ToolkitListImpl> lookup,
                                                            String name, Configuration config,
                                                            ToolkitListImpl tcClusteredObject) {
      return new DestroyableToolkitList(factory, lookup, tcClusteredObject, name, platformService);
    }

    @Override
    public ToolkitListImpl createTCClusteredObject(Configuration config) {
      return new ToolkitListImpl(platformService);
    }
  }

}
