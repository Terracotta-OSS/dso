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

import com.tc.config.schema.beanfactory.ConfigBeanFactory;
import com.tc.config.schema.repository.MutableBeanRepository;
import com.tc.config.schema.setup.sources.ConfigurationSource;
import com.tc.logging.TCLogging;

/**
 * A {@link ConfigurationCreator} that creates config appropriate for tests only.
 */
public class TestConfigurationCreator extends StandardXMLFileConfigurationCreator {

  private final boolean trustedSource;

  public TestConfigurationCreator(final ConfigurationSpec configurationSpec, final ConfigBeanFactory beanFactory,
                                  boolean trustedSource) {
    super(TCLogging.getLogger(TestConfigurationCreator.class), configurationSpec, beanFactory, null);
    this.trustedSource = trustedSource;
  }

  @Override
  protected ConfigurationSource[] getConfigurationSources(String configrationSpec) {
    ConfigurationSource[] out = new ConfigurationSource[1];
    out[0] = new TestConfigurationSource();
    return out;
  }

  @Override
  public void createConfigurationIntoRepositories(MutableBeanRepository l1BeanRepository,
                                                  MutableBeanRepository l2sBeanRepository,
                                                  MutableBeanRepository systemBeanRepository,
                                                  MutableBeanRepository tcPropertiesRepository, boolean isClient)
      throws ConfigurationSetupException {
    loadConfigAndSetIntoRepositories(l1BeanRepository, l2sBeanRepository, systemBeanRepository, tcPropertiesRepository,
                                     isClient);
  }

  @Override
  public String describeSources() {
    return "Dynamically-generated configuration for tests";
  }

  @Override
  public boolean loadedFromTrustedSource() {
    return this.trustedSource;
  }

  @Override
  public String reloadServersConfiguration(MutableBeanRepository l2sBeanRepository, boolean b, boolean reportToConsole) {
    throw new UnsupportedOperationException();
  }

}
