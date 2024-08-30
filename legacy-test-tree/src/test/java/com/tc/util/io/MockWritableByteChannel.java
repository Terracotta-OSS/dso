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
import java.nio.channels.WritableByteChannel;

/**
 * dev-null implementation of a writable byte channel, you can specify the maximum number of bytes to write at once by
 * calling {@link MockWritableByteChannel#setMaxWriteCount(long)}.
 */
public class MockWritableByteChannel extends MockChannel implements WritableByteChannel {

  private long    maxWriteCount = Long.MAX_VALUE;

  @Override
  public final synchronized int write(ByteBuffer src) throws IOException {
    checkOpen();
    if (src == null) { throw new IOException("null ByteBuffer passed in to write(ByteBuffer)"); }
    int writeCount = 0;
    while (src.hasRemaining() && writeCount < getMaxWriteCount()) {
      src.get();
      ++writeCount;
    }
    return writeCount;
  }

  synchronized final void setMaxWriteCount(long maxBytesToWriteAtOnce) {
    maxWriteCount = maxBytesToWriteAtOnce;
  }

  protected final synchronized long getMaxWriteCount() {
    return maxWriteCount;
  }

}
