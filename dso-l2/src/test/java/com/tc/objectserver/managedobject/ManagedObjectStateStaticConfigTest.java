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
package com.tc.objectserver.managedobject;

import com.tc.objectserver.managedobject.ManagedObjectStateStaticConfig.Factory;

import java.util.Set;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ManagedObjectStateStaticConfigTest extends TestCase {

  public void test() {
    Set<String> toolkitTypeNames = ManagedObjectStateStaticConfig.ToolkitTypeNames.values();
    ManagedObjectStateStaticConfig[] configs = ManagedObjectStateStaticConfig.values();
    Factory[] factories = ManagedObjectStateStaticConfig.Factory.values();

    Assert.assertTrue(configs.length > 1);
    Assert.assertTrue(factories.length > 1);

    for (ManagedObjectStateStaticConfig config : configs) {
      System.out.println(config);
      Assert.assertNotNull(config.getFactory());
      Assert.assertEquals(config.getFactory(), ManagedObjectStateStaticConfig.Factory.getFactoryForType(config
          .getFactory().getStateObjectType()));
      Assert.assertEquals(config.getStateObjectType(), config.getFactory().getStateObjectType());

      // verify every config has a name defined
      Assert.assertTrue("ToolkitTypeName constant not defined for '" + config.getClientClassName() + "'",
                        toolkitTypeNames.contains(config.getClientClassName()));
    }

    for (Factory f : factories) {
      System.out.println(f);
      Assert.assertEquals(f, Factory.getFactoryForType(f.getStateObjectType()));
    }

    for (String name : toolkitTypeNames) {
      Assert.assertTrue(name != null);
      Assert.assertTrue(name.trim().length() > 0);
      ManagedObjectStateStaticConfig config = ManagedObjectStateStaticConfig.getConfigForClientClassName(name);
      System.out.println(name);
      // verify every name has a config defined
      Assert.assertNotNull("ManagedObjectStateStaticFactory enum type not defined for toolkit type: '" + name + "'",
                           config);
      Assert.assertEquals(name, config.getClientClassName());
    }

  }
}
