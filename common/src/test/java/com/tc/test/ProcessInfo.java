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

import com.tc.process.Exec;
import com.tc.util.runtime.Os;

import java.io.File;

public class ProcessInfo {
  public static String ps_grep_java() {
    File jps = getProgram("jps");
    if (jps.isFile()) {
      try {
        Exec.Result result = Exec.execute(new String[] {jps.getAbsolutePath(), "-mlv"});

        return result.getStdout().trim() + "\n" + result.getStderr().trim();
      } catch (Exception e) {
        e.printStackTrace();
        return "jps failure";
      }
    } else {
      return "jps not found";
    }
  }

  private static File getProgram(String prog) {
    File javaHome = new File(System.getProperty("java.home"));
    if (javaHome.getName().equals("jre")) {
      javaHome = javaHome.getParentFile();
    }

    if (Os.isWindows()) {
      return new File(new File(javaHome, "bin"), prog + ".exe");
    } else {
      return new File(new File(javaHome, "bin"), prog);
    }
  }
}