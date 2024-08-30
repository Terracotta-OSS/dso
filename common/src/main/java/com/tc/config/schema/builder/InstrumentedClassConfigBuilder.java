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

public interface InstrumentedClassConfigBuilder {

  public void setIsInclude(boolean isInclude);

  public void setClassExpression(String value);

  public void setHonorTransient(String value);

  public void setHonorTransient(boolean value);

  public void setCallConstructorOnLoad(String value);

  public void setCallConstructorOnLoad(boolean value);
  
  public void setCallMethodOnLoad(String value);

}
