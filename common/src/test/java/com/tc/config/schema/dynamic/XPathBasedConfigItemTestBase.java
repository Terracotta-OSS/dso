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
package com.tc.config.schema.dynamic;

import org.apache.xmlbeans.XmlObject;

import com.tc.config.schema.MockXmlObject;
import com.tc.config.schema.context.MockConfigContext;
import com.tc.test.TCTestCase;

/**
 * A base for all {@link XPathBasedConfigItem} tests.
 */
public abstract class XPathBasedConfigItemTestBase extends TCTestCase {

  protected MockConfigContext context;
  protected String            xpath;

  protected MockXmlObject     bean;
  protected MockXmlObject     subBean;

  @Override
  protected void setUp() throws Exception {
    this.context = new MockConfigContext();
    this.xpath = "foobar/baz";

    this.bean = new MockXmlObject();
    this.context.setReturnedBean(this.bean);

    this.subBean = createSubBean();
    this.bean.setReturnedSelectPath(new XmlObject[] { this.subBean });
  }

  protected abstract MockXmlObject createSubBean() throws Exception;

}
