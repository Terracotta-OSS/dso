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
package com.tc.io;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class TCFileChannelImpl implements TCFileChannel {

  private final FileChannel channel;

  public TCFileChannelImpl(FileChannel channel) {
    this.channel = channel;
  }

  @Override
  public TCFileLock lock() throws IOException, OverlappingFileLockException {
    return new TCFileLockImpl(channel.lock());
  }

  @Override
  public void close() throws IOException {
    channel.close();
  }

  @Override
  public TCFileLock tryLock() throws IOException, OverlappingFileLockException {
    FileLock lock = channel.tryLock();
    if (lock != null) { return new TCFileLockImpl(lock); }
    return null;
  }

}
