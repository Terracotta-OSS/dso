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
package com.tc.test;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;

import junit.framework.TestCase;

/**
 * Unit test for {@link DataDirectoryHelper}.
 */
public class DataDirectoryHelperTest extends TestCase {

  private File                baseFile;
  private File                expectedDir;
  private DataDirectoryHelper helper;

  @Override
  public void setUp() throws Exception {
    String root = TestConfigObject.getInstance().tempDirectoryRoot() + File.separator + "temp-DataDirectoryHelperTest";

    this.baseFile = new File(root);
    this.expectedDir = new File(this.baseFile, ClassBasedDirectoryTreeTest.joinWithFileSeparator(new String[] { "com",
        "tc", "test", "DataDirectoryHelperTest" }));
    if (this.expectedDir.exists()) FileUtils.deleteDirectory(this.expectedDir);
    assertTrue(this.expectedDir.mkdirs());

    this.helper = new DataDirectoryHelper(getClass(), root);
  }

  public void testFailsIfNonexistent() throws Exception {
    assertTrue(this.expectedDir.delete());
    assertFalse(this.expectedDir.exists());

    try {
      this.helper.getDirectory();
      fail("Didn't get exception on getDirectory() with no directory there");
    } catch (FileNotFoundException fnfe) {
      // ok
    }
  }

  public void testGetDirectory() throws Exception {
    assertTrue(this.expectedDir.exists());

    File dataFile = new File(this.expectedDir, "foo.txt");
    assertTrue(dataFile.createNewFile());
    assertTrue(dataFile.exists());

    File theDirectory = this.helper.getDirectory();
    assertEquals(this.expectedDir.getAbsolutePath(), theDirectory.getAbsolutePath());
    assertTrue(theDirectory.exists());
    assertTrue(dataFile.exists());

    theDirectory = this.helper.getDirectory();
    assertEquals(this.expectedDir.getAbsolutePath(), theDirectory.getAbsolutePath());
    assertTrue(theDirectory.exists());
    assertTrue(dataFile.exists());
  }

  public void testGetFile() throws Exception {
    File theFile = new File(this.expectedDir, "foo.txt");
    assertTrue(theFile.createNewFile());
    assertTrue(theFile.exists());

    File fromHelper = this.helper.getFile("foo.txt");
    assertTrue(theFile.exists());
    assertEquals(theFile.getAbsolutePath(), fromHelper.getAbsolutePath());

    try {
      this.helper.getFile("nonexistent.txt");
      fail("Didn't get exception on get of nonexistent file");
    } catch (FileNotFoundException fnfe) {
      // ok
    }
  }

}