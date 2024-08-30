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
package com.terracotta.toolkit.express;

import junit.framework.TestCase;

/**
 * @author Alex Snaps
 */
public class URLConfigUtilTest extends TestCase {

  public void testParsesUsername() {
    assertEquals("alex", URLConfigUtil.getUsername("alex@localhost:896"));
    assertEquals("alex", URLConfigUtil.getUsername("  alex@localhost:896"));
    assertEquals("alex", URLConfigUtil.getUsername("alex@localhost:896,alex@localhost:87645"));
    assertEquals("alex", URLConfigUtil.getUsername(" alex@localhost:896,  alex@localhost:87645"));
    assertEquals("alex", URLConfigUtil.getUsername("alex@localhost:896,localhost:87645"));
    assertEquals("alex", URLConfigUtil.getUsername("localhost:896,alex@localhost:87645"));
    try {
      assertEquals("alex", URLConfigUtil.getUsername("alex@localhost:896,john@localhost:87645"));
      fail();
    } catch (AssertionError e) {
      // Expected
    }
  }

  public void testTranslateSystemProperties() throws Exception {
    String[] systemProperties = { "host1=localhost:80", "host2=localhost:81" };

    for(int i = 0; i < systemProperties.length; i++) {
      String[] tokens = systemProperties[i].split("=");
      System.setProperty(tokens[0], tokens[1]);
    }

    assertEquals("user1@localhost:80,user2@localhost:81", URLConfigUtil.translateSystemProperties("user1@${host1},user2@${host2}"));
  }
}
