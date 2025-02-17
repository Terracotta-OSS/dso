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
package com.tc.test.config.model;

import org.apache.commons.io.IOUtils;
import org.terracotta.test.util.TestBaseUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Config class for configuring the use of Byteman in the test framework.
 */
public class BytemanConfig {
  private String script = null;

  /**
   * Set the resource location of the byteman script to use.
   * 
   * @param script script resource path
   */
  public void setScript(String script) {
    if (script == null) { throw new IllegalArgumentException("Script should not be null"); }

    this.script = script.startsWith("/") ? script : "/" + script;
  }

  /**
   * Get the currently set script resource path
   * 
   * @return script resource path
   */
  public String getScript() {
    return script;
  }

  /**
   * Add the Byteman configuration to the jvmArgs.
   * <br>
   * Note: This is a noop if a script is not configured.
   * 
   * @param jvmArgs the list to add the byteman configuration to
   * @param tempDirectory a temporary folder to store the byteman script in.
   */
  public void addTo(List<String> jvmArgs, File tempDirectory) {
    if (getScript() == null) { return; }

    try {
      StringBuilder builder = new StringBuilder();
      builder.append("-javaagent:");
      builder.append(TestBaseUtil.jarFor(org.jboss.byteman.agent.Main.class));

      builder.append("=script:");
      builder.append(makeScript(tempDirectory).getAbsolutePath());

      jvmArgs.add(builder.toString());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private File makeScript(File tempDirectory) throws IOException {
    InputStream is = BytemanConfig.class.getResourceAsStream(getScript());
    File scriptFile = File.createTempFile("script", ".btm", tempDirectory);
    FileOutputStream fos = new FileOutputStream(scriptFile);
    try {
      IOUtils.copy(is, fos);
    } finally {
      is.close();
      fos.close();
    }
    return scriptFile;
  }
}
