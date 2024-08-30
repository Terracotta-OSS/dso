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
package com.tc.config.schema.setup.sources;

import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.util.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * A {@link ConfigurationSource} that reads from a URL.
 */
public class URLConfigurationSource implements ConfigurationSource {

  private final String url;

  public URLConfigurationSource(String url) {
    Assert.assertNotBlank(url);
    this.url = url;
  }

  @Override
  public InputStream getInputStream(long maxTimeoutMillis) throws IOException, ConfigurationSetupException {
    URL theURL = new URL(this.url);
    try {
      URLConnection connection = theURL.openConnection();
      connection.setConnectTimeout((int) maxTimeoutMillis);
      connection.setReadTimeout((int) maxTimeoutMillis);
      return connection.getInputStream();
    } catch (MalformedURLException murle) {
      throw new ConfigurationSetupException("The URL '" + this.url
                                            + "' is malformed, and thus can't be used to load configuration.");
    }
  }

  @Override
  public File directoryLoadedFrom() {
    return null;
  }

  @Override
  public boolean isTrusted() {
    return false;
  }

  @Override
  public String toString() {
    return "URL '" + this.url + "'";
  }

}
