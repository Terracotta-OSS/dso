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
package com.tc.util;

import com.tc.test.TCTestCase;

public class PortChooserTest extends TCTestCase {

  private PortChooser portChooser;

  @Override
  protected void setUp() throws Exception {
    this.portChooser = new PortChooser();
  }

  public void testChooseRandomPorts() {
    int numOfPorts = 100;
    int portNum = this.portChooser.chooseRandomPorts(numOfPorts);

    for (int i = 0; i < numOfPorts; i++) {
      Assert.assertTrue(this.portChooser.isPortUsed(portNum + i));
    }
  }

  public void testAll() {
    int portNum1 = this.portChooser.chooseRandomPort();

    int portNum2 = this.portChooser.chooseRandom2Port();
    Assert.assertTrue(this.portChooser.isPortUsed(portNum2));
    Assert.assertTrue(this.portChooser.isPortUsed(portNum2 + 1));

    int numOfPorts = 1000;
    int portNum3 = this.portChooser.chooseRandomPorts(numOfPorts);
    for (int i = 0; i < numOfPorts; i++) {
      Assert.assertTrue(this.portChooser.isPortUsed(portNum3 + i));
    }

    Assert.assertTrue(portNum1 != portNum2);
    Assert.assertTrue(portNum2 != portNum3);
    Assert.assertTrue(portNum3 != portNum1);

    Assert.assertTrue(portNum1 != portNum2 + 1);
    
    for (int i = 0; i < numOfPorts; i++) {
      Assert.assertTrue(portNum1 != portNum3 + i);
      Assert.assertTrue(portNum2 != portNum3 + i);
      Assert.assertTrue(portNum2 + 1 != portNum3 + i);
    }
  }
}
