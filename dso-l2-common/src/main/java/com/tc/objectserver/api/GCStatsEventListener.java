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
package com.tc.objectserver.api;


/**
 * Interface for those interested in listening to Object Manager events. I'm thinking this event interface should really
 * only be for "low volume" events since there is fair amount of overhead per event. So things like "object looked up",
 * or "cache hit" aren't very good candidates for this interface
 */
public interface GCStatsEventListener {

  /**
   * notify the listener that GCStats object has been updated
   * @param stats statistics about this collection
   */
  public void update(GCStats stats);

 
}
