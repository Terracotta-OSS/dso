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
package com.tc.config.schema;

import com.tc.util.Assert;

/**
 * Represents the type of the license.
 */
public class LicenseType {

  public static final LicenseType NONE       = new LicenseType("none");
  public static final LicenseType TRIAL      = new LicenseType("trial");
  public static final LicenseType PRODUCTION = new LicenseType("production");

  private final String            type;

  private LicenseType(String type) {
    Assert.assertNotBlank(type);
    this.type = type;
  }

  @Override
  public boolean equals(Object that) {
    return (that instanceof LicenseType) && ((LicenseType) that).type.equals(this.type);
  }

  @Override
  public int hashCode() {
    return this.type.hashCode();
  }

  @Override
  public String toString() {
    return this.type;
  }
  
  public String getType() {
    return this.type;
  }

}
