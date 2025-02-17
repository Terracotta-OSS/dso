/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;

import java.io.EOFException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.ScatteringByteChannel;
import java.nio.channels.SocketChannel;

/**
 * @author Ludovic Orban
 */
class ClearTextBufferManager implements BufferManager {
  private static final TCLogger logger         = TCLogging.getLogger(ClearTextBufferManager.class);
  private static final String   BUFFER_SIZE    = "clear.text.buffer.size";
  private static final int      BUFFER_SIZE_KB = Integer.getInteger(BUFFER_SIZE, 16) * 1024;
  private final SocketChannel   channel;
  private final ByteBuffer      sendBuffer     = ByteBuffer.allocate(BUFFER_SIZE_KB);
  private final ByteBuffer      recvBuffer     = ByteBuffer.allocate(BUFFER_SIZE_KB);

  ClearTextBufferManager(SocketChannel channel) {
    this.channel = channel;
    if (logger.isDebugEnabled()) {
      logger.debug("ClearTextBufferManager " + BUFFER_SIZE + " " + BUFFER_SIZE_KB);
    }
  }

  @Override
  public boolean remainingToSend() {
    int remaining = sendBuffer.position();
    return remaining != 0;
  }

  @Override
  public int sendFromBuffer() throws IOException {
    sendBuffer.flip();
    int written;
    try {
      written = this.channel.write(sendBuffer);
    } finally {
      sendBuffer.compact();
    }
    if (written == -1) { throw new EOFException(); }
    return written;
  }

  @Override
  public int recvToBuffer() throws IOException {
    int read = this.channel.read(recvBuffer);
    if (read == -1) { throw new EOFException(); }
    return read;
  }

  @Override
  public void close() {
    //
  }

  @Override
  public int forwardFromReadBuffer(ByteBuffer dest) {
    recvBuffer.flip();
    int forwarded = forwardBuffer(recvBuffer, dest);
    recvBuffer.compact();
    return forwarded;
  }

  @Override
  public int forwardFromReadBuffer(GatheringByteChannel gbc) throws IOException {
    recvBuffer.flip();
    int forwarded = gbc.write(recvBuffer);
    recvBuffer.compact();
    if (forwarded == -1) { throw new EOFException(); }
    return forwarded;
  }

  @Override
  public int forwardToWriteBuffer(ByteBuffer src) {
    return forwardBuffer(src, sendBuffer);
  }

  @Override
  public int forwardToWriteBuffer(ScatteringByteChannel sbc) throws IOException {
    int read = sbc.read(sendBuffer);
    if (read == -1) { throw new EOFException(); }
    return read;
  }

  private static int forwardBuffer(final ByteBuffer source, final ByteBuffer dest) {
    int size = Math.min(dest.remaining(), source.remaining());
    if (size > 0) {
      ByteBuffer tmpBuf = source.duplicate();
      tmpBuf.limit(tmpBuf.position() + size);
      dest.put(tmpBuf);
      source.position(source.position() + size);
    }
    return size;
  }
}
