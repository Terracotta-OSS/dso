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
package com.tc.async.api;

/**
 * @author steve Used to filter events. Note, these are evaluted in the context of the sender so they should be fast.
 */
public interface AddPredicate {

  /**
   * Take a look at the context in the thread of the sender and see if you want to take it or ignore it or do something
   * else to it.
   * 
   * @param context
   * @return
   */
  public boolean accept(EventContext context);
}
