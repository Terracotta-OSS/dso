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

import org.junit.BeforeClass;
import org.junit.Test;


import java.io.File;

import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.*;
import static org.junit.Assert.fail;

/**
 * Created by alsu on 16/03/16.
 */
public class L2ConfigurationSetupManagerImplTest {
  private static TestConfigurationSetupManagerFactory factory;

  @BeforeClass
  public static void oneTimeSetup() throws ConfigurationSetupException {
    factory = new TestConfigurationSetupManagerFactory(new FatalIllegalConfigurationChangeHandler());
  }

  @Test
  public void testFailoverPriorityGroupMemberCountValidation() throws Exception {
    File tcConfig = new File(this.getClass().getClassLoader().getResource("failover-priority-config.xml").toURI());
    try {
      factory.createL2TVSConfigurationSetupManager(tcConfig, "server1");
      fail("L2ConfigurationSetupManager instantiation should have failed");
    } catch (ConfigurationSetupException cse) {
      assertThat(cse.getMessage(),
          containsString("group1" + " contains more than two servers " +
                         "which is a violation of the CONSISTENCY failover priority requirement"));
    }
  }

}