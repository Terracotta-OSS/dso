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

/**
 * A little utility class that will write class files to disk for uninstrumented class files.
 */
public class InitialClassDumper extends AbstractClassDumper {
  
  public final static InitialClassDumper INSTANCE = new InitialClassDumper();
  
  private InitialClassDumper() {
    // make the default constructor private to turn this class into a singleton
  }

  @Override
  protected String getDumpDirectoryName() {
    return "initial";
  }

  @Override
  protected String getPropertyName() {
    return "tc.classloader.writeToDisk.initial";
  }
}
