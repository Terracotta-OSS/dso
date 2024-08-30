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

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;

/**
 * An implementation of TCLogger that just delegates to a logback Logger instance NOTE: This implementation differs from
 * logback in at least one detail....When calling the various log methods (info, warn, etc..) that take a single
 * <code>Object</code> parameter (eg. <code>debug(Object message)</code>), if an instance of <code>Throwable</code> is
 * passed as the message paramater, the call will be translated to the <code>xxx(Object Message, Throwable t)</code>
 * signature
 * 
 * @author teck
 */
class TCLoggerImpl implements TCLogger {

  private final Logger logger;

  TCLoggerImpl(String name) {
    if (name == null) { throw new IllegalArgumentException("Logger name cannot be null"); }
    LoggerContext context = TCLogging.getLoggerContext();
    logger = context.getLogger(name);
  }

  Logger getLogger() {
    return logger;
  }

  @Override
  public void debug(Object message) {
    if (message instanceof Throwable) {
      debug("Exception thrown", (Throwable) message);
    } else {
      logger.debug(message.toString());
    }
  }

  @Override
  public void debug(Object message, Throwable t) {
    logger.debug(message.toString(), t);
  }

  @Override
  public void error(Object message) {
    if (message instanceof Throwable) {
      error("Exception thrown", (Throwable) message);
    } else {
      logger.error(message.toString());
    }
  }

  @Override
  public void error(Object message, Throwable t) {
    logger.error(message.toString(), t);
  }

  @Override
  public void fatal(Object message) {
    if (message instanceof Throwable) {
      fatal("Exception thrown", (Throwable) message);
    } else {
      logger.error(message.toString());
    }
  }

  @Override
  public void fatal(Object message, Throwable t) {
    logger.error(message.toString(), t);
  }

  @Override
  public void info(Object message) {
    if (message instanceof Throwable) {
      info("Exception thrown", (Throwable) message);
    } else {
      logger.info(message.toString());
    }
  }

  @Override
  public void info(Object message, Throwable t) {
    logger.info(message.toString(), t);
  }

  @Override
  public void warn(Object message) {
    if (message instanceof Throwable) {
      warn("Exception thrown", (Throwable) message);
    } else {
      logger.warn(message.toString());
    }
  }

  @Override
  public void warn(Object message, Throwable t) {
    logger.warn(message.toString(), t);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public void setLevel(LogLevel level) {
    logger.setLevel(LogLevelImpl.toLogBackLevel(level));
  }

  @Override
  public LogLevel getLevel() {
    return LogLevelImpl.fromLogBackLevel(logger.getLevel());
  }

  @Override
  public String getName() {
    return logger.getName();
  }
}
