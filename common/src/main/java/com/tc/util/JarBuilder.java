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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class JarBuilder extends ZipBuilder {

  private boolean isInit;
  
  public JarBuilder(File archiveFile) throws IOException {
    super(archiveFile, false);
  }

  @Override
  protected final ZipEntry createEntry(String name) {
    return new JarEntry(name);
  }
  
  @Override
  protected final ZipOutputStream getArchiveOutputStream(File archiveFile) throws IOException {
    if (isInit) super.getArchiveOutputStream(archiveFile); // throws Exception
    isInit = true;
    return new JarOutputStream(new BufferedOutputStream(new FileOutputStream(archiveFile)), new Manifest());
  }
}
