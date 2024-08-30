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
package com.terracotta.toolkit;

import org.terracotta.toolkit.internal.ToolkitLogger;

import com.tc.logging.TCLogger;
import com.tc.platform.PlatformService;

public class TerracottaLogger implements ToolkitLogger {
  private final static String TOOLKIT_LOGGER_NAMESPACE = "org.terracotta.toolkit";

  private final TCLogger      tclogger;

  /**
   * Creates a TerracottaLogger implementation whose name ends with the given string.
   * <p>
   * The supplied name is prefixed with a fixed value to ensure toolkit loggers are confined within a namespace.
   * 
   * @param name logger identifier
   * @param platformService
   * @throws NullPointerException if the supplied name is null
   */
  public TerracottaLogger(String name, PlatformService platformService) {
    if (null == name) { throw new NullPointerException("Logger name can't be null"); }

    StringBuilder fullName = new StringBuilder(TOOLKIT_LOGGER_NAMESPACE);
    if (!name.startsWith(".")) {
      fullName.append(".");
    }
    fullName.append(name);
    tclogger = platformService.getLogger(name);
  }

  @Override
  public void debug(Object message, Throwable t) {
    tclogger.debug(message, t);
  }

  @Override
  public void debug(Object message) {
    tclogger.debug(message);
  }

  @Override
  public void info(Object message, Throwable t) {
    tclogger.info(message, t);
  }

  @Override
  public void info(Object message) {
    tclogger.info(message);
  }

  @Override
  public void warn(Object message, Throwable t) {
    tclogger.warn(message, t);
  }

  @Override
  public void warn(Object message) {
    tclogger.warn(message);
  }

  @Override
  public void error(Object message, Throwable t) {
    tclogger.error(message, t);
  }

  @Override
  public void error(Object message) {
    tclogger.error(message);
  }

  @Override
  public void fatal(Object message, Throwable t) {
    tclogger.fatal(message, t);
  }

  @Override
  public void fatal(Object message) {
    tclogger.fatal(message);
  }

  @Override
  public String getName() {
    return tclogger.getName();
  }

  @Override
  public boolean isDebugEnabled() {
    return tclogger.isDebugEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return tclogger.isInfoEnabled();
  }

}
