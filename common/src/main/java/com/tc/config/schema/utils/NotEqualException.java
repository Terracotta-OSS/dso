/* 
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at 
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is 
 *      Terracotta, Inc., a Software AG company
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
