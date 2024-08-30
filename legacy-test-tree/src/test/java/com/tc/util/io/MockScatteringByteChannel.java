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
package com.tc.util.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ScatteringByteChannel;

/**
 * dev-zero implementation of a readable channel.
 */
public class MockScatteringByteChannel extends MockReadableByteChannel implements ScatteringByteChannel {

  @Override
  public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
    throw new IOException("Not yet implemented");
  }

  @Override
  public long read(ByteBuffer[] dsts) throws IOException {
    checkOpen();
    if (dsts == null) { throw new IOException("null ByteBuffer[] passed in to read(ByteBuffer[])"); }
    checkNull(dsts);
    long bytesRead = 0;
    for (int pos = 0; pos < dsts.length && bytesRead < getMaxReadCount(); ++pos) {
      ByteBuffer buffer = dsts[pos];
      while (buffer.hasRemaining() && bytesRead < getMaxReadCount()) {
        buffer.put((byte) 0x00);
        ++bytesRead;
      }
    }
    return bytesRead;
  }

  private void checkNull(ByteBuffer[] srcs) throws IOException {
    for (int pos = 0; pos < srcs.length; ++pos) {
      if (srcs[pos] == null) { throw new IOException("Null ByteBuffer at array position[" + pos + "]"); }
    }
  }
}
