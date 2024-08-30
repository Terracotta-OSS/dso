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
package com.tc.config.schema.context;

import com.tc.config.schema.IllegalConfigurationChangeHandler;
import com.tc.config.schema.defaults.DefaultValueProvider;
import com.tc.config.schema.dynamic.ConfigItem;
import com.tc.config.schema.listen.ConfigurationChangeListener;
import com.tc.config.schema.repository.BeanRepository;
import com.tc.util.Assert;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;

/**
 * Binds together a {@link BeanRepository} and a {@link DefaultValueProvider}.
 */
public class StandardConfigContext implements ConfigContext {

  private final BeanRepository                    beanRepository;
  private final DefaultValueProvider              defaultValueProvider;
  private final IllegalConfigurationChangeHandler illegalConfigurationChangeHandler;

  public StandardConfigContext(BeanRepository beanRepository, DefaultValueProvider defaultValueProvider,
                               IllegalConfigurationChangeHandler illegalConfigurationChangeHandler) {
    Assert.assertNotNull(beanRepository);
    Assert.assertNotNull(defaultValueProvider);
    Assert.assertNotNull(illegalConfigurationChangeHandler);

    this.beanRepository = beanRepository;
    this.defaultValueProvider = defaultValueProvider;
    this.illegalConfigurationChangeHandler = illegalConfigurationChangeHandler;
  }

  @Override
  public IllegalConfigurationChangeHandler illegalConfigurationChangeHandler() {
    return this.illegalConfigurationChangeHandler;
  }

  @Override
  public void ensureRepositoryProvides(Class theClass) {
    beanRepository.ensureBeanIsOfClass(theClass);
  }

  @Override
  public boolean hasDefaultFor(String xpath) throws XmlException {
    return this.defaultValueProvider.possibleForXPathToHaveDefault(xpath)
           && this.defaultValueProvider.hasDefault(this.beanRepository.rootBeanSchemaType(), xpath);
  }

  @Override
  public XmlObject defaultFor(String xpath) throws XmlException {
    return this.defaultValueProvider.defaultFor(this.beanRepository.rootBeanSchemaType(), xpath);
  }

  @Override
  public boolean isOptional(String xpath) throws XmlException {
    return this.defaultValueProvider.isOptional(this.beanRepository.rootBeanSchemaType(), xpath);
  }

  @Override
  public XmlObject bean() {
    return beanRepository.bean();
  }

  @Override
  public Object syncLockForBean() {
    return this.beanRepository;
  }

  @Override
  public void itemCreated(ConfigItem item) {
    if (item instanceof ConfigurationChangeListener) this.beanRepository
        .addListener((ConfigurationChangeListener) item);
  }

  @Override
  public String toString() {
    return "<ConfigContext around repository: " + this.beanRepository + ">";
  }

}
