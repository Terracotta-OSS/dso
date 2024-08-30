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

import org.terracotta.toolkit.ToolkitRuntimeException;

import com.tc.object.TCObject;
import com.tc.object.bytecode.Manageable;
import com.tc.util.FindbugsSuppressWarnings;

import java.io.IOException;

public class SerializedClusterObjectImpl<T> implements SerializedClusterObject<T>, Manageable {
  /**
   * <pre>
   * ********************************************************************************************
   * IF YOU'RE CHANGING ANYTHING ABOUT THE FIELDS IN THIS CLASS (name, type, add or remove, etc)
   * YOU MUST UPDATE BOTH THE APPLICATOR AND SERVER STATE CLASSES ACCORDINGLY!
   * ********************************************************************************************
   * </pre>
   */
  private volatile byte[]   bytes;
  private T                 value;
  private volatile TCObject tcObject;

  /**
   * Protected constructor - only supposed to be created by the factory
   */
  protected SerializedClusterObjectImpl(final T value, final byte[] bytes) {
    this.value = value;
    this.bytes = bytes;
  }

  @Override
  public synchronized byte[] getBytes() {
    return this.bytes;
  }

  /**
   * TODO: Remember to make byte[] null later
   */
  @Override
  public synchronized T getValue(SerializationStrategy strategy, boolean compression, boolean local) {
    if (value != null) { return value; }
    try {
      value = (T) strategy.deserialize(bytes, compression, local);
      return value;
    } catch (IOException e) {
      throw new ToolkitRuntimeException(e);
    } catch (ClassNotFoundException e) {
      throw new ToolkitRuntimeException(e);
    }
  }

  public synchronized void internalSetValue(byte[] bytesParam) {
    this.bytes = bytesParam;
  }

  @Override
  public void __tc_managed(TCObject tco) {
    this.tcObject = tco;
  }

  @Override
  public TCObject __tc_managed() {
    return tcObject;
  }

  @Override
  public boolean __tc_isManaged() {
    return tcObject != null;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SerializedClusterObjectImpl)) { return false; }
    SerializedClusterObjectImpl clusterObjectImpl = (SerializedClusterObjectImpl) obj;

    if (this.tcObject.getObjectID().equals(clusterObjectImpl.tcObject.getObjectID())) { return true; }
    if (this.value != null && value == clusterObjectImpl.value) { return true; }

    return false;
  }

  @Override
  public int hashCode() {
    return this.tcObject.getObjectID().hashCode();
  }

  @Override
  @FindbugsSuppressWarnings("DMI_INVOKING_TOSTRING_ON_ARRAY")
  public String toString() {
    return "SerializedClusterObject [cached=" + value + ", value=" + bytes + "]";
  }
}
