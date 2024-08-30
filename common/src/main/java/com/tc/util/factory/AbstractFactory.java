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
package com.tc.util.factory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public abstract class AbstractFactory {
  public static AbstractFactory getFactory(String id, Class defaultImpl) {
    String factoryClassName = findFactoryClassName(id);
    AbstractFactory factory = null;

    if (factoryClassName != null) {
      try {
        factory = (AbstractFactory) Class.forName(factoryClassName).newInstance();
      } catch (Exception e) {
        throw new RuntimeException("Could not instantiate '" + factoryClassName + "'", e);
      }
    }

    if (factory == null) {
      try {
        factory = (AbstractFactory) defaultImpl.newInstance();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    return factory;
  }

  private static String findFactoryClassName(String id) {
    String serviceId = "META-INF/services/" + id;
    InputStream is = null;

    ClassLoader cl = AbstractFactory.class.getClassLoader();
    if (cl != null) {
      is = cl.getResourceAsStream(serviceId);
    }

    if (is == null) { return System.getProperty(id); }

    BufferedReader rd;
    try {
      rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }

    String factoryClassName = null;
    try {
      factoryClassName = rd.readLine();
      rd.close();
    } catch (IOException x) {
      return System.getProperty(id);
    }

    if (factoryClassName != null && !"".equals(factoryClassName)) { return factoryClassName; }

    return System.getProperty(id);
  }
}
