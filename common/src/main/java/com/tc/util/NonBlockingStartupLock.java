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
package com.tc.util;

import com.tc.io.TCFile;

import java.io.IOException;
import java.nio.channels.OverlappingFileLockException;

/**
 * Class for testing if it is safe to startup a process on a specified directory (i.e. will it corrupt a db)
 */
public class NonBlockingStartupLock extends AbstractStartupLock implements StartupLock {

  public NonBlockingStartupLock(TCFile location, boolean retries) {
    super(location, retries);
  }

  @Override
  protected void requestLock(TCFile tcFile) {
    try {
      lock = channel.tryLock();
    } catch (OverlappingFileLockException e) {
      // File is already locked in this thread or virtual machine
      throw new AssertionError(e);
    } catch (IOException ioe) {
      throw new TCDataFileLockingException("Unable to acquire file lock on '" + tcFile.getFile().getAbsolutePath()
                                           + "'.  Aborting Terracotta server instance startup.");
    }
  }

  @Override
  public boolean isBlocked() {
    return false;
  }
}
