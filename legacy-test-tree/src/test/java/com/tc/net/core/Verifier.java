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
package com.tc.net.core;

import com.tc.bytes.TCByteBuffer;
import com.tc.net.protocol.GenericNetworkMessage;
import com.tc.net.protocol.GenericNetworkMessageSink;
import com.tc.net.protocol.TCNetworkHeader;
import com.tc.util.HexDump;

public class Verifier implements GenericNetworkMessageSink {
  private final int clientNum;
  private int       counter  = 0;
  private int       sequence = 0;

  public Verifier(int clientNum) {
    this.clientNum = clientNum;
  }

  public int getClientNum() {
    return this.clientNum;
  }

  @Override
  public void putMessage(GenericNetworkMessage msg) {
    final int cn = msg.getClientNum();
    if (cn != clientNum) {
      headerError("unexpected client number " + cn + ", expecting " + clientNum, msg);
    }

    final int seq = msg.getSequence();
    if (seq != sequence) {
      headerError("unexpected sequence number " + seq + ", expecting sequence " + sequence, msg);
    }
    sequence++;

    verifyMessage(msg);
  }

  private void verifyMessage(GenericNetworkMessage msg) {
    TCByteBuffer data[] = msg.getPayload();
    for (int i = 0; i < data.length; i++) {
      TCByteBuffer buf = data[i].duplicate();

      while (buf.hasRemaining()) {
        final int num = buf.getInt();
        if (num != clientNum) {
          dataError("unexpected client number " + num + ", expecting " + clientNum, buf, buf.position() - 4, i,
                    data.length);
        }

        final int cnt = buf.getInt();
        if (cnt != counter) {
          dataError("unexpected counter value " + cnt + ", expecting " + counter, buf, buf.position() - 4, i,
                    data.length);
        }
        counter++;
      }
    }
  }

  private void dataError(String error, TCByteBuffer buf, int position, int numBuf, int numBufs) {
    error += "\n";
    error += "Message " + sequence + ", Buffer " + (numBuf + 1) + " of " + numBufs + " at position 0x"
             + Integer.toHexString(position).toUpperCase();
    error += " " + HexDump.dump(buf.array(), buf.arrayOffset(), buf.limit());
    throw new RuntimeException(error);
  }

  private void headerError(String errorMsg, GenericNetworkMessage msg) {
    TCNetworkHeader hdr = msg.getHeader();
    TCByteBuffer hdrData = hdr.getDataBuffer();
    throw new RuntimeException(errorMsg + "\n"
                               + HexDump.dump(hdrData.array(), hdrData.arrayOffset(), hdr.getHeaderByteLength()));
  }
}