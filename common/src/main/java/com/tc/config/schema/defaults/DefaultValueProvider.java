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
package com.tc.config.schema.defaults;

import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

/**
 * Knows how to tell you the default value for an element in the config.
 */
public interface DefaultValueProvider {

  boolean possibleForXPathToHaveDefault(String xpath);
  
  XmlObject defaultFor(SchemaType baseType, String xpath) throws XmlException;
  
  boolean hasDefault(SchemaType baseType, String xpath) throws XmlException;
  
  boolean isOptional(SchemaType baseType, String xpath) throws XmlException;

}
