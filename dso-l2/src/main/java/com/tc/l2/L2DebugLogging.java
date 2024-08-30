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
package com.tc.l2;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.objectserver.impl.ThisServerNodeId;
import com.tc.properties.TCPropertiesImpl;

public abstract class L2DebugLogging {
  private static final TCLogger logger                     = TCLogging.getLogger(L2DebugLogging.class);

  private static final String   L2_DEBUG_LOGGING_PROP_NAME = "l2.debug-logging";
  private static final boolean  enabled                    = TCPropertiesImpl.getProperties()
                                                               .getBoolean(L2_DEBUG_LOGGING_PROP_NAME, false);

  static {
    logger.info("L2 debug logging: " + (enabled ? "ENABLED" : "DISABLED"));
  }

  public static enum LogLevel {
    ERROR, WARN, INFO, DEBUG;
  }

  public static void log(TCLogger log, LogLevel level, String message, Throwable throwable) {
    if (!enabled) return;
    message = "[" + ThisServerNodeId.getThisServerNodeId() + "] " + message;
    switch (level) {
      case ERROR:
        if (throwable != null) {
          log.error(message, throwable);
        } else {
          log.error(message);
        }
        break;
      case WARN:
        if (throwable != null) {
          log.warn(message, throwable);
        } else {
          log.warn(message);
        }
        break;
      case INFO:
        if (throwable != null) {
          log.info(message, throwable);
        } else {
          log.info(message);
        }
        break;
      case DEBUG:
        if (throwable != null) {
          log.debug(message, throwable);
        } else {
          log.debug(message);
        }
        break;
    }
  }
}
