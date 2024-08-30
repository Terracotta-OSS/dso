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
package com.tc.util.sequence;

public interface ObjectIDSequence {

  /**
   * Requests a new batch of object ids.
   * 
   * @param batchSize The number of object ids you want in your batch.
   * @return The first id of the next batch of object ids.
   */
  public long nextObjectIDBatch(int batchSize);

  public void setNextAvailableObjectID(long startID);

  public long currentObjectIDValue();
}
