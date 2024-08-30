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
import java.nio.channels.ReadableByteChannel;

/**
 * dev-zero implementation of a readable byte channel, you can specify the maximum number of bytes to read at once by
 * calling {@link MockReadableByteChannel#setMaxReadCount(long)}.
 */
public class MockReadableByteChannel extends MockChannel implements ReadableByteChannel {

  private long maxReadCount = Long.MAX_VALUE;

  @Override
  public final synchronized int read(ByteBuffer dst) throws IOException {
    checkOpen();
    dst.isReadOnly(); // NPE check
    int readCount = 0;
    while (dst.hasRemaining() && readCount < getMaxReadCount()) {
      dst.put((byte) 0x00);
      ++readCount;
    }
    return readCount;
  }

  synchronized final void setMaxReadCount(long maxBytesToReadAtOnce) {
    maxReadCount = maxBytesToReadAtOnce;
  }

  protected final synchronized long getMaxReadCount() {
    return maxReadCount;
  }

}
