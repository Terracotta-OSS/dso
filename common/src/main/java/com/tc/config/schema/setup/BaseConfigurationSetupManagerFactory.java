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
import com.tc.config.schema.beanfactory.ConfigBeanFactory;
import com.tc.config.schema.beanfactory.TerracottaDomainConfigurationDocumentBeanFactory;
import com.tc.config.schema.defaults.DefaultValueProvider;
import com.tc.config.schema.defaults.SchemaDefaultValueProvider;
import com.tc.config.schema.utils.StandardXmlObjectComparator;
import com.tc.config.schema.utils.XmlObjectComparator;
import com.tc.net.core.SecurityInfo;
import com.tc.util.Assert;

/**
 * A base class for all {@link com.tc.config.schema.setup.ConfigurationSetupManagerFactory} instances.
 */
public abstract class BaseConfigurationSetupManagerFactory implements ConfigurationSetupManagerFactory {

  protected final IllegalConfigurationChangeHandler illegalChangeHandler;
  
  protected final ConfigBeanFactory    beanFactory;
  protected final DefaultValueProvider defaultValueProvider;
  protected final XmlObjectComparator  xmlObjectComparator;

  public BaseConfigurationSetupManagerFactory(IllegalConfigurationChangeHandler illegalConfigurationChangeHandler) {
    Assert.assertNotNull(illegalConfigurationChangeHandler);
    
    this.illegalChangeHandler = illegalConfigurationChangeHandler;
    
    this.beanFactory = new TerracottaDomainConfigurationDocumentBeanFactory();
    this.defaultValueProvider = new SchemaDefaultValueProvider();
    this.xmlObjectComparator = new StandardXmlObjectComparator();
  }

  @Override
  public L1ConfigurationSetupManager getL1TVSConfigurationSetupManager() throws ConfigurationSetupException {
    return getL1TVSConfigurationSetupManager(new SecurityInfo());
  }
}
