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
import com.tc.net.core.TCConnection;

/**
 * A generic network messge. Not really useful except for testing
 * 
 * @author teck
 */
public class GenericNetworkMessage extends AbstractTCNetworkMessage {
  private final TCConnection source;
  private boolean            sent = false;

  public GenericNetworkMessage(TCConnection source, TCByteBuffer data) {
    this(source, new TCByteBuffer[] { data });
  }

  public GenericNetworkMessage(TCConnection source, TCByteBuffer data[]) {
    super(new GenericNetworkHeader(), data);

    GenericNetworkHeader hdr = (GenericNetworkHeader) getHeader();

    int msgLength = 0;
    for (int i = 0; i < data.length; i++) {
      msgLength += data[i].limit();
    }

    hdr.setMessageDataLength(msgLength);
    this.source = source;
  }

  GenericNetworkMessage(TCConnection source, TCNetworkHeader header, TCByteBuffer[] payload) {
    super(header, payload);
    this.source = source;
  }

  public void setSequence(int seq) {
    ((GenericNetworkHeader) getHeader()).setSequence(seq);
  }

  public int getSequence() {
    return ((GenericNetworkHeader) getHeader()).getSequence();
  }

  public void setClientNum(int num) {
    ((GenericNetworkHeader) getHeader()).setClientNum(num);
  }

  public int getClientNum() {
    return ((GenericNetworkHeader) getHeader()).getClientNum();
  }

  public TCConnection getSource() {
    return source;
  }

  public synchronized void waitUntilSent() throws InterruptedException {
    while (!sent) {
      wait();
    }
  }

  public synchronized void setSent() {
    this.sent = true;
    notifyAll();
  }
}