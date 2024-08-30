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
package com.tc.admin;

import com.tc.server.TCServerMain;
import com.tc.test.TCTestCase;
import com.tc.util.ToolClassNames;

/**
 * These tests verify that the tool class names recorded in ToolClassNames
 * are still valid against the actual class names. If someone changes the
 * class name of TCStop or AdminClient, we want these tests to go off,
 * indicating a needed change in ToolClassNames.
 */
public class ToolClassNameCheck extends TCTestCase {

  public void testTCStopClassName() {
    assertEquals(TCStop.class.getName(), ToolClassNames.TC_STOP_CLASS_NAME);
  }

  public void testTCServerClassName() {
    assertEquals(TCServerMain.class.getName(), ToolClassNames.TC_SERVER_CLASS_NAME);
  }
}
