/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
package com.tc.object;

import org.apache.commons.lang3.ArrayUtils;

import com.tc.config.schema.IllegalConfigurationChangeHandler;
import com.tc.config.schema.dynamic.ConfigItem;
import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.config.schema.setup.L1ConfigurationSetupManager;
import com.tc.config.schema.setup.TestConfigurationSetupManagerFactory;
import com.tc.object.config.DSOClientConfigHelper;
import com.tc.object.config.StandardDSOClientConfigHelperImpl;
import com.tc.properties.TCProperties;
import com.tc.properties.TCPropertiesImpl;
import com.tc.test.TCTestCase;
import com.terracottatech.config.Client;
import com.terracottatech.config.Server;

import java.io.IOException;

/**
 * The base of all DSO tests that use config.
 */
public class BaseDSOTestCase extends TCTestCase implements TestClientConfigHelperFactory {

  private Exception            failTestException;
  protected final TCProperties tcProps = TCPropertiesImpl.getProperties();

  private class TestFailingIllegalConfigChangeHandler implements IllegalConfigurationChangeHandler {
    @Override
    public void changeFailed(ConfigItem item, Object oldValue, Object newValue) {
      failTestException = new Exception("An attempt was made to illegally change the config item " + item + " from "
                                        + ArrayUtils.toString(oldValue) + " to " + ArrayUtils.toString(newValue));
    }
  }

  @Override
  public void runBare() throws Throwable {
    super.runBare();
    if (this.failTestException != null) throw this.failTestException;
  }

  private TestConfigurationSetupManagerFactory configFactory;
  private L1ConfigurationSetupManager          l1ConfigManager;
  private DSOClientConfigHelper                configHelper;

  protected synchronized final void setUp(TestConfigurationSetupManagerFactory factory, DSOClientConfigHelper helper)
      throws Exception {
    super.setUp();
    this.configFactory = factory;
    this.configHelper = helper;
  }

  protected synchronized final TestConfigurationSetupManagerFactory configFactory() throws ConfigurationSetupException {
    if (this.configFactory == null) this.configFactory = createDistributedConfigFactory();
    return this.configFactory;
  }

  protected synchronized final TestConfigurationSetupManagerFactory createDistributedConfigFactory()
      throws ConfigurationSetupException {
    TestConfigurationSetupManagerFactory out;
    out = new TestConfigurationSetupManagerFactory(TestConfigurationSetupManagerFactory.MODE_DISTRIBUTED_CONFIG, null,
                                                   new TestFailingIllegalConfigChangeHandler());

    prepareFactory(out);
    return out;
  }

  private synchronized void prepareFactory(TestConfigurationSetupManagerFactory out) throws ConfigurationSetupException {
    setupConfigLogDataPaths(out);

    out.activateConfigurationChange();
  }

  protected synchronized void setupConfigLogDataPaths(TestConfigurationSetupManagerFactory out)
      throws ConfigurationSetupException {
    try {
      Server server = (Server) out.l2CommonConfig().getBean();
      server.setData(getTempFile("l2-data").toString());
      server.setLogs(getTempFile("l2-logs").toString());
      server.setIndex(getTempFile("l2-index").toString());
      server.setDataBackup(getTempFile("l2-data-backup").toString());
      ((Client) out.l1CommonConfig().getBean()).setLogs(getTempFile("l1-logs").toString());
    } catch (IOException ioe) {
      throw new ConfigurationSetupException("Can't set up log, data and statistics paths", ioe);
    }
  }

  protected synchronized final TestConfigurationSetupManagerFactory createCentralizedConfigFactory()
      throws ConfigurationSetupException {
    TestConfigurationSetupManagerFactory out;
    out = new TestConfigurationSetupManagerFactory(new TestFailingIllegalConfigChangeHandler());

    prepareFactory(out);
    return out;
  }

  protected synchronized final L1ConfigurationSetupManager l1Manager() throws ConfigurationSetupException {
    if (this.l1ConfigManager == null) this.l1ConfigManager = createL1ConfigManager();
    return this.l1ConfigManager;
  }

  protected synchronized final L1ConfigurationSetupManager createL1ConfigManager() throws ConfigurationSetupException {
    return configFactory().getL1TVSConfigurationSetupManager();
  }

  protected synchronized final DSOClientConfigHelper configHelper() throws ConfigurationSetupException {
    if (this.configHelper == null) this.configHelper = createClientConfigHelper();
    return this.configHelper;
  }

  @Override
  public synchronized final DSOClientConfigHelper createClientConfigHelper() throws ConfigurationSetupException {
    return new StandardDSOClientConfigHelperImpl(true, createL1ConfigManager());
  }

  // TODO: fix this
  protected synchronized final void makeClientUsePort(int whichPort) {
    configFactory.l2DSOConfig().tsaPort().setIntValue(whichPort);
  }

  public BaseDSOTestCase() {
    super();
  }

  public BaseDSOTestCase(String arg0) {
    super(arg0);
  }

  @Override
  protected synchronized void tearDown() throws Exception {
    this.configFactory = null;
    this.configHelper = null;
    this.l1ConfigManager = null;
  }
}
