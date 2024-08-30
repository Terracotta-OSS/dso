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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A little utility class that will write class files to disk.
 */
public abstract class AbstractClassDumper {

  private volatile File adaptedRoot = getFileRoot();

  public void setRoot(File root) {
    adaptedRoot = root;
  }

  public synchronized void write(String name, byte[] b) {
    if (adaptedRoot == null) { return; }

    name = name.replace('.', '/') + ".class";
    FileOutputStream fos = null;

    try {
      try {
        String pattern = File.separator.replaceAll("\\\\", "\\\\\\\\");
        String[] strings = new File(adaptedRoot, name).getAbsolutePath().split(pattern);

        final StringBuffer sb;
        if (adaptedRoot.getAbsolutePath().startsWith("/")) {
          sb = new StringBuffer("/");
        } else {
          sb = new StringBuffer();
        }

        for (int i = 0; i < strings.length - 1; i++) {
          sb.append(strings[i]);
          sb.append(File.separatorChar);
        }

        File dir = new File(sb.toString());
        if (!dir.exists() && !dir.mkdirs()) {
          throw new IOException("Couldn't create directory");
        }

        File outFile = new File(adaptedRoot, name);
        System.out.println("Writing resource: " + outFile);
        System.out.flush();
        fos = new FileOutputStream(outFile);
        fos.write(b);
      } finally {
        if (fos != null) {
          fos.close();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private File getFileRoot() {
    try {
      boolean writeToDisk = (System.getProperty(getPropertyName()) != null);
      if (!writeToDisk) { return null; }

      String userHome = System.getProperty("user.home");

      if (userHome != null) {
        File homeDir = new File(userHome);
        if (homeDir.isDirectory() && homeDir.canWrite()) { return new File(homeDir, getDumpDirectoryName()); }
      }

      return null;
    } catch (Exception e) {
      // you can get a SecurityException here, but we shouldn't blow up just b/c of that
      e.printStackTrace();
      return null;
    }
  }

  protected abstract String getDumpDirectoryName();

  protected abstract String getPropertyName();
}
