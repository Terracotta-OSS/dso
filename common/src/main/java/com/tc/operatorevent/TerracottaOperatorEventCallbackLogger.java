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
package com.tc.operatorevent;

import com.tc.logging.CustomerLogging;
import com.tc.logging.TCLogger;
import com.tc.operatorevent.TerracottaOperatorEvent.EventLevel;

public class TerracottaOperatorEventCallbackLogger implements TerracottaOperatorEventCallback {

  private final TCLogger logger = CustomerLogging.getOperatorEventLogger();

  @Override
  public void logOperatorEvent(TerracottaOperatorEvent event) {
    EventLevel eventType = event.getEventLevel();
    switch (eventType) {
      case INFO:
        this.logger.info("NODE : " + event.getNodeName() + " Subsystem: " + event.getEventSubsystem() + " EventType: "
                         + event.getEventType() + " Message: " + event.getEventMessage());
        break;
      case WARN:
        this.logger.warn("NODE : " + event.getNodeName() + " Subsystem: " + event.getEventSubsystem() + " EventType: "
                         + event.getEventType() + " Message: " + event.getEventMessage());
        break;
      case DEBUG:
        this.logger.debug("NODE : " + event.getNodeName() + " Subsystem: " + event.getEventSubsystem() + " EventType: "
                          + event.getEventType() + " Message: " + event.getEventMessage());
        break;
      case ERROR:
        this.logger.error("NODE : " + event.getNodeName() + " Subsystem: " + event.getEventSubsystem() + " EventType: "
                          + event.getEventType() + " Message: " + event.getEventMessage());
        break;
      case CRITICAL:
        this.logger.fatal("NODE : " + event.getNodeName() + " Subsystem: " + event.getEventSubsystem() + " EventType: "
                          + event.getEventType() + " Message: " + event.getEventMessage());
        break;
      default:
        throw new RuntimeException("invalid event type: " + eventType);
    }
  }

}
