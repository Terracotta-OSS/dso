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

import org.junit.Before;
import org.junit.Test;

import com.tc.object.ObjectID;
import com.tc.objectserver.api.EvictableEntry;
import com.tc.objectserver.api.EvictableMap;
import com.tc.objectserver.api.ServerMapEvictionManager;
import com.tc.objectserver.context.ServerMapEvictionContext;
import com.tc.objectserver.l1.impl.ClientObjectReferenceSet;
import com.tc.objectserver.l1.impl.ClientObjectReferenceSetChangedListener;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import junit.framework.Assert;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author mscott
 */
public class CapacityEvictionTriggerTest extends AbstractEvictionTriggerTest {
  
  ServerMapEvictionManager mgr = mock(ServerMapEvictionManager.class);
    
    public CapacityEvictionTriggerTest() {
    }

    @Override
    public AbstractEvictionTrigger createTrigger() {
        return new CapacityEvictionTrigger(mgr, ObjectID.NULL_ID);
    }
    
    @Test
    public void testCapacityEvictionChaining() throws Exception {
        //  ten million elements in map
        when(getEvictableMap().getSize()).thenReturn(10000000);
        //  set max to 250k
        when(getEvictableMap().getMaxTotalCount()).thenReturn(250000);
        checkCycle(250000);
        verify(this.getClientSet())
          .addReferenceSetChangeListener(any(ClientObjectReferenceSetChangedListener.class));
    }

        @Test
    public void testCapacityEvictionStacking() throws Exception {
        final EvictableMap map = getEvictableMap();
        final CapacityEvictionTrigger ct = (CapacityEvictionTrigger)getTrigger();
        final ClientObjectReferenceSet cs = getClientSet();
        //  ten million elements in map
        when(map.getSize()).thenReturn(10000000);
        //  set max to 250k
        when(map.getMaxTotalCount()).thenReturn(250000);
        when(map.getRandomSamples(anyInt(), any(ClientObjectReferenceSet.class), any(SamplingType.class)))
          .thenReturn(Collections.<Object, EvictableEntry>emptyMap());

        boolean started = ct.startEviction(map);
        Assert.assertTrue(started);
        verify(map).startEviction();
        ServerMapEvictionContext found = ct.collectEvictionCandidates(250000, "MOCK", map, cs);
        Assert.assertNull(found);
        verify(cs)
          .addReferenceSetChangeListener(any(ClientObjectReferenceSetChangedListener.class));
//  now pretend that client updated very fast 
        ct.notifyReferenceSetChanged();
//  now pretend that client updated very fast 
        ct.notifyReferenceSetChanged();
//  now pretend that client updated very fast 
        ct.notifyReferenceSetChanged();
//  happens once
        verify(cs)
          .removeReferenceSetChangeListener(any(ClientObjectReferenceSetChangedListener.class));
//  happens once
        verify(mgr).doEvictionOn(ct);
// simulate eviction start on new thread
        Thread es = new Thread() {

          @Override
          public void run() {
            ct.startEviction(map);
// confirm repeating the same trigger            
            Assert.assertTrue(ct.isValid());
// happened once before start was allowed to continue            
            verify(map, times(2)).startEviction();

            when(map.getRandomSamples(anyInt(), any(ClientObjectReferenceSet.class), any(SamplingType.class)))
              .thenReturn(Collections.singletonMap("test", mock(EvictableEntry.class)));
            
            ct.collectEvictionCandidates(250000, "MOCK", map, cs);
            ct.completeEviction(map);
            verify(map, times(2)).getRandomSamples(anyInt(), eq(cs), eq(SamplingType.FOR_EVICTION));
// only once
            verify(cs).addReferenceSetChangeListener(any(ClientObjectReferenceSetChangedListener.class));
          }
          
        };

        es.start();   
        
        TimeUnit.SECONDS.sleep(3);
// make sure getRandomSamples hasn't been called again
        verify(map).getRandomSamples(anyInt(), eq(cs), eq(SamplingType.FOR_EVICTION));
        
        ct.completeEviction(map);
        es.join();
    }
        
    @Override @Before
    public void setUp() {
        when(getEvictableMap().getSize()).thenReturn(250);
        super.setUp();
    }
    
    
}
