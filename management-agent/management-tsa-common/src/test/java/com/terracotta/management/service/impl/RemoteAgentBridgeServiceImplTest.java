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
package com.terracotta.management.service.impl;


import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RemoteAgentBridgeServiceImplTest {

  @Test
  public void testStringArrayToString__2strings() throws Exception {
    String[] array = new String[]{"string1", "string2"};
    String s = RemoteAgentBridgeServiceImpl.toString(array);
    assertEquals("string1,string2",s);
  }

  @Test
  public void testStringArrayToString__1string() throws Exception {
    String[] array = new String[]{"string1"};
    String s = RemoteAgentBridgeServiceImpl.toString(array);
    assertEquals("string1",s);
  }

}