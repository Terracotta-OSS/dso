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

import com.tc.exception.TCRuntimeException;
import com.tc.io.serializer.api.Serializer;
import com.tc.objectserver.core.api.ManagedObjectState;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ManagedObjectStateSerializer implements Serializer {

  @Override
  public void serializeTo(Object o, ObjectOutput out) throws IOException {
    if (!(o instanceof ManagedObjectState)) throw new AssertionError("Attempt to serialize an unknown type: " + o);
    ManagedObjectState mo = (ManagedObjectState) o;
    out.writeByte(mo.getType());
    mo.writeTo(out);
  }

  @Override
  public Object deserializeFrom(ObjectInput in) throws IOException {
    byte type = in.readByte();
    return getStateFactory().readManagedObjectStateFrom(in, type);
  }

  @Override
  public byte getSerializerID() {
    return MANAGED_OBJECT_STATE;
  }

  private ManagedObjectStateFactory getStateFactory() {
    return ManagedObjectStateFactory.getInstance();
  }

}
