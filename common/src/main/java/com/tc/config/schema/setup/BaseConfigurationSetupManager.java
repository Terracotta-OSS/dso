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

import com.tc.config.schema.IllegalConfigurationChangeHandler;
import com.tc.config.schema.context.ConfigContext;
import com.tc.config.schema.context.StandardConfigContext;
import com.tc.config.schema.defaults.DefaultValueProvider;
import com.tc.config.schema.repository.BeanRepository;
import com.tc.config.schema.repository.MutableBeanRepository;
import com.tc.config.schema.repository.StandardBeanRepository;
import com.tc.config.schema.utils.XmlObjectComparator;
import com.tc.util.Assert;
import com.terracottatech.config.Client;
import com.terracottatech.config.Servers;
import com.terracottatech.config.TcProperties;

import java.io.File;

/**
 * A base class for all TVS configuration setup managers.
 */
public class BaseConfigurationSetupManager {
  private final String[]                          args;
  private final ConfigurationCreator              configurationCreator;
  private final MutableBeanRepository             clientBeanRepository;
  private final MutableBeanRepository             serversBeanRepository;
  private final MutableBeanRepository             systemBeanRepository;
  private final MutableBeanRepository             tcPropertiesRepository;

  protected final DefaultValueProvider            defaultValueProvider;
  private final XmlObjectComparator               xmlObjectComparator;
  private final IllegalConfigurationChangeHandler illegalConfigurationChangeHandler;

  public BaseConfigurationSetupManager(ConfigurationCreator configurationCreator,
                                       DefaultValueProvider defaultValueProvider,
                                       XmlObjectComparator xmlObjectComparator,
                                       IllegalConfigurationChangeHandler illegalConfigurationChangeHandler) {
    this((String[]) null, configurationCreator, defaultValueProvider, xmlObjectComparator,
         illegalConfigurationChangeHandler);
  }

  public BaseConfigurationSetupManager(String[] args, ConfigurationCreator configurationCreator,
                                       DefaultValueProvider defaultValueProvider,
                                       XmlObjectComparator xmlObjectComparator,
                                       IllegalConfigurationChangeHandler illegalConfigurationChangeHandler) {
    Assert.assertNotNull(configurationCreator);
    Assert.assertNotNull(defaultValueProvider);
    Assert.assertNotNull(xmlObjectComparator);
    Assert.assertNotNull(illegalConfigurationChangeHandler);

    this.args = args;
    this.configurationCreator = configurationCreator;
    this.systemBeanRepository = new StandardBeanRepository(System.class);
    this.clientBeanRepository = new StandardBeanRepository(Client.class);
    this.serversBeanRepository = new StandardBeanRepository(Servers.class);
    this.tcPropertiesRepository = new StandardBeanRepository(TcProperties.class);

    this.defaultValueProvider = defaultValueProvider;
    this.xmlObjectComparator = xmlObjectComparator;
    this.illegalConfigurationChangeHandler = illegalConfigurationChangeHandler;
  }

  public String[] processArguments() {
    return args;
  }

  protected final MutableBeanRepository clientBeanRepository() {
    return this.clientBeanRepository;
  }

  public final MutableBeanRepository serversBeanRepository() {
    return this.serversBeanRepository;
  }

  protected final MutableBeanRepository systemBeanRepository() {
    return this.systemBeanRepository;
  }

  protected final MutableBeanRepository tcPropertiesRepository() {
    return this.tcPropertiesRepository;
  }

  protected final XmlObjectComparator xmlObjectComparator() {
    return this.xmlObjectComparator;
  }

  protected final ConfigurationCreator configurationCreator() {
    return this.configurationCreator;
  }

  protected final void runConfigurationCreator(boolean isClient) throws ConfigurationSetupException {
    this.configurationCreator.createConfigurationIntoRepositories(clientBeanRepository, serversBeanRepository,
                                                                  systemBeanRepository, tcPropertiesRepository,
                                                                  isClient);
  }

  public final ConfigContext createContext(BeanRepository beanRepository, File configFilePath) {
    Assert.assertNotNull(beanRepository);
    return new StandardConfigContext(beanRepository, this.defaultValueProvider, this.illegalConfigurationChangeHandler);
  }

}
