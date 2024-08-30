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
package com.tc.object.config;

import com.tc.config.schema.CommonL1Config;
import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.config.schema.setup.L1ConfigurationSetupManager;
import com.tc.net.core.SecurityInfo;
import com.tc.object.Portability;
import com.tc.properties.ReconnectConfig;
import com.tc.security.PwProvider;

/**
 * Knows how to interpret the terracotta client config and tell you things like whether a class is portable. This
 * interface extends DSOApplicationConfig which is a much simpler interface suitable for manipulating the config from
 * the perspective of generating a configuration file.
 */
public interface DSOClientConfigHelper extends DSOMBeanConfig {
  String[] processArguments();

  String rawConfigText();

  Class getChangeApplicator(Class clazz);

  boolean addTunneledMBeanDomain(String tunneledMBeanDomain);

  TransparencyClassSpec getOrCreateSpec(String className, String applicator);

  TransparencyClassSpec getSpec(String className);

  int getFaultCount();

  void setFaultCount(int count);

  boolean isUseNonDefaultConstructor(Class clazz);

  CommonL1Config getNewCommonL1Config();

  Portability getPortability();

  void removeSpec(String className);

  public ReconnectConfig getL1ReconnectProperties(final PwProvider pwProvider) throws ConfigurationSetupException;

  public void validateGroupInfo(final PwProvider pwProvider) throws ConfigurationSetupException;

  public void validateClientServerCompatibility(final PwProvider pwProvider, SecurityInfo securityInfo)
      throws ConfigurationSetupException;

  L1ConfigurationSetupManager reloadServersConfiguration() throws ConfigurationSetupException;

  SecurityInfo getSecurityInfo();
}
