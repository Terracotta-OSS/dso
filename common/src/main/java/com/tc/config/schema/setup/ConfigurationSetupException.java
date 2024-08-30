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
package com.tc.config.schema.setup;

import com.tc.exception.ExceptionWrapper;
import com.tc.exception.ExceptionWrapperImpl;
import com.tc.exception.TCException;

/**
 * Thrown when the configuration system couldn't be set up. This should generally be treated as a fatal exception.
 */
public class ConfigurationSetupException extends TCException {

  private static final ExceptionWrapper wrapper = new ExceptionWrapperImpl();
  
  public ConfigurationSetupException() {
    super();
  }

  public ConfigurationSetupException(String message) {
    super(wrapper.wrap(message));
  }

  public ConfigurationSetupException(Throwable cause) {
    super(cause);
  }

  public ConfigurationSetupException(String message, Throwable cause) {
    super(wrapper.wrap(message), cause);
  }

}
