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
import com.tc.net.core.SecurityInfo;
import com.tc.security.PwProvider;
import com.tc.util.Assert;
import com.tc.util.io.ServerURL;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

/**
 * A {@link ConfigurationSource} that reads from a URL.
 */
public class ServerConfigurationSource implements ConfigurationSource {

  private final String       host;
  private final int          port;
  private final SecurityInfo securityInfo;
  private final PwProvider   pwProvider;

  public ServerConfigurationSource(final String host, final int port, final SecurityInfo securityInfo, final PwProvider pwProvider) {
      this.securityInfo = securityInfo;
    Assert.assertNotBlank(host);
    Assert.assertTrue(port > 0);
    this.host = host;
    this.port = port;
    this.pwProvider = pwProvider;
  }

  @Override
  public InputStream getInputStream(long maxTimeoutMillis) throws IOException, ConfigurationSetupException {
    try {
      ServerURL theURL = new ServerURL(host, port, "/config" , (int)maxTimeoutMillis, securityInfo);
      return theURL.openStream(pwProvider);
    } catch (MalformedURLException murle) {
      throw new ConfigurationSetupException("Can't load configuration from "+this+".");
    }
  }

  @Override
  public File directoryLoadedFrom() {
    return null;
  }

  @Override
  public boolean isTrusted() {
    return true;
  }

  @Override
  public String toString() {
    return "server at '" + this.host + ":" + this.port + "'";
  }

}
