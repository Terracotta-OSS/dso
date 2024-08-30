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

/**
 * Come here to get loggers for public / customer-facing use
 */
public class CustomerLogging {

  // Logger names. You'll want to keep these unique unless you really want to cross streams
  private static final String GENERIC_CUSTOMER_LOGGER             = "general";

  private static final String DSO_CUSTOMER_GENERIC_LOGGER = "tsa";
  private static final String DSO_RUNTIME_LOGGER          = "tsa.runtime";

  private CustomerLogging() {
    // no need to instaniate me
  }

  public static TCLogger getConsoleLogger() {
    return TCLogging.getConsoleLogger();
  }

  public static TCLogger getGenericCustomerLogger() {
    return TCLogging.getCustomerLogger(GENERIC_CUSTOMER_LOGGER);
  }

  public static TCLogger getDSOGenericLogger() {
    return TCLogging.getCustomerLogger(DSO_CUSTOMER_GENERIC_LOGGER);
  }

  public static TCLogger getDSORuntimeLogger() {
    return TCLogging.getCustomerLogger(DSO_RUNTIME_LOGGER);
  }
  
  public static TCLogger getOperatorEventLogger() {
    return TCLogging.getOperatorEventLogger();
  }
}
