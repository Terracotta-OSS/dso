/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
package com.tc.objectserver.api;

import com.tc.async.api.PostInit;
import com.tc.object.ObjectID;
import com.tc.stats.counter.sampled.SampledCounter;
import com.tc.text.PrettyPrintable;

import java.util.Map;

public interface ServerMapEvictionManager extends PostInit, PrettyPrintable {

  public void startEvictor();

  public void runEvictor();
  
  public boolean doEvictionOn(EvictionTrigger trigger);
  
  public boolean scheduleCapacityEviction(ObjectID oid);
  
  public void evict(ObjectID oid, Map<Object, EvictableEntry> samples, String className, String cacheName);
  
  public SampledCounter getExpirationStatistics();
  
  public SampledCounter getEvictionStatistics();

}
