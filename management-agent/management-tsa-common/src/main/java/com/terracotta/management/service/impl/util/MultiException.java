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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ludovic Orban
 */
public class MultiException extends Exception {

  private final List<Throwable> throwables;

  public MultiException(String message, List<Throwable> throwables) {
    super(message);
    this.throwables = Collections.unmodifiableList(new ArrayList<Throwable>(throwables));
  }

  public List<Throwable> getThrowables() {
    return throwables;
  }

  public String getMessage() {
    StringBuilder errorMessage = new StringBuilder();
    errorMessage.append(super.getMessage());
    errorMessage.append("; collected ");
    errorMessage.append(throwables.size());
    errorMessage.append(" exception(s):");

    for (Throwable throwable : throwables) {
      errorMessage.append(System.getProperty("line.separator"));
      errorMessage.append(" [");
      errorMessage.append(throwable.getClass().getName());
      errorMessage.append(" - ");
      errorMessage.append(throwable.getMessage());
      errorMessage.append("]");
    }
    return errorMessage.toString();
  }

}
