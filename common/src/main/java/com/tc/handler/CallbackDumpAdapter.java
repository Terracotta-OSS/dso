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
package com.tc.handler;

import com.tc.logging.CallbackOnExitHandler;
import com.tc.logging.CallbackOnExitState;
import com.tc.text.DumpLoggerWriter;
import com.tc.text.PrettyPrintable;
import com.tc.text.PrettyPrinterImpl;

import java.io.PrintWriter;

public class CallbackDumpAdapter implements CallbackOnExitHandler {

  private final PrettyPrintable dumpObject;

  public CallbackDumpAdapter(PrettyPrintable dumpObject) {
    this.dumpObject = dumpObject;
  }

  @Override
  public void callbackOnExit(CallbackOnExitState state) {
    DumpLoggerWriter writer = new DumpLoggerWriter();
    writer.write("\n***********************************************************************************\n");
    PrintWriter pw = new PrintWriter(writer);
    PrettyPrinterImpl prettyPrinter = new PrettyPrinterImpl(pw);
    prettyPrinter.autoflush(false);
    prettyPrinter.visit(dumpObject);
    writer.flush();
  }
}
