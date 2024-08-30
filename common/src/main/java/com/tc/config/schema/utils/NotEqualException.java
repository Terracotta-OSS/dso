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
package com.tc.config.schema.utils;

import org.apache.xmlbeans.XmlObject;

/**
 * Thrown when two {@link XmlObject}s are not equal in
 * {@link com.tc.config.schema.utils.StandardXmlObjectComparator#checkEquals(XmlObject, XmlObject)}.
 */
public class NotEqualException extends Exception {

  public NotEqualException() {
    super();
  }

  public NotEqualException(String message, Throwable cause) {
    super(message, cause);
  }

  public NotEqualException(String message) {
    super(message);
  }

  public NotEqualException(Throwable cause) {
    super(cause);
  }

}
