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
package com.tc.net.protocol.transport;

import com.tc.exception.ImplementMe;
import com.tc.net.protocol.NetworkLayer;

public class TestSynAckMessage extends TestTransportHandshakeMessage implements SynAckMessage {

  @Override
  public String getErrorContext() {
    throw new ImplementMe();
  }

  @Override
  public short getErrorType() {
    throw new ImplementMe();
  }

  @Override
  public boolean hasErrorContext() {
    throw new ImplementMe();
  }

  @Override
  public boolean isSyn() {
    return false;
  }

  @Override
  public boolean isSynAck() {
    return true;
  }

  @Override
  public boolean isAck() {
    return false;
  }

  @Override
  public void recycle() {
    return;
  }

  @Override
  public short getStackLayerFlags() {
    return NetworkLayer.TYPE_TEST_MESSAGE;
  }

  @Override
  public int getCallbackPort() {
    throw new ImplementMe();
  }

}
