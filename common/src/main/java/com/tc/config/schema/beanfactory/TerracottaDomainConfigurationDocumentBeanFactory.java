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
package com.tc.config.schema.beanfactory;

import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;

import com.tc.config.Loader;
import com.tc.util.Assert;
import com.terracottatech.config.TcConfigDocument;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A {@link ConfigBeanFactory} that creates {@link BeanWithErrors} beans.
 */
public class TerracottaDomainConfigurationDocumentBeanFactory implements ConfigBeanFactory {

  public TerracottaDomainConfigurationDocumentBeanFactory() {
    // Nothing here yet.
  }

  @Override
  public BeanWithErrors createBean(InputStream in, String sourceDescription) throws IOException, XmlException {
    Assert.assertNotBlank(sourceDescription);

    List errors = new ArrayList();
    XmlOptions options = createXmlOptions(errors, sourceDescription);
    Loader configLoader = new Loader();

    TcConfigDocument document = configLoader.parse(in, options);
    document.validate(options);
    return new BeanWithErrors(document, (XmlError[]) errors.toArray(new XmlError[errors.size()]));
  }

  public static XmlOptions createXmlOptions(List errors, String sourceDescription) {
    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(
                                                 TerracottaDomainConfigurationDocumentBeanFactory.class
                                                     .getClassLoader());
    try {
      XmlOptions options = new XmlOptions();
      options = options.setLoadLineNumbers();
      options = options.setDocumentSourceName(sourceDescription);
      options = options.setErrorListener(errors);
      return options;

    } finally {
      Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

  }
}
