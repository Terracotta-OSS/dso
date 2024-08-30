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
package com.tc.io.serializer.api;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public interface Serializer {

  public static final byte UNKNOWN              = 0x00;
  public static final byte OBJECT_ID            = 0x01;
  public static final byte STRING_INDEX         = 0x02;
  public static final byte STRING_UTF           = 0x03;
  public static final byte BOOLEAN              = 0x04;
  public static final byte BYTE                 = 0x05;
  public static final byte CHARACTER            = 0x06;
  public static final byte DOUBLE               = 0x07;
  public static final byte FLOAT                = 0x08;
  public static final byte INTEGER              = 0x09;
  public static final byte LONG                 = 0x0a;
  public static final byte SHORT                = 0x0b;
  public static final byte OBJECT               = 0x0c;
  public static final byte MANAGED_OBJECT_STATE = 0x0d;
  public static final byte MANAGED_OBJECT       = 0x0e;

  public void serializeTo(Object o, ObjectOutput out) throws IOException;

  public Object deserializeFrom(ObjectInput in) throws IOException, ClassNotFoundException;

  public byte getSerializerID();
}
