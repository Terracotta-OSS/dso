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
package com.terracotta.toolkit.events;

import org.terracotta.toolkit.monitoring.OperatorEventLevel;

import com.tc.operatorevent.TerracottaOperatorEvent.EventLevel;
import com.tc.operatorevent.TerracottaOperatorEvent.EventSubsystem;
import com.tc.operatorevent.TerracottaOperatorEvent.EventType;
import com.tc.platform.PlatformService;

public class OperatorEventUtil {

  public static final String DELIMITER = ":";

  private static EventLevel translateOperatorEventLevel(OperatorEventLevel level) {
    switch (level) {
      case INFO: {
        return EventLevel.INFO;
      }
      case WARN: {
        return EventLevel.WARN;
      }
      case DEBUG: {
        return EventLevel.DEBUG;
      }
      case ERROR: {
        return EventLevel.ERROR;
      }
      case CRITICAL: {
        return EventLevel.CRITICAL;
      }
    }
    // don't do this as the "default" in the switch block so the compiler can catch errors
    throw new AssertionError("unknown OperatorEventLevel " + level);
  }

  public static void fireOperatorEvent(PlatformService platformService, OperatorEventLevel level,
                                       String applicationName, String eventMessage) {
    // TAB-5323 hacky way to get Event Subsystem and Event type
    // if applicationName is of format "Event_Subsystem + DELIMITER + Event_type", then extract and call fireOperatorEvent with given info
    // else call with EventSubsystem.APPLICATION and EventType.APPLICATION_USER_DEFINED
    EventSubsystem eventSubsystem = EventSubsystem.APPLICATION;
    EventType eventType = EventType.APPLICATION_USER_DEFINED;
    String message = applicationName + ": " + eventMessage;
    String[] tokens = applicationName.split(DELIMITER);
    if(tokens.length == 2) {
      try {
        eventSubsystem = EventSubsystem.valueOf(tokens[0]);
        eventType = EventType.valueOf(tokens[1]);
        message = eventMessage;
      } catch (IllegalArgumentException ie) {
        eventSubsystem = EventSubsystem.APPLICATION;
        eventType = EventType.APPLICATION_USER_DEFINED;
        message = applicationName + ": " + eventMessage;
      }
    }
    platformService.fireOperatorEvent(translateOperatorEventLevel(level), eventSubsystem, eventType, message);
  }
}
