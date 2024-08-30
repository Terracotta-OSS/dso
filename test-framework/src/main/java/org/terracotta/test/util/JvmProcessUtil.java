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
package org.terracotta.test.util;

import java.io.IOException;

public class JvmProcessUtil {

  public static enum Signal {
    SIGSTOP, SIGCONT
  }

  public static void sendSignal(Signal signal, int pid) throws InterruptedException, IOException {
    int status = 0;
    switch (signal) {
      case SIGCONT:
        status = Runtime.getRuntime().exec("kill -SIGCONT " + pid).waitFor();
        LogUtil.debug(JvmProcessUtil.class, "PID :" + pid + " Status of Signal SIGCONT:  " + status);
        break;
      case SIGSTOP:
        status = Runtime.getRuntime().exec("kill -SIGSTOP " + pid).waitFor();
        LogUtil.debug(JvmProcessUtil.class, "PID :" + pid + "Status of Signal SIGSTOP : " + status);
        break;
    }
  }

}
