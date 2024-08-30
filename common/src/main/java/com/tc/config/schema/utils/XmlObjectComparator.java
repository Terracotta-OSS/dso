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
 * Allows you to compare several {@link XmlObject}s.
 */
public interface XmlObjectComparator {

  boolean equals(XmlObject one, XmlObject two);

  /**
   * This compares two {@link XmlObject} implementations to see if they are semantically equal; it also descends to
   * child objects. It throws an exception instead of returning a value so that you can find out <em>why</em> the two
   * objects aren't equal, since this is a deep compare.
   */
  void checkEquals(XmlObject one, XmlObject two) throws NotEqualException;

}
