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
package com.tc.util.runtime;

import com.tc.lcp.LinkedJavaProcess;
import com.tc.process.StreamCollector;
import com.tc.test.TCTestCase;
import com.tc.test.TestConfigObject;
import com.tc.util.runtime.ThreadDump.PID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ThreadDumpTest extends TCTestCase {

  public ThreadDumpTest() {
    if (Vm.isIBM()) {
      disableTest();
    }
  }

  public void testDump() throws IOException, InterruptedException {
    LinkedJavaProcess process = new LinkedJavaProcess(ThreadDump.class.getName());

    List args = new ArrayList<String>();
    args.add("-D" + TestConfigObject.TC_BASE_DIR + "=" + System.getProperty(TestConfigObject.TC_BASE_DIR));
    args.add("-D" + TestConfigObject.PROPERTY_FILE_LIST_PROPERTY_NAME + "="
             + System.getProperty(TestConfigObject.PROPERTY_FILE_LIST_PROPERTY_NAME));
    if (Vm.isIBM()) {
      args.add("-Xdump:console");
      args.add("-Xdump:java:file=-");
    }
    process.addAllJvmArgs(args);

    System.err.println("JAVA ARGS: " + args);

    process.start();

    StreamCollector err = new StreamCollector(process.STDERR());
    StreamCollector out = new StreamCollector(process.STDOUT());

    err.start();
    out.start();

    process.waitFor();

    err.join();
    out.join();

    String stderr = err.toString();
    String stdout = out.toString();

    System.out.println("**** STDOUT BEGIN ****\n" + stdout + "\n**** STDOUT END ****");
    System.out.println("**** STDERR BEGIN ****\n" + stderr + "\n**** STDERR END ****");

    String expect = Vm.isIBM() ? "^^^^^^^^ console dump ^^^^^^^^" : "full thread dump";

    assertTrue(stderr.toLowerCase().indexOf(expect) >= 0 || stdout.toLowerCase().indexOf(expect) >= 0);
  }

  // public void testPidMechanismsAreSame() {
  // int jniPID = GetPid.getInstance().getPid();
  // int fallback = ThreadDump.getPIDUsingFallback().getPid();
  // assertEquals(jniPID, fallback);
  // }

  public void testFindAllJavaPIDs() {
    Set<PID> allPIDs = ThreadDump.findAllJavaPIDs();
    System.err.println("ALL: " + allPIDs);

    PID pid = ThreadDump.getPID();
    System.err.println("PID: " + pid);

    assertTrue(allPIDs.contains(pid));
  }
}
