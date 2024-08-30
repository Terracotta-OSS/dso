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
package com.tc.config.test.schema;

public class GarbageCollectionConfigBuilder extends BaseConfigBuilder {

  private static final String[] ALL_PROPERTIES = new String[] { "enabled", "interval", "verbose" };

  public GarbageCollectionConfigBuilder() {
    super(3, ALL_PROPERTIES);
  }

  public void setGCEnabled(boolean data) {
    setProperty("enabled", data);
  }

  public void setGCEnabled(String data) {
    setProperty("enabled", data);
  }

  public void setGCVerbose(boolean data) {
    setProperty("verbose", data);
  }

  public void setGCVerbose(String data) {
    setProperty("verbose", data);
  }

  public void setGCInterval(int data) {
    setProperty("interval", data);
  }

  @Override
  public String toString() {
    String out = "";

    out += openElement("garbage-collection");

    for (String e : ALL_PROPERTIES) {
      out += element(e);
    }

    out += closeElement("garbage-collection");

    return out;
  }

}
