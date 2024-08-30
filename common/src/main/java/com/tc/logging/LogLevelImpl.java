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
package com.tc.logging;

import ch.qos.logback.classic.Level;

import com.tc.util.Assert;

/**
 * Defines constants for various logging levels
 * 
 * @author teck
 */
public class LogLevelImpl implements LogLevel {
  static final int             LEVEL_DEBUG = 5;
  static final int             LEVEL_INFO  = 4;
  static final int             LEVEL_WARN  = 3;
  static final int             LEVEL_ERROR = 2;
  static final int             LEVEL_OFF   = 0;

  public static final LogLevel DEBUG       = new LogLevelImpl(LEVEL_DEBUG);
  public static final LogLevel INFO        = new LogLevelImpl(LEVEL_INFO);
  public static final LogLevel WARN        = new LogLevelImpl(LEVEL_WARN);
  public static final LogLevel ERROR       = new LogLevelImpl(LEVEL_ERROR);
  public static final LogLevel OFF         = new LogLevelImpl(LEVEL_OFF);

  public static final String   DEBUG_NAME  = "DEBUG";
  public static final String   INFO_NAME   = "INFO";
  public static final String   WARN_NAME   = "WARN";
  public static final String   ERROR_NAME  = "ERROR";
  public static final String   OFF_NAME    = "OFF";

  private final int            level;

  private LogLevelImpl(int level) {
    this.level = level;
  }

  @Override
  public int getLevel() {
    return level;
  }

  @Override
  public boolean isInfo() {
    return level == LEVEL_INFO;
  }

  static Level toLogBackLevel(LogLevel level) {
    if (level == null) return null;

    switch (level.getLevel()) {
      case LEVEL_DEBUG:
        return Level.DEBUG;
      case LEVEL_INFO:
        return Level.INFO;
      case LEVEL_WARN:
        return Level.WARN;
      case LEVEL_ERROR:
        return Level.ERROR;
      case LEVEL_OFF:
        return Level.OFF;
      default:
        throw Assert.failure("Logic Error: Invalid Level: " + level);
    }
  }

  static LogLevel fromLogBackLevel(Level level) {
    if (level == null) return null;
    switch (level.toInt()) {
      case Level.DEBUG_INT:
        return DEBUG;
      case Level.INFO_INT:
        return INFO;
      case Level.WARN_INT:
        return WARN;
      case Level.ERROR_INT:
        return ERROR;
      case Level.OFF_INT:
        return OFF;
      default:
        throw Assert.failure("Unsupported Level" + level);
    }
  }

  @Override
  public String toString() {
    switch (getLevel()) {
      case LEVEL_DEBUG:
        return DEBUG_NAME;
      case LEVEL_INFO:
        return INFO_NAME;
      case LEVEL_WARN:
        return WARN_NAME;
      case LEVEL_ERROR:
        return ERROR_NAME;
      case LEVEL_OFF:
        return OFF_NAME;
      default:
        return "Unknown";
    }
  }

  public static LogLevel valueOf(String v) {
    if (DEBUG_NAME.equals(v)) {
      return DEBUG;
    } else if (INFO_NAME.equals(v)) {
      return INFO;
    } else if (WARN_NAME.equals(v)) {
      return WARN;
    } else if (ERROR_NAME.equals(v)) {
      return ERROR;
    } else if (OFF_NAME.equals(v)) {
      return OFF;
    } else {
      return null;
    }
  }

}
