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
package com.terracotta.management.service.impl.util;

import org.terracotta.management.resource.ErrorEntity;

/**
 * @author Ludovic Orban
 */
public class ManagementSourceException extends RuntimeException {
  private final ErrorEntity errorEntity;

  public ManagementSourceException(Throwable t) {
    super(t);
    errorEntity = null;
  }

  public ManagementSourceException(String msg) {
    super(msg);
    errorEntity = null;
  }

  public ManagementSourceException(String message, ErrorEntity errorEntity) {
    super(message);
    this.errorEntity = errorEntity;
  }

  public ErrorEntity getErrorEntity() {
    return errorEntity;
  }
}
