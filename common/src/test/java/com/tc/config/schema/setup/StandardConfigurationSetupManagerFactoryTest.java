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

import org.junit.Test;

import com.tc.config.schema.IllegalConfigurationChangeHandler;
import com.tc.security.PwProvider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class StandardConfigurationSetupManagerFactoryTest {

  @Test
  public void testSafeModeConfiguration() throws Exception {
    String[] args = {"-f", this.getClass().getResource("/default-config.xml").getPath(), "--safe-mode"};

    StandardConfigurationSetupManagerFactory factory =
        new StandardConfigurationSetupManagerFactory(args,
                                                     StandardConfigurationSetupManagerFactory.ConfigMode.L2,
                                                     mock(IllegalConfigurationChangeHandler.class),
                                                     mock(PwProvider.class));

    L2ConfigurationSetupManager l2TVSConfigurationSetupManager =
        factory.createL2TVSConfigurationSetupManager(null, false);

    assertTrue(l2TVSConfigurationSetupManager.isSafeModeConfigured());
  }

  @Test
  public void testNonSafeModeConfiguration() throws Exception {
    String[] args = {"-f", this.getClass().getResource("/default-config.xml").getPath()};

    StandardConfigurationSetupManagerFactory factory =
        new StandardConfigurationSetupManagerFactory(args,
                                                     StandardConfigurationSetupManagerFactory.ConfigMode.L2,
                                                     mock(IllegalConfigurationChangeHandler.class),
                                                     mock(PwProvider.class));

    L2ConfigurationSetupManager l2TVSConfigurationSetupManager =
        factory.createL2TVSConfigurationSetupManager(null, false);

    assertFalse(l2TVSConfigurationSetupManager.isSafeModeConfigured());
  }
}