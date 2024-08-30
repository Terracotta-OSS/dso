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

import com.tc.stats.Monitorable;

import java.util.Collection;

/**
 * Represents the sink in the SEDA system
 */
public interface Sink extends Monitorable {
  /**
   * The context may or may not be added to the sink depending on the state of the sink. The implementation can make the
   * decision based on various factors.
   * 
   * @param context
   * @return
   */
  public boolean addLossy(EventContext context);

  /**
   * Add More than one context at a time. This is more efficient then adding one at a time
   * 
   * @param contexts
   */
  public void addMany(Collection contexts);

  /**
   * Add a event to the Sink (no, really!)
   * 
   * @param context
   */
  public void add(EventContext context);

  /**
   * The predicate allows the Sink to reject the EventContext rather than handle it
   * 
   * @param predicate
   */
  public void setAddPredicate(AddPredicate predicate);

  /**
   * Get the predicate 
   * 
   */
  public AddPredicate getPredicate();

  /**
   * returns the current size of the queue
   * 
   * @return
   */
  public int size();

  public void clear();

}
