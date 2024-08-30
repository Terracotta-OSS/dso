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

import com.tc.bytes.TCByteBuffer;

/**
 * @author teck
 */
public class NullNetworkMessage implements TCNetworkMessage {

  public NullNetworkMessage() {
    super();
  }

  @Override
  public TCNetworkHeader getHeader() {
    return new NullNetworkHeader();
  }

  @Override
  public TCNetworkMessage getMessagePayload() {
    return null;
  }

  @Override
  public TCByteBuffer[] getPayload() {
    return getEntireMessageData();
  }

  @Override
  public TCByteBuffer[] getEntireMessageData() {
    return new TCByteBuffer[] {};
  }

  @Override
  public boolean isSealed() {
    return true;
  }

  @Override
  public void seal() {
    return;
  }

  @Override
  public int getDataLength() {
    return 0;
  }

  @Override
  public int getHeaderLength() {
    return 0;
  }

  @Override
  public int getTotalLength() {
    return 0;
  }

  @Override
  public void wasSent() {
    return;
  }

  @Override
  public void setSentCallback(Runnable callback) {
    return;
  }

  @Override
  public Runnable getSentCallback() {
    return null;
  }

  @Override
  public void recycle() {
    return;
  }

}