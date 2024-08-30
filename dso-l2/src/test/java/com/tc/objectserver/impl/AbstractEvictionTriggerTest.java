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
package com.tc.objectserver.impl;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;

import com.tc.object.ObjectID;
import com.tc.objectserver.api.EvictableEntry;
import com.tc.objectserver.api.EvictableMap;
import com.tc.objectserver.context.ServerMapEvictionContext;
import com.tc.objectserver.l1.impl.ClientObjectReferenceSet;

import java.util.Collections;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.intThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author mscott
 */
public class AbstractEvictionTriggerTest {

  private EvictableMap             evm;
  private AbstractEvictionTrigger  trigger;
  private ClientObjectReferenceSet clientSet;

  public EvictableMap getEvictableMap() {
    if (evm == null) {
      evm = mock(EvictableMap.class);
      when(evm.getMaxTotalCount()).thenReturn(-1);
      when(evm.getSize()).thenReturn(100);
    }
    return evm;
  }

  public AbstractEvictionTrigger getTrigger() {
    if (trigger == null) {
      trigger = createTrigger();
    }
    return trigger;
  }

  public ClientObjectReferenceSet getClientSet() {
    if (clientSet == null) {
      clientSet = mock(ClientObjectReferenceSet.class);
    }
    return clientSet;
  }

  public AbstractEvictionTrigger createTrigger() {
    return new AbstractEvictionTrigger(ObjectID.NULL_ID) {

      @Override
      public ServerMapEvictionContext collectEvictionCandidates(int targetMax, String className, EvictableMap map,
                                                            ClientObjectReferenceSet clients) {
        return createEvictionContext(className, map.getRandomSamples(boundsCheckSampleSize(targetMax), clientSet,
            SamplingType.FOR_EVICTION));
      }
    };
  }

  public AbstractEvictionTriggerTest() {
  }

  @BeforeClass
  public static void setUpClass() {
    //
  }

  @AfterClass
  public static void tearDownClass() {
    //
  }

  public void checkSizeCycle(int size) {
    EvictableMap map = getEvictableMap();
    when(map.getSize()).thenReturn(size);
    checkCycle(Integer.MAX_VALUE);
  }

  public void checkMaxCycle(int max) {
    EvictableMap map = getEvictableMap();
    when(map.getMaxTotalCount()).thenReturn(max);
    checkCycle(max);
  }

  public void checkCycle(int max) {
    EvictableMap map = getEvictableMap();
    final AbstractEvictionTrigger et = getTrigger();
    ClientObjectReferenceSet cs = getClientSet();
    ServerMapEvictionContext found = null;

    boolean startEviction = et.startEviction(map);
    if (startEviction) {
      verify(map).startEviction();
      found = et.collectEvictionCandidates(max, "MOCK", map, cs);
      et.completeEviction(map);
    }
    
    if (found != null) {
      verify(map).getRandomSamples(intThat(new ArgumentMatcher<Integer>() {
        int maxLocal = et.boundsCheckSampleSize(Integer.MAX_VALUE);

        @Override
        public boolean matches(Integer item) {
          return item <= maxLocal && item >= 0;
        }
      }), eq(cs), SamplingType.FOR_EVICTION);
      verify(map).evictionCompleted();
    }
  }

  @Test
  public void testNormalMax() {
    checkMaxCycle(200);
  }

  @Test
  public void testPinnedMax() {
    checkMaxCycle(0);
  }

  @Test
  public void testLowerMax() {
    checkMaxCycle(Integer.MIN_VALUE);
  }

  @Test
  public void testUpperMax() {
    checkMaxCycle(Integer.MAX_VALUE);
  }

  @Test
  public void testNormalSize() {
    checkSizeCycle(200);
  }

  @Test
  public void testEmptySize() {
    checkSizeCycle(0);
  }

  @Test
  public void testUpperSize() {
    checkSizeCycle(Integer.MAX_VALUE);
  }

  @Before
  public void setUp() {
    evm = getEvictableMap();
    trigger = getTrigger();
    when(evm.startEviction()).thenReturn(Boolean.TRUE);
    when(evm.getRandomSamples(anyInt(), eq(clientSet), eq(SamplingType.FOR_EVICTION)))
        .thenReturn(Collections.<Object, EvictableEntry> emptyMap());
  }

  @After
  public void tearDown() {
    //
  }
}
