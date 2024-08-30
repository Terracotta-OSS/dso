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
package com.tc.objectserver.control;

import org.apache.commons.io.IOUtils;

import com.tc.lcp.LinkedJavaProcess;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

public class ExtraL1ProcessControl extends ExtraProcessServerControl {

  private final Class        mainClass;
  private final List<String> mainArgs;
  private final File         directory;

  public ExtraL1ProcessControl(String l2Host, int tsaPort, Class mainClass, String configFileLoc,
                               List<String> mainArgs, File directory, List extraJvmArgs) {
    super(new DebugParams(), l2Host, tsaPort, 0, configFileLoc, true, extraJvmArgs);
    this.mainClass = mainClass;
    this.mainArgs = mainArgs == null ? Collections.EMPTY_LIST : mainArgs;
    this.directory = directory;

    setJVMArgs();
  }

  public ExtraL1ProcessControl(String l2Host, int tsaPort, Class mainClass, String configFileLoc,
                               List<String> mainArgs, File directory, List extraJvmArgs, boolean mergeOutput) {
    super(new DebugParams(), l2Host, tsaPort, 0, configFileLoc, mergeOutput, extraJvmArgs);
    this.mainClass = mainClass;
    this.mainArgs = mainArgs == null ? Collections.EMPTY_LIST : mainArgs;
    this.directory = directory;

    setJVMArgs();
  }

  @Override
  public File getJavaHome() {
    return javaHome;
  }

  @Override
  protected LinkedJavaProcess createLinkedJavaProcess() {
    LinkedJavaProcess out = super.createLinkedJavaProcess();
    out.setDirectory(this.directory);
    return out;
  }

  private void setJVMArgs() {
    try {
      this.jvmArgs.add("-Dtc.classpath=" + createTcClassPath());
      this.jvmArgs.add("-Dtc.config=" + super.configFileLoc);
    } catch (Exception e) {
      e.printStackTrace();
      Assert.fail("Can't set JVM args");
    }
  }

  private String createTcClassPath() {
    File tcClassPathFile = new File(directory, "tc.classpath." + this.hashCode() + ".txt");
    FileOutputStream fos = null;
    try {
      fos = new FileOutputStream(tcClassPathFile);
      IOUtils.write(System.getProperty("java.class.path"), fos);
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      IOUtils.closeQuietly(fos);
    }
    return tcClassPathFile.toURI().toString();
  }

  @Override
  protected String getMainClassName() {
    return mainClass.getName();
  }

  @Override
  protected List<String> getMainClassArguments() {
    return mainArgs;
  }

  @Override
  public boolean isRunning() {
    try {
      process.exitValue();
      return false;
    } catch (IllegalThreadStateException e) {
      System.out.println("Expected " + e.getMessage());
      return true;
    }
  }

  @Override
  public void attemptForceShutdown() throws Exception {
    // TODO:: comeback
    process.destroy();
  }
}
