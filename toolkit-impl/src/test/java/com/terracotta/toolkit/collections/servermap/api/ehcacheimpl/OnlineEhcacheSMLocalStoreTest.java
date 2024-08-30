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
package com.terracotta.toolkit.collections.servermap.api.ehcacheimpl;

import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.terracotta.InternalEhcache;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author mscott
 */
public class OnlineEhcacheSMLocalStoreTest {
  
  /**
   * Test of get method, of class OnlineEhcacheSMLocalStore.
   */
  @Test
  public void testGet() {
    Object key = "test";
    Object value = "value";
    Element old = new Element(key,value);
    
    InternalEhcache base = mock(InternalEhcache.class);
    when(base.get(any(Object.class))).thenReturn(old);
    
    CacheConfiguration config = mock(CacheConfiguration.class);
    when(config.isOverflowToOffHeap()).thenReturn(false);
    when(base.getCacheConfiguration()).thenReturn(config);
    
    OnlineEhcacheSMLocalStore instance = new OnlineEhcacheSMLocalStore(base);
    Object result = instance.get(key);
    assertEquals(old.getObjectValue(), result);
    
    verify(base).get(any(Object.class));
  }

  /**
   * Test of getKeys method, of class OnlineEhcacheSMLocalStore.
   */
  @Test
  public void testGetKeys() {
    List<Object> keys = new ArrayList<Object>(5);
    for (int x=0;x<5;x++) {
      keys.add(Integer.toString(x));
    }
    
    InternalEhcache base = mock(InternalEhcache.class);
    when(base.getKeys()).thenReturn(keys);
    
    CacheConfiguration config = mock(CacheConfiguration.class);
    when(config.isOverflowToOffHeap()).thenReturn(false);
    when(base.getCacheConfiguration()).thenReturn(config);
    
    OnlineEhcacheSMLocalStore instance = new OnlineEhcacheSMLocalStore(base);
    instance.getKeys();
    
    verify(base).getKeys();
  }

  /**
   * Test of put method, of class OnlineEhcacheSMLocalStore.
   */
  @Test
  public void testPut() throws Exception {
    Object key = "test";
    Object value = "value";
    Element old = new Element("test","old");
    
    InternalEhcache base = mock(InternalEhcache.class);
    when(base.removeAndReturnElement(eq(key))).thenReturn(old);
    when(base.putIfAbsent(any(Element.class))).thenReturn(old);
    when(base.replace(eq(old), any(Element.class))).thenReturn(true);
    
    CacheConfiguration config = mock(CacheConfiguration.class);
    when(config.isOverflowToOffHeap()).thenReturn(false);
    when(base.getCacheConfiguration()).thenReturn(config);
    
    OnlineEhcacheSMLocalStore instance = new OnlineEhcacheSMLocalStore(base);
    Object result = instance.put(key, value);
    assertEquals(old.getObjectValue(), result);
    
//    verify(base).putIfAbsent(Matchers.any(Element.class));
//    verify(base).replace(Matchers.eq(old), Matchers.any(Element.class));
  }

  /**
   * Test of remove method, of class OnlineEhcacheSMLocalStore.
   */
  @Test
  public void testRemove_Object() {
    Object key = "test";
    Object value = "value";
    Element old = new Element(key,value);
    
    InternalEhcache base = mock(InternalEhcache.class);
    when(base.removeAndReturnElement(any())).thenReturn(old);
    
    CacheConfiguration config = mock(CacheConfiguration.class);
    when(config.isOverflowToOffHeap()).thenReturn(false);
    when(base.getCacheConfiguration()).thenReturn(config);
    
    OnlineEhcacheSMLocalStore instance = new OnlineEhcacheSMLocalStore(base);
    Object result = instance.remove(key);
    assertEquals(old.getObjectValue(), result);
    
    verify(base).removeAndReturnElement(any());
  }

  /**
   * Test of remove method, of class OnlineEhcacheSMLocalStore.
   */
  @Test
  public void testRemove_Object_Object() {
    Object key = "test";
    Object value = "value";
    Element old = new Element(key,value);
    
    InternalEhcache base = mock(InternalEhcache.class);
    when(base.get(any(Object.class))).thenReturn(old);
    when(base.removeElement(eq(old))).thenReturn(true);
    
    CacheConfiguration config = mock(CacheConfiguration.class);
    when(config.isOverflowToOffHeap()).thenReturn(false);
    when(base.getCacheConfiguration()).thenReturn(config);
    
    OnlineEhcacheSMLocalStore instance = new OnlineEhcacheSMLocalStore(base);
    Object result = instance.remove(key, value);
    assertEquals(old.getObjectValue(), result);
    
    verify(base).get(any(Object.class));
    verify(base).removeElement(eq(old));
  }

  /**
   * Test of clear method, of class OnlineEhcacheSMLocalStore.
   */
  @Test
  public void testClear() {
    Object key = "test";
    Object value = "value";
    Element old = new Element(key,value);
    
    InternalEhcache base = mock(InternalEhcache.class);
    
    CacheConfiguration config = mock(CacheConfiguration.class);
    when(config.isOverflowToOffHeap()).thenReturn(false);
    when(base.getCacheConfiguration()).thenReturn(config);
    
    OnlineEhcacheSMLocalStore instance = new OnlineEhcacheSMLocalStore(base);
    instance.clear();
    
    verify(base).removeAll();
  }

  /**
   * Test of cleanLocalState method, of class OnlineEhcacheSMLocalStore.
   */
  @Test
  public void testCleanLocalState() {
    Object key = "test";
    Object value = "value";
    Element old = new Element(key,value);
    
    InternalEhcache base = mock(InternalEhcache.class);
    
    CacheConfiguration config = mock(CacheConfiguration.class);
    when(config.isOverflowToOffHeap()).thenReturn(false);
    when(base.getCacheConfiguration()).thenReturn(config);
    
    OnlineEhcacheSMLocalStore instance = new OnlineEhcacheSMLocalStore(base);
    instance.cleanLocalState();
    
    verify(base).removeAll(eq(true));
  }
  
}
