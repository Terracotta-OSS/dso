/* 
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at 
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is 
 *      Terracotta, Inc., a Software AG company
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
