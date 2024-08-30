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
package com.terracotta.toolkit.events;

import com.tc.platform.PlatformService;
import org.junit.Before;
import org.junit.Test;
import org.terracotta.toolkit.monitoring.OperatorEventLevel;

import static com.tc.operatorevent.TerracottaOperatorEvent.EventLevel;
import static com.tc.operatorevent.TerracottaOperatorEvent.EventSubsystem;
import static com.tc.operatorevent.TerracottaOperatorEvent.EventType;
import static com.terracotta.toolkit.events.OperatorEventUtil.DELIMITER;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class OperatorEventUtilTest {

  private static PlatformService platformService;
  @Before
  public void setUp() throws Exception {
    platformService = mock(PlatformService.class);
  }

  @Test
  public void testFireOperatorEvent() throws Exception {
    final String TEST_MESSAGE = "test message";
    final String NORMAL_APPLICATION_NAME = "applicationName";
    final String EXPECTED_TEST_MESSAGE = NORMAL_APPLICATION_NAME + ": " + TEST_MESSAGE;

    OperatorEventUtil.fireOperatorEvent(platformService, OperatorEventLevel.INFO, NORMAL_APPLICATION_NAME, TEST_MESSAGE);
    verify(platformService).fireOperatorEvent(EventLevel.INFO, EventSubsystem.APPLICATION, EventType.APPLICATION_USER_DEFINED, EXPECTED_TEST_MESSAGE);
  }

  @Test
  public void testFireOperatorEventWithSpecialApplicationName() throws Exception {
    final String TEST_MESSAGE = "test message";
    final String SPECIAL_APPLICATION_NAME = EventSubsystem.WAN + DELIMITER + EventType.WAN_REPLICA_CONNECTED;

    OperatorEventUtil.fireOperatorEvent(platformService, OperatorEventLevel.INFO, SPECIAL_APPLICATION_NAME, TEST_MESSAGE);
    verify(platformService).fireOperatorEvent(EventLevel.INFO, EventSubsystem.WAN, EventType.WAN_REPLICA_CONNECTED, TEST_MESSAGE);
  }
}