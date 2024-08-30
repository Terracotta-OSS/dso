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
package com.tc.net.protocol;

public class GenericNetworkHeader extends AbstractTCNetworkHeader {

  private static final int LENGTH = 12;

  public GenericNetworkHeader() {
    super(LENGTH, LENGTH);
  }

  public void setSequence(int sequence) {
    data.putInt(4, sequence);
  }

  public int getSequence() {
    return data.getInt(4);
  }

  public void setClientNum(int num) {
    data.putInt(8, num);
  }

  public int getClientNum() {
    return data.getInt(8);
  }

  @Override
  public int getHeaderByteLength() {
    return LENGTH;
  }

  @Override
  protected void setHeaderLength(short length) {
    if (length != LENGTH) { throw new IllegalArgumentException("Header length must be " + LENGTH); }

    return;
  }

  public int getMessageDataLength() {
    return data.getInt(0);
  }

  void setMessageDataLength(int length) {
    data.putInt(0, length);
  }

  @Override
  public void validate() {
    return;
  }

}