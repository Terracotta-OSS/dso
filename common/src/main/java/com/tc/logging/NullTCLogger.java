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
package com.tc.logging;


/**
 * @author steve
 */
public class NullTCLogger implements TCLogger {

  @Override
  public void debug(Object message) {
    //
  }

  @Override
  public void debug(Object message, Throwable t) {
    //
  }

  @Override
  public void error(Object message) {
    //
  }

  @Override
  public void error(Object message, Throwable t) {
    //
  }

  @Override
  public void fatal(Object message) {
    //
  }

  @Override
  public void fatal(Object message, Throwable t) {
    //
  }

  @Override
  public void info(Object message) {
    //
  }

  @Override
  public void info(Object message, Throwable t) {
    //
  }

  @Override
  public void warn(Object message) {
    //
  }

  @Override
  public void warn(Object message, Throwable t) {
    //
  }

  @Override
  public boolean isDebugEnabled() {
    return false;
  }

  @Override
  public boolean isInfoEnabled() {
    return false;
  }

  @Override
  public void setLevel(LogLevel level) {
    //
  }

  @Override
  public LogLevel getLevel() {
    throw new AssertionError();
  }

  @Override
  public String getName() {
    return "";
  }

}