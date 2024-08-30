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

import com.tc.object.ObjectID;
import com.tc.objectserver.context.ServerMapEvictionContext;
import com.tc.objectserver.l1.impl.ClientObjectReferenceSet;

/**
 * EvictionTriggers signal that an eviction operation needs to occur on a given
 * map.
 * 
 * @author mscott
 */
public interface EvictionTrigger {
    /**
     * 
     * @return the objectid of the target map
     */
    ObjectID  getId();
    /**
     * change or confirm state on the target map and perform any other preprocessing at the
     * start of eviction
     * 
     * @param map the target map for evction
     * @return <code>true</code> if eviction should be started and the map has the proper state
     *         <code>false</code> abort eviction and exit.  map state should not be changed
     */
    boolean   startEviction(EvictableMap map);
    /**
     * return state on the map to non-evicting and perform any cleanup at the end of eviction
     * 
     * @param map
     */
    void      completeEviction(EvictableMap map);
    /**
     * Produce the map of evictable items contained in the map
     * 
     * @param targetMax max on the map segment
     * @param map       target map
     * @param clients   the client object id reference set
     * @return          a map of evictable items
     */
    ServerMapEvictionContext collectEvictionCandidates(int targetMax, String className, EvictableMap map, ClientObjectReferenceSet clients);
        
    long getRuntimeInMillis();
    
    int getCount();

    String getName();
    
    boolean isValid();    
}
