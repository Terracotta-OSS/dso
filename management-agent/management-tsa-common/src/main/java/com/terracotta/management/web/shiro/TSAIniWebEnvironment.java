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
package com.terracotta.management.web.shiro;

import org.apache.shiro.web.env.IniWebEnvironment;
import org.terracotta.management.security.web.shiro.TCWebIniSecurityManagerFactory;

/**
 * @author Ludovic Orban
 */
public class TSAIniWebEnvironment extends IniWebEnvironment {
  protected final static String UNSECURE_INI_RESOURCE_PATH = "classpath:shiro.ini";

  public TSAIniWebEnvironment() {
    setSecurityManagerFactory(new TCWebIniSecurityManagerFactory());
  }

  @Override
  protected String[] getDefaultConfigLocations() {
    return new String[]{UNSECURE_INI_RESOURCE_PATH};
  }

}
