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
package com.tc.util;

import com.tc.io.TCFile;
import com.tc.io.TCFileChannel;
import com.tc.io.TCFileLock;
import com.tc.io.TCRandomFileAccess;
import com.tc.util.startuplock.FileNotCreatedException;
import com.tc.util.startuplock.LocationNotCreatedException;

import java.io.File;
import java.io.IOException;
import java.nio.channels.OverlappingFileLockException;

import junit.framework.TestCase;

public class StartupLockTest extends TestCase {

  private static final int lockedAlreadyOnThisVM = 0;
  private static final int lockCanBeAquired      = 1;

  public void testBlocking() throws Throwable {
    TestTCRandomFileAccessImpl randomFileAccess = new TestTCRandomFileAccessImpl();

    boolean locationIsMakable = false;
    boolean fileIsmakable = false;
    TestTCFileImpl location = new TestTCFileImpl(locationIsMakable);
    location.setNewFileIsMakable(fileIsmakable);
    StartupLock startupLock = new BlockingStartupLock(location, false);
    try {
      startupLock.canProceed(randomFileAccess);
      fail("Expected LocationNotCreatedException. Not thrown.");
    } catch (LocationNotCreatedException se) {
      // ok
    }
    Assert.assertFalse(location.exists());

    locationIsMakable = true;
    fileIsmakable = false;
    location = new TestTCFileImpl(locationIsMakable);
    location.setNewFileIsMakable(fileIsmakable);
    startupLock = new BlockingStartupLock(location, false);
    try {
      startupLock.canProceed(randomFileAccess);
      fail("Expected FileNotCreatedException. Not thrown.");
    } catch (FileNotCreatedException se) {
      // ok
    }
    Assert.assertTrue(location.exists());

    randomFileAccess.setLockAvailability(lockedAlreadyOnThisVM);
    locationIsMakable = true;
    fileIsmakable = true;
    location = new TestTCFileImpl(locationIsMakable);
    location.setNewFileIsMakable(fileIsmakable);
    startupLock = new BlockingStartupLock(location, false);
    try {
      startupLock.canProceed(randomFileAccess);
      fail("Expected AssertionError for OverlappingFileLockException. Not thrown.");
    } catch (AssertionError se) {
      // ok
    }

    randomFileAccess.setLockAvailability(lockCanBeAquired);
    locationIsMakable = true;
    fileIsmakable = true;
    location = new TestTCFileImpl(locationIsMakable);
    location.setNewFileIsMakable(fileIsmakable);
    startupLock = new BlockingStartupLock(location, false);
    boolean result = startupLock.canProceed(randomFileAccess);
    Assert.assertTrue(location.exists());
    Assert.assertTrue(result);
  }

  public void testNonBlocking() throws Throwable {
    TestTCRandomFileAccessImpl randomFileAccess = new TestTCRandomFileAccessImpl();

    boolean locationIsMakable = false;
    boolean fileIsmakable = false;
    TestTCFileImpl location = new TestTCFileImpl(locationIsMakable);
    location.setNewFileIsMakable(fileIsmakable);
    StartupLock startupLock = new NonBlockingStartupLock(location, false);
    try {
      startupLock.canProceed(randomFileAccess);
      fail("Expected LocationNotCreatedException. Not thrown.");
    } catch (LocationNotCreatedException se) {
      // ok
    }
    Assert.assertFalse(location.exists());

    locationIsMakable = true;
    fileIsmakable = false;
    location = new TestTCFileImpl(locationIsMakable);
    location.setNewFileIsMakable(fileIsmakable);
    startupLock = new NonBlockingStartupLock(location, false);
    try {
      startupLock.canProceed(randomFileAccess);
      fail("Expected FileNotCreatedException. Not thrown.");
    } catch (FileNotCreatedException se) {
      // ok
    }
    Assert.assertTrue(location.exists());

    randomFileAccess.setLockAvailability(lockedAlreadyOnThisVM);
    locationIsMakable = true;
    fileIsmakable = true;
    location = new TestTCFileImpl(locationIsMakable);
    location.setNewFileIsMakable(fileIsmakable);
    startupLock = new NonBlockingStartupLock(location, false);
    try {
      startupLock.canProceed(randomFileAccess);
      fail("Expected AssertionError for OverlappingFileLockException. Not thrown.");
    } catch (AssertionError se) {
      // ok
    }

    randomFileAccess.setLockAvailability(lockCanBeAquired);
    locationIsMakable = true;
    fileIsmakable = true;
    location = new TestTCFileImpl(locationIsMakable);
    location.setNewFileIsMakable(fileIsmakable);
    startupLock = new NonBlockingStartupLock(location, false);
    boolean result = startupLock.canProceed(randomFileAccess);
    Assert.assertTrue(location.exists());
    Assert.assertTrue(result);
  }

  private static class TestTCRandomFileAccessImpl implements TCRandomFileAccess {
    private int lockAvailability;

    public void setLockAvailability(int lockAvailability) {
      this.lockAvailability = lockAvailability;
    }

    @Override
    public TCFileChannel getChannel(TCFile tcFile, String mode) {
      return new TestTCFileChannelImpl(tcFile, mode, lockAvailability);
    }
  }

  private static class TestTCFileChannelImpl implements TCFileChannel {

    private final int lockAvailability;

    public TestTCFileChannelImpl(TCFile file, String mode, int lockAvailability) {
      this.lockAvailability = lockAvailability;
    }

    @Override
    public TCFileLock lock() throws OverlappingFileLockException {
      if (lockAvailability == lockedAlreadyOnThisVM) { throw new OverlappingFileLockException(); }
      return new TestTCFileLockImpl();
    }

    @Override
    public void close() {
      // method is not used in test
    }

    @Override
    public TCFileLock tryLock() throws OverlappingFileLockException {
      return lock();
    }

  }

  private static class TestTCFileLockImpl implements TCFileLock {

    @Override
    public void release() {
      // method not used in test
    }

  }

  private class TestTCFileImpl implements TCFile {

    private final boolean fileIsMakable;
    private boolean       fileExists;
    private boolean       newFileIsMakable;

    public TestTCFileImpl(boolean isMakable) {
      fileIsMakable = isMakable;
      fileExists = false;
    }

    @Override
    public boolean exists() {
      return fileExists;
    }

    @Override
    public void forceMkdir() throws IOException {
      if (!fileIsMakable) { throw new IOException("Could not create indicated directory."); }

      fileExists = true;
    }

    @Override
    public File getFile() {
      return null;
    }

    @Override
    public TCFile createNewTCFile(TCFile location, String fileName) {
      return new TestTCFileImpl(newFileIsMakable);
    }

    @Override
    public boolean createNewFile() throws IOException {
      if (!fileIsMakable) { throw new IOException("Could not create indicated directory."); }

      fileExists = true;
      return fileExists;
    }

    public void setNewFileIsMakable(boolean val) {
      newFileIsMakable = val;
    }

    @Override
    public String toString() {
      String s = "TestTCFileImpl:  ";
      if (fileIsMakable) {
        s += "file is makable, ";
      } else {
        s += "file is not makable, ";
      }
      if (fileExists) {
        s += "file exists.";
      } else {
        s += "file does not exist.";
      }
      return s;
    }
  }

}
