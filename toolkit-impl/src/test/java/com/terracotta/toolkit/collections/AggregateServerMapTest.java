/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
package com.terracotta.toolkit.collections;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import com.tc.exception.TCNotRunningException;
import com.tc.logging.TCLogging;
import com.tc.search.SearchRequestID;
import com.tc.server.ServerEvent;
import com.tc.server.ServerEventType;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.terracotta.toolkit.ToolkitObjectType;
import org.terracotta.toolkit.builder.ToolkitCacheConfigBuilder;
import org.terracotta.toolkit.cache.ToolkitCacheListener;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.config.Configuration;
import org.terracotta.toolkit.internal.cache.VersionedValue;
import org.terracotta.toolkit.rejoin.RejoinException;
import org.terracotta.toolkit.search.ToolkitSearchQuery;
import org.terracotta.toolkit.store.ToolkitConfigFields;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SetMultimap;
import com.tc.object.ObjectID;
import com.tc.object.TCObjectServerMap;
import com.tc.platform.PlatformService;
import com.tc.properties.TCPropertiesImpl;
import com.tc.util.concurrent.TaskRunner;
import com.terracotta.toolkit.bulkload.BufferedOperation;
import com.terracotta.toolkit.collections.map.AggregateServerMap;
import com.terracotta.toolkit.collections.map.InternalToolkitMap;
import com.terracotta.toolkit.collections.map.ServerMap;
import com.terracotta.toolkit.collections.map.ValuesResolver;
import com.terracotta.toolkit.collections.map.VersionedValueImpl;
import com.terracotta.toolkit.collections.servermap.api.ServerMapLocalStore;
import com.terracotta.toolkit.collections.servermap.api.ServerMapLocalStoreConfig;
import com.terracotta.toolkit.collections.servermap.api.ServerMapLocalStoreFactory;
import com.terracotta.toolkit.object.ToolkitObjectStripe;
import com.terracotta.toolkit.object.ToolkitObjectStripeImpl;
import com.terracotta.toolkit.search.SearchExecutor;
import com.terracotta.toolkit.search.SearchFactory;
import com.terracotta.toolkit.type.DistributedClusteredObjectLookup;
import com.terracotta.toolkit.util.ImmediateTimer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyBoolean;
import static org.mockito.Mockito.anyMap;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author tim
 */
public class AggregateServerMapTest {

  private PlatformService platformService;
  private ServerMapLocalStoreFactory serverMapLocalStoreFactory;
  private Configuration configuration;
  private ListAppender<ILoggingEvent> listAppender;

  @Before
  public void setUp() throws Exception {
    TaskRunner taskRunner = mock(TaskRunner.class);
    when(taskRunner.newTimer()).thenReturn(new ImmediateTimer());
    when(taskRunner.newTimer(anyString())).thenReturn(new ImmediateTimer());

    platformService = mock(PlatformService.class);
    when(platformService.getTaskRunner()).thenReturn(taskRunner);
    when(platformService.getTCProperties()).thenReturn(TCPropertiesImpl.getProperties());
    LoggerContext context = TCLogging.getLoggerContext();
    listAppender = new ListAppender<>();
    listAppender.setContext(context);
    listAppender.start();
    context.getLogger(AggregateServerMap.class).addAppender(listAppender);
    ServerMapLocalStore serverMapLocalStore = mock(ServerMapLocalStore.class);
    serverMapLocalStoreFactory = mock(ServerMapLocalStoreFactory.class);
    when(serverMapLocalStoreFactory.getOrCreateServerMapLocalStore(any(ServerMapLocalStoreConfig.class))).thenReturn(serverMapLocalStore);

    configuration = new ToolkitCacheConfigBuilder().consistency(ToolkitConfigFields.Consistency.EVENTUAL).build();
  }

  @Test
  public void testDrain() throws Exception {
    final List<ServerMap> serverMapList = mockServerMaps(256);
    ToolkitObjectStripe[] stripeObjects = createObjectStripes(configuration, serverMapList, 64);

    AggregateServerMap<String, String> asm = new AggregateServerMap<String, String>(ToolkitObjectType.CACHE, mock(SearchFactory.class),
        mock(DistributedClusteredObjectLookup.class), "foo", stripeObjects, configuration,
        mock(Callable.class), serverMapLocalStoreFactory, platformService, mock(ToolkitLock.class));

    Map<String, BufferedOperation<String>> allOps = new HashMap<String, BufferedOperation<String>>();
    Map<Integer, Map<String, BufferedOperation<String>>> batchedOps = new HashMap<Integer, Map<String, BufferedOperation<String>>>();
    for (int i = 0; i < 1000; i++) {
      String key = UUID.randomUUID().toString();
      BufferedOperation<String> bufferedOperation = mock(BufferedOperation.class);
      allOps.put(key, bufferedOperation);
      Map<String, BufferedOperation<String>> batch = batchedOps.get(Math.abs(key.hashCode() % 256));
      if (batch == null) {
        batch = new HashMap<String, BufferedOperation<String>>();
        batchedOps.put(Math.abs(key.hashCode() % 256), batch);
      }
      batch.put(key, bufferedOperation);
    }

    asm.drain(allOps);
    for (Map.Entry<Integer, Map<String, BufferedOperation<String>>> entry : batchedOps.entrySet()) {
      verify(serverMapList.get(entry.getKey())).drain(entry.getValue());
    }
  }

  @Test
  public void testRejoinDuringDrain() throws Exception {
    final ServerMap serverMap = mockServerMap(1);
    ToolkitObjectStripe[] stripeObjects = createObjectStripes(configuration, Collections.singletonList(serverMap), 1);

    AggregateServerMap<String, String> asm = new AggregateServerMap<String, String>(ToolkitObjectType.CACHE, mock(SearchFactory.class),
        mock(DistributedClusteredObjectLookup.class), "foo", stripeObjects, configuration,
        mock(Callable.class), serverMapLocalStoreFactory, platformService, mock(ToolkitLock.class));

    doThrow(new RejoinException()).when(serverMap).drain(anyMap());

    Map<String, BufferedOperation<String>> op = Collections.singletonMap("foo",
        (BufferedOperation<String>) mock(BufferedOperation.class));

    // Check that we're ignoring the rejoin exception.
    asm.drain(op);
  }

  @Test
  public void testCreateBufferedOperation() throws Exception {
    final List<ServerMap> serverMapList = mockServerMaps(256);
    ToolkitObjectStripe[] stripeObjects = createObjectStripes(configuration, serverMapList, 64);

    AggregateServerMap<String, String> asm = new AggregateServerMap<String, String>(ToolkitObjectType.CACHE, mock(SearchFactory.class),
        mock(DistributedClusteredObjectLookup.class), "foo", stripeObjects, configuration,
        mock(Callable.class), serverMapLocalStoreFactory, platformService, mock(ToolkitLock.class));

    asm.createBufferedOperation(BufferedOperation.Type.REMOVE, "a", "b", 1, 2, 3, 4);
    verify(serverMapList.get(Math.abs("a".hashCode() % 256))).createBufferedOperation(BufferedOperation.Type.REMOVE, "a", "b", 1, 2, 3, 4);
  }

  @Test
  public void testGetAllVersioned() throws Exception {
    final List<ServerMap> serverMapList = mockServerMaps(2);
    ToolkitObjectStripe[] stripeObjects = createObjectStripes(configuration, serverMapList, 1);

    AggregateServerMap<String, String> asm = new AggregateServerMap<String, String>(ToolkitObjectType.CACHE, mock(SearchFactory.class),
        mock(DistributedClusteredObjectLookup.class), "foo", stripeObjects, configuration,
        mock(Callable.class), serverMapLocalStoreFactory, platformService, mock(ToolkitLock.class)) {
      @Override
      protected TCObjectServerMap getAnyTCObjectServerMap() {
        return (TCObjectServerMap) serverMapList.get(0).__tc_managed();
      }

      @Override
      protected InternalToolkitMap<String, String> getAnyServerMap() {
        return serverMapList.get(0);
      }
    };

    SetMultimap<ObjectID, String> getAllRequest = HashMultimap.create();
    getAllRequest.put(new ObjectID(0), "foo");
    getAllRequest.put(new ObjectID(1), "bar");

    Map<String, VersionedValue<String>> expectedResult = Maps.newHashMap();
    expectedResult.put("foo", new VersionedValueImpl<String>("foo", 10));
    expectedResult.put("bar", new VersionedValueImpl<String>("bar", 12));
    when(serverMapList.get(0).getAllVersioned(getAllRequest)).thenReturn(expectedResult);

    // "foo".hashCode() is even, "bar".hashCode() is odd, neat.
    Map<String, VersionedValue<String>> result = asm.getAllVersioned(Arrays.asList("foo", "bar"));

    verify(serverMapList.get(0)).getAllVersioned(getAllRequest);
    assertThat(result, is(expectedResult));
  }

  private List<ServerMap> mockServerMaps(int number) {
    List<ServerMap> serverMaps = new ArrayList<ServerMap>();
    for (int i = 0; i < number; i++) {
      serverMaps.add(mockServerMap(i));
    }
    return serverMaps;
  }

  private ToolkitObjectStripe[] createObjectStripes(Configuration configuration, List<ServerMap> serverMaps, int mapsPerStripe) {
    List<ToolkitObjectStripe<ServerMap>> toolkitObjectStripes = new ArrayList<ToolkitObjectStripe<ServerMap>>();
    for (List<ServerMap> maps : Lists.partition(serverMaps, mapsPerStripe)) {
      toolkitObjectStripes.add(new ToolkitObjectStripeImpl<ServerMap>(configuration, maps.toArray(new ServerMap[maps.size()])));
    }
    return toolkitObjectStripes.toArray(new ToolkitObjectStripe[toolkitObjectStripes.size()]);
  }

  private ServerMap mockServerMap(long oid) {
    ServerMap serverMap = mock(ServerMap.class);
    TCObjectServerMap tcObject = mock(TCObjectServerMap.class);
    when(tcObject.getObjectID()).thenReturn(new ObjectID(oid));
    when(serverMap.__tc_managed()).thenReturn(tcObject);
    return serverMap;
  }

  @Test
  public void testDoHandleEvictionsTCNotRunningExceptionLogging() {
    final List<ServerMap> serverMapList = mockServerMaps(256);
    ToolkitObjectStripe[] stripeObjects = createObjectStripes(configuration, serverMapList, 64);
    AggregateServerMap<String, String> asm = new AggregateServerMap<String, String>(ToolkitObjectType.CACHE, mock(SearchFactory.class),
            mock(DistributedClusteredObjectLookup.class), "foo", stripeObjects, configuration,
            mock(Callable.class), serverMapLocalStoreFactory, platformService, mock(ToolkitLock.class));

    ToolkitCacheListener listener1 = new ToolkitCacheListener<Object>() {
        @Override
        public void onEviction(Object o) {
            throw new TCNotRunningException(" Test Exception");
        }

        @Override
        public void onExpiration(Object o) {
        }
    };

    ToolkitCacheListener listener2 = new ToolkitCacheListener<Object>() {
        @Override
        public void onEviction(Object o) {
        }

        @Override
        public void onExpiration(Object o) {
          throw new IllegalStateException("Test Exception");
        }
    };

    asm.addListener(listener1);
    ServerEvent event1 = mock(ServerEvent.class);
    when(event1.getType()).thenReturn(ServerEventType.EVICT);
    asm.handleServerEvent(event1);
    Assert.assertTrue(listAppender.list.isEmpty());

    asm.addListener(listener2);
    ServerEvent event2 = mock(ServerEvent.class);
    when(event2.getType()).thenReturn(ServerEventType.EXPIRE);
    asm.handleServerEvent(event2);
    Assert.assertSame("Cache listener threw an exception", (listAppender.list.get(0).getMessage()));
  }

  @Test
  public void testSearchSequenceIDGenerator() throws Exception {
    for(int i = 1; i <= 10; i++) {
      verifySearchRequestID(i);
    }
  }

  private void verifySearchRequestID(long expectedRequestID) {
    final List<ServerMap> serverMapList = mockServerMaps(1);
    ToolkitObjectStripe[] stripeObjects = createObjectStripes(configuration, serverMapList, 1);

    SearchFactory searchFactoryMock = mock(SearchFactory.class);
    SearchExecutor searchExecutorMock = mock(SearchExecutor.class);
    when(searchFactoryMock.createSearchExecutor(anyString(),
                                            any(ToolkitObjectType.class),
                                            any(ValuesResolver.class),
                                            anyBoolean(),
                                            any(PlatformService.class))).thenReturn(searchExecutorMock);

    AggregateServerMap<String, String> asm = new AggregateServerMap<String, String>(ToolkitObjectType.CACHE, searchFactoryMock,
        mock(DistributedClusteredObjectLookup.class), "foo", stripeObjects, configuration,
        mock(Callable.class), serverMapLocalStoreFactory, platformService, mock(ToolkitLock.class));

    ToolkitSearchQuery toolkitSearchQuery = mock(ToolkitSearchQuery.class);
    when(toolkitSearchQuery.getResultPageSize()).thenReturn(-1); //to avoid snapshots
    asm.executeQuery(toolkitSearchQuery);

    ArgumentCaptor<SearchRequestID> searchRequestIDArgumentCaptor = ArgumentCaptor.forClass(SearchRequestID.class);
    verify(searchExecutorMock).executeQuery(ArgumentCaptor.forClass(ToolkitSearchQuery.class).capture(),
        searchRequestIDArgumentCaptor.capture());
    assertThat(searchRequestIDArgumentCaptor.getValue().toLong(), is(expectedRequestID));
  }
}
