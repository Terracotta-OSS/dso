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
package com.terracotta.toolkit.object.serialization;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.tc.platform.PlatformService;

import java.io.ObjectStreamClass;
import java.io.Serializable;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.mock;

/**
 * @author tim
 */
public class ObjectStreamClassMappingClassUnloadTest {

  private static final int PACKING_UNIT = 512 * 1024;

  public volatile WeakReference<Class<?>> classRef;
  public volatile Serializable specialObject;

  @Before
  public void createSpecialObject() throws Exception {
    URL[] urls = new URL[] {SpecialClass.class.getProtectionDomain().getCodeSource().getLocation()};
    ClassLoader duplicate = new URLClassLoader(urls, null);

    @SuppressWarnings("unchecked")
    Class<? extends Serializable> special = (Class<? extends Serializable>) duplicate.loadClass(SpecialClass.class.getName());
    classRef = new WeakReference<Class<?>>(special);

    specialObject = special.newInstance();
  }


  @Test
  public void testClassUnloadingAfterGetMapping() throws Exception {
    ObjectStreamClassMapping objectStreamClassMapping = new ObjectStreamClassMapping(mock(PlatformService.class), new LocalSerializerMap());

    objectStreamClassMapping.getMappingFor(ObjectStreamClass.lookup(specialObject.getClass()));

    specialObject = null;

    for (int i = 0; i < 10; i++) {
      if (classRef.get() == null) {
        return;
      } else {
        packHeap();
      }
    }
    throw new AssertionError();
  }

  @Test
  public void testClassUnloadingAfterGetMappingAndGetDescriptor() throws Exception {
    ObjectStreamClassMapping objectStreamClassMapping = new ObjectStreamClassMapping(mock(PlatformService.class), new LocalSerializerMap());

    objectStreamClassMapping.getObjectStreamClassFor(objectStreamClassMapping.getMappingFor(ObjectStreamClass.lookup(specialObject.getClass())));

    specialObject = null;

    for (int i = 0; i < 10; i++) {
      if (classRef.get() == null) {
        return;
      } else {
        packHeap();
      }
    }
    throw new AssertionError();
  }

  private static void packHeap() {
    List<SoftReference<?>> packing = new ArrayList<SoftReference<?>>();
    ReferenceQueue<byte[]> queue = new ReferenceQueue<byte[]>();
    packing.add(new SoftReference<byte[]>(new byte[PACKING_UNIT], queue));
    while (queue.poll() == null) {
      packing.add(new SoftReference<byte[]>(new byte[PACKING_UNIT]));
    }
  }

  public static class SpecialClass implements Serializable {

    private static final long serialVersionUID = 1L;

    //empty impl
  }

}
