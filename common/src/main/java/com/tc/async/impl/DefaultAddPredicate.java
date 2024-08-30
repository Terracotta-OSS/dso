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
package com.tc.async.impl;

import com.tc.async.api.AddPredicate;
import com.tc.async.api.EventContext;

/**
 * @author steve Using the NullObject pattern to put a predicate in when no predicate exists
 */
public class DefaultAddPredicate implements AddPredicate {

  private final static AddPredicate instance = new DefaultAddPredicate();

  public static AddPredicate getInstance() {
    return instance;
  }

  /*
   * (non-Javadoc)
   * 
   * @see com.tc.async.api.AddPredicate#accept(com.tc.async.api.EventContext)
   */
  @Override
  public boolean accept(EventContext context) {
    return true;
  }

}
