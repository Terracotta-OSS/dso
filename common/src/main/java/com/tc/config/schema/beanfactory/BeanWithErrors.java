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
import org.apache.xmlbeans.XmlObject;

import com.tc.util.Assert;

/**
 * An XML bean, plus a list of errors.
 */
public class BeanWithErrors {
  
  private final XmlObject bean;
  private final XmlError[] errors;
  
  public BeanWithErrors(XmlObject bean, XmlError[] errors) {
    Assert.assertNotNull(bean);
    Assert.assertNoNullElements(errors);
    
    this.bean = bean;
    this.errors = errors;
  }
  
  public XmlObject bean() {
    return this.bean;
  }
  
  public XmlError[] errors() {
    return this.errors;
  }

}
