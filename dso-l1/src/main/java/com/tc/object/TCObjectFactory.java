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

import com.tc.object.dna.api.DNA;
import com.tc.platform.PlatformService;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface TCObjectFactory {

  public final static TCObject NULL_TC_OBJECT = NullTCObject.INSTANCE;

  public void setObjectManager(ClientObjectManager objectManager);

  public TCObject getNewInstance(ObjectID id, Object peer, Class clazz, boolean isNew);

  public Object getNewPeerObject(TCClass type) throws IllegalArgumentException, InstantiationException,
      IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException;

  public Object getNewPeerObject(TCClass type, DNA dna, PlatformService platformService) throws IOException,
      ClassNotFoundException;

  public void initClazzIfRequired(Class clazz, TCObjectSelf tcObjectSelf);

}