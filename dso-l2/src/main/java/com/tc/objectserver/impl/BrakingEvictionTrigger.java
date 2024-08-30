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
package com.tc.objectserver.impl;

import com.tc.object.ObjectID;
import com.tc.objectserver.api.EvictableMap;
import com.tc.objectserver.context.ServerMapEvictionContext;
import com.tc.objectserver.l1.impl.ClientObjectReferenceSet;
import java.util.Map;

/**
 *
 * @author mscott
 */
public class BrakingEvictionTrigger extends AbstractEvictionTrigger {
    
    private final int turns ;

    public BrakingEvictionTrigger(ObjectID oid, int turns) {
        super(oid);
        this.turns = turns;
    }

    @Override
    public ServerMapEvictionContext collectEvictionCandidates(int targetMax, String className, EvictableMap map, ClientObjectReferenceSet clients) {
        int size = map.getSize();
        
        Map sampled = map.getRandomSamples(Math.round(size*turns/10000f),clients, SamplingType.FOR_EVICTION);

        return createEvictionContext(className, sampled);
    }

    
}
