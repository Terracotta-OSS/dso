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
import com.tc.object.dna.api.DNA;
import com.tc.platform.PlatformService;

public class TestObjectFactory implements TCObjectFactory {

  public TCObject tcObject;
  public Object   peerObject;

  @Override
  public void setObjectManager(ClientObjectManager objectManager) {
    return;
  }

  @Override
  public TCObject getNewInstance(ObjectID id, Object peer, Class clazz, boolean isNew) {
    return tcObject;
  }

  public TCObject getNewInstance(ObjectID id, Class clazz, boolean isNew) {
    return tcObject;
  }

  @Override
  public Object getNewPeerObject(TCClass type) throws IllegalArgumentException, SecurityException {
    return peerObject;
  }

  @Override
  public Object getNewPeerObject(TCClass type, DNA dna, PlatformService platformService) {
    return peerObject;
  }

  @Override
  public void initClazzIfRequired(Class clazz, TCObjectSelf tcObjectSelf) {
    throw new ImplementMe();

  }

}
