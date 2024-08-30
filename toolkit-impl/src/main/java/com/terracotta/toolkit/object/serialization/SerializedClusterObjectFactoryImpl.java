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

import org.terracotta.toolkit.object.serialization.NotSerializableRuntimeException;

import com.tc.net.GroupID;
import com.tc.platform.PlatformService;

import java.io.Serializable;

public class SerializedClusterObjectFactoryImpl implements SerializedClusterObjectFactory {

  private final SerializationStrategy strategy;
  private final PlatformService       platformService;

  public SerializedClusterObjectFactoryImpl(PlatformService platformService, SerializationStrategy strategy) {
    this.strategy = strategy;
    this.platformService = platformService;
  }

  @Override
  public SerializedClusterObject createSerializedClusterObject(final Object value, final GroupID gid)
      throws NotSerializableRuntimeException {
    SerializedClusterObject clusterObject;
    clusterObject = new SerializedClusterObjectImpl(value, strategy.serialize(value, false));
    platformService.lookupOrCreate(clusterObject, gid);
    return clusterObject;
  }

  @Override
  public SerializedMapValue createSerializedMapValue(SerializedMapValueParameters params, GroupID gid) {
    SerializedMapValue serializedMapValue;
    if (params.isCustomLifespan()) {
      serializedMapValue = new CustomLifespanSerializedMapValue<Serializable>(params);
    } else {
      serializedMapValue = new SerializedMapValue<Serializable>(params);
    }
    platformService.lookupOrCreate(serializedMapValue, gid);
    return serializedMapValue;
  }

}