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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TCFileImpl implements TCFile {
  private File pathToFile;
  
  public TCFileImpl(File pathToFile) {
    this.pathToFile = pathToFile;
  }
  
  public TCFileImpl(TCFile location, String fileName) {
    pathToFile = new File(location.getFile(), fileName);
  }

  @Override
  public boolean exists() {
    return pathToFile.exists();
  }

  @Override
  public void forceMkdir() throws IOException {
    FileUtils.forceMkdir(pathToFile);
  }

  @Override
  public boolean createNewFile() throws IOException {
    return pathToFile.createNewFile();
  }

  @Override
  public File getFile() {
    return pathToFile;
  }

  @Override
  public TCFile createNewTCFile(TCFile location, String fileName) {
    return new TCFileImpl(location, fileName);
  }

  @Override
  public String toString() {
    return pathToFile.toString();
  }
}
