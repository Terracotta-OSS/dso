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
package com.tc.cli.command;

import java.io.IOException;
import java.io.Writer;

public abstract class BaseCommand implements Command {
  
  protected Writer writer;
  
  public BaseCommand(Writer writer) {
    this.writer = writer;
  }
  
  protected void println(String message) {
    try {
      writer.write(message);
      writer.write("\n");
      writer.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
