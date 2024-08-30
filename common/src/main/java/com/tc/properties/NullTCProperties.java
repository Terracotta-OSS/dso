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
package com.tc.properties;

import java.util.Map;
import java.util.Properties;

public class NullTCProperties implements TCProperties {

  public static final TCProperties INSTANCE = new NullTCProperties();

  private NullTCProperties() {
    //
  }

  @Override
  public int getInt(String key) {
    //
    return 0;
  }

  @Override
  public int getInt(String key, int defaultValue) {
    //
    return 0;
  }

  @Override
  public long getLong(String key) {
    //
    return 0;
  }

  @Override
  public long getLong(String key, long defaultValue) {
    //
    return 0;
  }

  @Override
  public boolean getBoolean(String key) {
    //
    return false;
  }

  @Override
  public boolean getBoolean(String key, boolean defaultValue) {
    //
    return false;
  }

  @Override
  public String getProperty(String key) {
    //
    return null;
  }

  @Override
  public TCProperties getPropertiesFor(String key) {
    //
    return null;
  }

  @Override
  public String getProperty(String key, boolean missingOkay) {
    //
    return null;
  }

  @Override
  public float getFloat(String key) {
    //
    return 0;
  }

  @Override
  public Properties addAllPropertiesTo(Properties properties) {
    //
    return null;
  }

  @Override
  public void overwriteTcPropertiesFromConfig(Map<String, String> props) {
    //

  }

  @Override
  public void setProperty(String key, String value) {
    //

  }

}
