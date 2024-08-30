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
import java.nio.channels.GatheringByteChannel;

/**
 * dev-null implementation of a gathering byte channel.
 */
public class MockGatheringByteChannel extends MockWritableByteChannel implements GatheringByteChannel {

  @Override
  public synchronized long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
    checkOpen();
    if (srcs == null) { throw new IOException("null ByteBuffer[] passed in to write(ByteBuffer[], int, int)"); }
    checkNull(srcs);
    throw new IOException("Not yet implemented");
  }

  @Override
  public synchronized long write(ByteBuffer[] srcs) throws IOException {
    checkOpen();
    if (srcs == null) { throw new IOException("null ByteBuffer[] passed in to write(ByteBuffer[])"); }
    checkNull(srcs);
    long bytesWritten = 0;
    for (int pos = 0; pos < srcs.length && bytesWritten < getMaxWriteCount(); ++pos) {
      ByteBuffer buffer = srcs[pos];
      while (buffer.hasRemaining() && bytesWritten < getMaxWriteCount()) {
        buffer.get();
        ++bytesWritten;
      }
    }
    return bytesWritten;
  }

  private void checkNull(ByteBuffer[] srcs) throws IOException {
    for (int pos = 0; pos < srcs.length; ++pos) {
      if (srcs[pos] == null) { throw new IOException("Null ByteBuffer at array position[" + pos + "]"); }
    }
  }
}
