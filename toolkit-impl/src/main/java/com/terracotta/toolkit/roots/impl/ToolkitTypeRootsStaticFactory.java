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
package com.terracotta.toolkit.roots.impl;

import com.tc.net.GroupID;
import com.tc.platform.PlatformService;
import com.terracotta.toolkit.object.TCToolkitObject;
import com.terracotta.toolkit.object.ToolkitObjectStripe;
import com.terracotta.toolkit.rejoin.RejoinAwareToolkitObject;
import com.terracotta.toolkit.roots.AggregateToolkitTypeRoot;
import com.terracotta.toolkit.roots.ToolkitTypeRoot;
import com.terracotta.toolkit.roots.ToolkitTypeRootsFactory;
import com.terracotta.toolkit.roots.impl.RootsUtil.RootObjectCreator;
import com.terracotta.toolkit.type.DistributedToolkitType;
import com.terracotta.toolkit.type.DistributedToolkitTypeFactory;
import com.terracotta.toolkit.type.IsolatedToolkitTypeFactory;
import com.terracotta.toolkit.util.collections.WeakValueMapManager;

public class ToolkitTypeRootsStaticFactory implements ToolkitTypeRootsFactory {

  private final WeakValueMapManager manager;

  public ToolkitTypeRootsStaticFactory(WeakValueMapManager manager) {
    this.manager = manager;
  }

  @Override
  public <T extends RejoinAwareToolkitObject, S extends TCToolkitObject> AggregateIsolatedToolkitTypeRoot<T, S> createAggregateIsolatedTypeRoot(String rootName,
                                                                                                                                     IsolatedToolkitTypeFactory<T, S> isolatedTypeFactory,
                                                                                                                                     PlatformService platformService) {
    GroupID[] gids = platformService.getGroupIDs();
    ToolkitTypeRoot<S>[] roots = new ToolkitTypeRoot[gids.length];
    for (int i = 0; i < gids.length; i++) {
      roots[i] = lookupOrCreateRootInGroup(platformService, gids[i], rootName);
    }
    return new AggregateIsolatedToolkitTypeRoot<T, S>(rootName, roots, isolatedTypeFactory,
                                                      manager.createWeakValueMap(), platformService);
  }

  public static ToolkitTypeRoot lookupOrCreateRootInGroup(final PlatformService platformService, GroupID gid,
                                                          String name) {
      return RootsUtil.lookupOrCreateRootInGroup(platformService, gid, name,
                                                 new RootObjectCreator<ToolkitTypeRootImpl>() {
                                                   @Override
                                                   public ToolkitTypeRootImpl create() {
                                                   return new ToolkitTypeRootImpl(platformService);
                                                   }
                                                 });
  }

  @Override
  public <T extends DistributedToolkitType<S>, S extends TCToolkitObject> AggregateToolkitTypeRoot<T, S> createAggregateDistributedTypeRoot(String rootName,
                                                                                                                                            DistributedToolkitTypeFactory<T, S> aggregateToolkitTypeFactory,
                                                                                                                                            PlatformService platformService) {
    GroupID[] gids = platformService.getGroupIDs();
    ToolkitTypeRoot<ToolkitObjectStripe<S>>[] roots = new ToolkitTypeRoot[gids.length];
    for (int i = 0; i < gids.length; i++) {
      roots[i] = lookupOrCreateRootInGroup(platformService, gids[i], rootName);
    }
    return new AggregateDistributedToolkitTypeRoot(rootName, roots, aggregateToolkitTypeFactory,
                                                   manager.createWeakValueMap(),
                                                   platformService);
  }
}
