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
package com.tc.object;

import com.tc.exception.ImplementMe;
import com.tc.object.applicator.ChangeApplicator;
import com.tc.object.dna.api.DNA;
import com.tc.object.dna.api.DNAWriter;
import com.tc.platform.PlatformService;

import java.lang.reflect.Constructor;

public class TestClassFactory implements TCClassFactory {

  @Override
  public TCClass getOrCreate(final Class clazz, final ClientObjectManager objectManager) {
    return new MockTCClass();
  }

  @Override
  public ChangeApplicator createApplicatorFor(final TCClass clazz) {
    throw new ImplementMe();
  }

  @Override
  public void setPlatformService(PlatformService platformService) {
    throw new UnsupportedOperationException();
  }

  public static class MockTCClass implements TCClass {

    private ClientObjectManager clientObjectManager;

    public MockTCClass() {
      //
    }

    public MockTCClass(final ClientObjectManager clientObjectManager) {
      this.clientObjectManager = clientObjectManager;
    }

    public String getOnLoadMethod() {
      return null;
    }

    public String getOnLoadExecuteScript() {
      return "";
    }

    @Override
    public TraversedReferences getPortableObjects(final Object pojo, final TraversedReferences addTo) {
      return addTo;
    }

    @Override
    public Constructor getConstructor() {
      return null;
    }

    @Override
    public String getName() {
      return null;
    }

    @Override
    public void hydrate(final TCObject tcObject, final DNA dna, final Object pojo, final boolean force) {
      //
    }

    @Override
    public void dehydrate(final TCObject tcObject, final DNAWriter writer, final Object pojo) {
      //
    }

    @Override
    public TCObject createTCObject(final ObjectID id, final Object peer, final boolean isNew) {
      return null;
    }

    @Override
    public boolean isUseNonDefaultConstructor() {
      return false;
    }

    @Override
    public Object getNewInstanceFromNonDefaultConstructor(final DNA dna, PlatformService platformService) {
      return null;
    }

    @Override
    public Class getPeerClass() {
      return Object.class;
    }

    @Override
    public ClientObjectManager getObjectManager() {
      return clientObjectManager;
    }

  }
}
