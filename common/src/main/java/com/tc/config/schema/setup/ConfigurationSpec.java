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
package com.tc.config.schema.setup;

import com.tc.config.schema.setup.StandardConfigurationSetupManagerFactory.ConfigMode;
import com.tc.util.Assert;

import java.io.File;

public class ConfigurationSpec {

  private final String     baseConfigSpec;
  private final String     serverTopologyOverrideConfigSpec;
  private final ConfigMode configMode;
  private final File       workingDir;

  public ConfigurationSpec(String baseConfigSpec, ConfigMode configMode, File workingDir) {
    this(baseConfigSpec, null, configMode, workingDir);
  }

  public ConfigurationSpec(String baseConfigSpec, String serverTopologyOverrideConfigSpec, ConfigMode configMode,
                           File workingDir) {
    Assert.assertNotNull(baseConfigSpec);
    Assert.assertNotBlank(baseConfigSpec);
    Assert.assertNotNull(workingDir);
    this.baseConfigSpec = baseConfigSpec;
    this.serverTopologyOverrideConfigSpec = serverTopologyOverrideConfigSpec;
    this.configMode = configMode;
    this.workingDir = workingDir;
  }

  public String getBaseConfigSpec() {
    return baseConfigSpec;
  }

  public String getServerTopologyOverrideConfigSpec() throws ConfigurationSetupException {
    if (!shouldOverrideServerTopology()) { throw new ConfigurationSetupException(
                                                                                 "Not suppose to override the server topology config : "
                                                                                     + this.configMode
                                                                                     + "; "
                                                                                     + this.serverTopologyOverrideConfigSpec); }
    return serverTopologyOverrideConfigSpec;
  }

  public File getWorkingDir() {
    return workingDir;
  }

  public boolean shouldOverrideServerTopology() {
    return (this.configMode != ConfigMode.EXPRESS_L1) && (this.serverTopologyOverrideConfigSpec != null);
  }
}
