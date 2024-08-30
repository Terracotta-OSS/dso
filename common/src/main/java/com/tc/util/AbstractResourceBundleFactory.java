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
package com.tc.util;

import com.tc.util.factory.AbstractFactory;

import java.util.ResourceBundle;

public abstract class AbstractResourceBundleFactory extends AbstractFactory implements ResourceBundleFactory {
  private static ResourceBundleFactory bundleFactory;
  private static String FACTORY_SERVICE_ID = "com.tc.util.ResourceBundleFactory";
  private static Class STANDARD_BUNDLE_FACTORY_CLASS = StandardResourceBundleFactory.class;
  
  public static AbstractResourceBundleFactory getFactory() {
    return (AbstractResourceBundleFactory)getFactory(FACTORY_SERVICE_ID, STANDARD_BUNDLE_FACTORY_CLASS);
  }

  @Override
  public abstract ResourceBundle createBundle(Class clas);
  
  public static ResourceBundle getBundle(Class clas) {
    if(bundleFactory == null) {
      bundleFactory = getFactory();
    }
    return bundleFactory.createBundle(clas);
  }
}
