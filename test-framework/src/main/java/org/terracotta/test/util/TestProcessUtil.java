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

import static org.terracotta.test.util.JvmProcessUtil.sendSignal;

import org.terracotta.test.util.JvmProcessUtil.Signal;

import com.tc.lcp.LinkedJavaProcess;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

public class TestProcessUtil {
  
  public static void pauseProcess(LinkedJavaProcess process, long pauseTimeMillis) throws InterruptedException {
    int pid = getPid(process);
    if (pid < 0) {
      LogUtil.info(TestProcessUtil.class, "PID negative Cannot Pause a Process :" + process.toString());
      return;
    }
    try {
      sendSignal(Signal.SIGSTOP, pid);
      long time = System.currentTimeMillis();
      LogUtil.debug(TestProcessUtil.class, "Pausing Process PID : " + pid);
      try {
        TimeUnit.MILLISECONDS.sleep(pauseTimeMillis);
      } finally {
        sendSignal(Signal.SIGCONT, pid);
        LogUtil.debug(TestProcessUtil.class, "Resuming Process PID : " + pid + " in "
                                             + (System.currentTimeMillis() - time));
      }
    } catch (IOException e) {
      LogUtil.info(TestProcessUtil.class, "Pause : failed for PID : " + pid + " Becoz of " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void pauseProcess(LinkedJavaProcess process) throws InterruptedException {
    int pid = getPid(process);
    if (pid < 0) {
      LogUtil.info(TestProcessUtil.class, "PID negative Cannot Pause a Process :" + process.toString());
      return;
    }
    try {
      sendSignal(Signal.SIGSTOP, pid);
      LogUtil.debug(TestProcessUtil.class, "Pausing Process PID : " + pid);
    } catch (IOException e) {
      LogUtil.info(TestProcessUtil.class, "Pause : failed for PID : " + pid + " Becoz of " + e.getMessage());
      e.printStackTrace();
    }
  }

  public static void unpauseProcess(LinkedJavaProcess process) throws InterruptedException {
    int pid = getPid(process);
    if (pid < 0) {
      LogUtil.info(TestProcessUtil.class, "PID negative Cannot Pause a Process :" + process.toString());
      return;
    }
    try {
      sendSignal(Signal.SIGCONT, pid);
      LogUtil.debug(TestProcessUtil.class, "Resuming Process PID : " + pid);
    } catch (IOException e) {
      LogUtil.info(TestProcessUtil.class, "UnPause : failed for PID : " + pid + " Becoz of " + e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Needed a Good Implementation here. This is not good
   */
  private static int getPid(LinkedJavaProcess process) {
    try {
      Field processField = LinkedJavaProcess.class.getDeclaredField("process");
      processField.setAccessible(true);
      Object internalProcessObject = processField.get(process);
      Field pidField = internalProcessObject.getClass().getDeclaredField("pid");
      pidField.setAccessible(true);
      int pid = (Integer) pidField.get(internalProcessObject);
      return pid;
    } catch (Exception e) {
      LogUtil.info(TestProcessUtil.class, "Pause : failed for Process : " + process + " Becoz of " + e.getMessage());
      e.printStackTrace();
    }
    return -1;
  }

}
