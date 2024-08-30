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
package com.tc.objectserver.managedobject;

import com.tc.io.serializer.api.Serializer;
import com.tc.object.ObjectID;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.objectserver.core.api.ManagedObjectState;
import com.tc.objectserver.persistence.ManagedObjectPersistor;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ManagedObjectSerializer implements Serializer {
  private final ManagedObjectStateSerializer serializer;
  private final ManagedObjectPersistor persistor;

  public ManagedObjectSerializer(ManagedObjectStateSerializer serializer, ManagedObjectPersistor persistor) {
    this.serializer = serializer;
    this.persistor = persistor;
  }

  @Override
  public void serializeTo(final Object mo, final ObjectOutput out) throws IOException {
    if (mo instanceof ManagedObject) {
      ((ManagedObject)mo).serializeTo(out, serializer);
    } else {
      throw new IllegalArgumentException("Trying to serialize a non-ManagedObject " + mo);
    }
  }

  @Override
  public Object deserializeFrom(final ObjectInput in) throws IOException {
    // read data
    final long version = in.readLong();
    final ObjectID id = new ObjectID(in.readLong());
    final ManagedObjectState state = (ManagedObjectState) this.serializer.deserializeFrom(in);

    // populate managed object...
    final ManagedObjectImpl rv = new ManagedObjectImpl(id, persistor);
    rv.setDeserializedState(version, state);
    return rv;
  }

  @Override
  public byte getSerializerID() {
    return MANAGED_OBJECT;
  }
}
