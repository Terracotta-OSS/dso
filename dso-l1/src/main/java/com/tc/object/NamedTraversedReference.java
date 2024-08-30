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
package com.tc.object;

public class NamedTraversedReference implements TraversedReference {

  private final String className;
  private final String fieldName;
  private final Object value;

  public NamedTraversedReference(String fullyQualifiedFieldname, Object value) {
    this(null, fullyQualifiedFieldname, value);
  }

  public NamedTraversedReference(String className, String fieldName, Object value) {
    this.className = className;
    this.fieldName = fieldName;
    this.value = value;
  }
  
  @Override
  public Object getValue() {
    return this.value;
  }

  @Override
  public boolean isAnonymous() {
    return false;
  }

  @Override
  public String getFullyQualifiedReferenceName() {
    return this.className == null ? fieldName : className + "." + fieldName;
  }

}
