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

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResourceBundleHelper {
  private ResourceBundle bundle;

  public ResourceBundleHelper(Class clas) {
    bundle = AbstractResourceBundleFactory.getBundle(clas);
  }

  public ResourceBundleHelper(Object instance) {
    Class clas = instance.getClass();
    while (true) {
      try {
        bundle = AbstractResourceBundleFactory.getBundle(clas);
        break;
      } catch (MissingResourceException e) {
        if ((clas = clas.getSuperclass()) == null) { throw new RuntimeException("Missing bundle for type '"
                                                                                + instance.getClass() + "'"); }
      }
    }
  }

  public Object getObject(final String key) {
    Assert.assertNotNull(key);
    return bundle.getObject(key);
  }

  public String getString(final String key) {
    Assert.assertNotNull(key);
    return bundle.getString(key);
  }

  public String format(final String key, Object[] args) {
    Assert.assertNotNull(key);
    String fmt = getString(key);
    Assert.assertNotNull(fmt);
    return MessageFormat.format(fmt, args);
  }
}
