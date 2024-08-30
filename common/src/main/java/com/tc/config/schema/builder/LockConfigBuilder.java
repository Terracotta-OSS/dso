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
package com.tc.config.schema.builder;

public interface LockConfigBuilder {

  public static final String TAG_AUTO_LOCK  = "autolock";
  public static final String TAG_NAMED_LOCK = "named-lock";

  public void setLockName(String value);

  public void setMethodExpression(String value);

  public static final String LEVEL_WRITE             = "write";
  public static final String LEVEL_READ              = "read";
  public static final String LEVEL_CONCURRENT        = "concurrent";
  public static final String LEVEL_SYNCHRONOUS_WRITE = "synchronous-write";

  public void setLockLevel(String value);

  public void setLockName(int value);

}
