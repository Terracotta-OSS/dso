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
package com.tc.config.schema.setup;

import com.tc.config.schema.setup.sources.ConfigurationSource;
import com.terracottatech.config.TcConfigDocument;

import java.io.File;
import java.io.InputStream;

public class TestConfigurationSource implements ConfigurationSource {

  @Override
  public File directoryLoadedFrom() {
    return new File(System.getProperty("user.dir"));
  }

  @Override
  public InputStream getInputStream(long maxTimeoutMillis) {
    TcConfigDocument tcConfigDocument = TcConfigDocument.Factory.newInstance();
    tcConfigDocument.addNewTcConfig();
    return tcConfigDocument.newInputStream();
  }

  @Override
  public boolean isTrusted() {
    return true;
  }

}
