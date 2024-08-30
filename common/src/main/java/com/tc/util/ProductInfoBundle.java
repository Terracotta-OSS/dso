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
package com.tc.util;

import java.util.ListResourceBundle;

public class ProductInfoBundle extends ListResourceBundle {
  @Override
  public Object[][] getContents() {
    return contents;
  }

  static final Object[][] contents = {
    {"moniker", "Terracotta"},
    {"invalid.timestamp", "The build timestamp string ''{0}'' does not appear to be valid."},
    {"load.properties.failure", "Unable to load build properties from ''{0}''."},
    {"copyright", "Copyright (c) 2003-2008 Terracotta, Inc. Copyright Super iPaaS Integration LLC, an IBM Company 2024. All rights reserved."},
    {"option.verbose", "Produces more detailed information."},
    {"option.raw", "Produces raw information."},
    {"option.help", "Shows this text."},
    {"version.message", ""}
  };
}
