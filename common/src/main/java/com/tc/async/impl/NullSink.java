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
import com.tc.async.api.Sink;
import com.tc.exception.ImplementMe;
import com.tc.stats.Stats;

import java.util.Collection;

/**
 * @author steve
 */
public class NullSink implements Sink {
  public NullSink() {
    //
  }

  @Override
  public boolean addLossy(EventContext context) {
    return false;
  }

  @Override
  public void addMany(Collection contexts) {
    //
  }

  @Override
  public void add(EventContext context) {
    //
  }

  @Override
  public void setAddPredicate(AddPredicate predicate) {
    //
  }

  @Override
  public AddPredicate getPredicate() {
    return null;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public void clear() {
    throw new ImplementMe();
  }

  @Override
  public void enableStatsCollection(boolean enable) {
    throw new ImplementMe();
  }

  @Override
  public Stats getStats(long frequency) {
    throw new ImplementMe();
  }

  @Override
  public Stats getStatsAndReset(long frequency) {
    throw new ImplementMe();
  }

  @Override
  public boolean isStatsCollectionEnabled() {
    throw new ImplementMe();
  }

  @Override
  public void resetStats() {
    throw new ImplementMe();
    
  }

}
