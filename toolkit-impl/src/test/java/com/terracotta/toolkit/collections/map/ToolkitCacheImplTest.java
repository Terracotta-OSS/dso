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
package com.terracotta.toolkit.collections.map;

import com.tc.cluster.DsoCluster;
import com.tc.cluster.DsoClusterTopology;
import com.tc.object.ObjectID;
import com.tc.object.TCObject;
import com.tc.object.TCObjectServerMap;
import com.tc.platform.PlatformService;
import com.tc.properties.TCProperties;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;
import com.tc.util.concurrent.ScheduledNamedTaskRunner;
import com.tcclient.cluster.DsoNode;
import com.terracotta.toolkit.TerracottaToolkit;
import com.terracotta.toolkit.abortable.ToolkitAbortableOperationException;
import com.terracotta.toolkit.collections.DestroyableToolkitMap;
import com.terracotta.toolkit.collections.ToolkitSetImpl;
import com.terracotta.toolkit.factory.ToolkitObjectFactory;
import com.terracotta.toolkit.factory.impl.AbstractPrimaryToolkitObjectFactory;
import com.terracotta.toolkit.object.serialization.SerializationStrategy;
import com.terracotta.toolkit.type.IsolatedClusteredObjectLookup;
import org.junit.Test;
import org.terracotta.toolkit.collections.ToolkitSet;
import org.terracotta.toolkit.config.Configuration;
import org.terracotta.toolkit.internal.ToolkitInternal;
import org.terracotta.toolkit.store.ToolkitConfigFields;

import java.util.ArrayList;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ToolkitCacheImplTest {
  private AggregateServerMap<Integer, String> aggregateServerMap;
  private ToolkitCacheImpl<Integer, String> toolkitCache;

  // This test exists because of the defect reported in TAB-7292.
  // The state of whether the cache is buffering is held in multiple objects:
  // ToolkitCacheImpl, BulkLoadToolkitCache and LocalBufferedMap
  // If an exception is thrown because the server is unavailable and non-stop
  // is turned on, this could leave the state across these object inconsistent.
  @Test
  public void noRaceInSetNodeBulkLoadEnabled() {
    setUpToolkitCache();

    ToolkitAbortableOperationException exception = new ToolkitAbortableOperationException();
    doThrow(exception).doNothing().when(aggregateServerMap).setConfigField(ToolkitConfigFields.LOCAL_CACHE_ENABLED_FIELD_NAME, true);

    assertFalse(toolkitCache.isBulkLoadEnabled());
    toolkitCache.setNodeBulkLoadEnabled(true);
    assertTrue(toolkitCache.isBulkLoadEnabled());

    try {
      toolkitCache.setNodeBulkLoadEnabled(false);
      fail("Expected setNodeBulkLoadEnabled to throw - that's the point of the test");
    } catch (ToolkitAbortableOperationException e) {
      // This was expected because we made AggregateServerMap.setConfigField throw an exception
    }

    assertTrue(toolkitCache.isBulkLoadEnabled());
    toolkitCache.isEmpty();
    verify(aggregateServerMap, never()).isEmpty();

    toolkitCache.setNodeBulkLoadEnabled(true);
    toolkitCache.setNodeBulkLoadEnabled(false);

    assertFalse(toolkitCache.isBulkLoadEnabled());
    toolkitCache.isEmpty();
    verify(aggregateServerMap, atLeastOnce()).isEmpty();
  }

  private void setUpToolkitCache() {
    aggregateServerMap = mock(AggregateServerMap.class);

    ToolkitObjectFactory toolkitObjectFactory = mock(AbstractPrimaryToolkitObjectFactory.class);
    PlatformService platformService = mock(PlatformService.class);
    ToolkitInternal toolkit = mock(ToolkitInternal.class);
    Configuration configuration = mock(Configuration.class);

    when(aggregateServerMap.getConfiguration()).thenReturn(configuration);
    when(configuration.getBoolean(ToolkitConfigFields.LOCAL_CACHE_ENABLED_FIELD_NAME)).thenReturn(true);

    InternalToolkitMap<Integer, String> serverMap = mock(InternalToolkitMap.class);
    TCObjectServerMap tcObject1 = mock(TCObjectServerMap.class);
    when(tcObject1.getResolveLock()).thenReturn(new Object());
    when(tcObject1.getObjectID()).thenReturn(new ObjectID(1L));
    when(serverMap.__tc_managed()).thenReturn(tcObject1);
    when(aggregateServerMap.getAnyServerMap()).thenReturn(serverMap);

    SerializationStrategy serializer = mock(SerializationStrategy.class);
    when(platformService.lookupRegisteredObjectByName(TerracottaToolkit.TOOLKIT_SERIALIZER_REGISTRATION_NAME, SerializationStrategy.class)).thenReturn(serializer);

    DsoNode currentClusterNode = mock(DsoNode.class);
    when(currentClusterNode.getId()).thenReturn("currentNode");
    DsoCluster dsoCluster = mock(DsoCluster.class);
    DsoClusterTopology topology = mock(DsoClusterTopology.class);
    when(dsoCluster.getClusterTopology()).thenReturn(topology);
    ArrayList<DsoNode> clusterNodes = new ArrayList<DsoNode>(1);
    clusterNodes.add(currentClusterNode);
    when(topology.getNodes()).thenReturn(clusterNodes);
    when(platformService.getDsoCluster()).thenReturn(dsoCluster);
    when(dsoCluster.getCurrentNode()).thenReturn(currentClusterNode);

    when(platformService.getTaskRunner()).thenReturn(new ScheduledNamedTaskRunner(2));

    TCProperties properties = TCPropertiesImpl.getProperties();
    properties.setProperty(TCPropertiesConsts.TOOLKIT_BULKLOAD_LOGGING_ENABLED, "true");
    when(platformService.getTCProperties()).thenReturn(properties);

    IsolatedClusteredObjectLookup<ToolkitMapImpl> lookup = mock(IsolatedClusteredObjectLookup.class);
    ToolkitMapImpl<String, Integer> map = new ToolkitMapImpl<String, Integer>(platformService);
    TCObject tcObject2 = mock(TCObject.class);
    when(tcObject2.getResolveLock()).thenReturn(new Object());
    when(tcObject2.getObjectID()).thenReturn(new ObjectID(2L));
    map.__tc_managed(tcObject2);
    DestroyableToolkitMap<String, Integer> destroyableToolkitMap = new DestroyableToolkitMap<String, Integer>(toolkitObjectFactory, lookup, map, "name", platformService);
    ToolkitSet<String> toolkitSet = new ToolkitSetImpl<String>(destroyableToolkitMap, platformService);
    when(toolkit.getSet("__tc_bulk-load-nodes-set_for_cache_name", String.class)).thenReturn(toolkitSet);

    toolkitCache = new ToolkitCacheImpl<Integer, String>(toolkitObjectFactory, "name", aggregateServerMap, platformService, toolkit);
  }
}
