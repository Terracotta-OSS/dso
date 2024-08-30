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

import com.tc.object.bytecode.Manageable;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * A SerializedEntry subclass that supports cache entries with custom TTI/TTL values.
 * 
 * @author Chris Dennis
 */
public class CustomLifespanSerializedMapValue<T> extends SerializedMapValue<T> implements Manageable {

  /**
   * <pre>
   * ********************************************************************************************
   * IF YOU'RE CHANGING ANYTHING ABOUT THE FIELDS IN THIS CLASS (name, type, add or remove, etc)
   * YOU MUST UPDATE BOTH THE APPLICATOR AND SERVER STATE CLASSES ACCORDINGLY!
   * ********************************************************************************************
   * </pre>
   */
  private volatile int customTti;
  private volatile int customTtl;

  public CustomLifespanSerializedMapValue() {
    super();
  }

  public CustomLifespanSerializedMapValue(final SerializedMapValueParameters<T> params) {
    super(params);
    this.customTti = params.getCustomTTI();
    this.customTtl = params.getCustomTTL();
  }

  @Override
  public boolean isExpired(int atTime, int maxTTISeconds, int maxTTLSeconds) {
    final int tti = this.customTti < 0 ? maxTTISeconds : this.customTti;
    final int ttl = this.customTtl < 0 ? maxTTLSeconds : this.customTtl;

    return atTime >= calculateExpiresAt(tti, ttl);
  }

  protected int internalGetCustomTti() {
    return customTti;
  }

  protected int internalGetCustomTtl() {
    return customTtl;
  }

  protected void internalSetCustomTti(int tti) {
    this.customTti = tti;
  }

  protected void internalSetCustomTtl(int ttl) {
    this.customTtl = ttl;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeInt(customTti);
    out.writeInt(customTtl);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException {
    super.readExternal(in);
    customTti = in.readInt();
    customTtl = in.readInt();
  }

  @Override
  public long getTimeToIdle() {
    return customTti;
  }

  @Override
  public void setTimeToIdle(final long timeToIdle) {
    this.customTti = (int) timeToIdle;
  }

  @Override
  public long getTimeToLive() {
    return customTtl;
  }

  @Override
  public void setTimeToLive(final long timeToLive) {
    this.customTtl = (int) timeToLive;
  }
}
