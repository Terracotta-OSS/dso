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
package com.tc.objectserver.core.api;

import com.tc.objectserver.api.ObjectManagerStats;
import com.tc.stats.counter.sampled.SampledCounter;
import com.tc.stats.counter.sampled.SampledCumulativeCounter;
import com.tc.stats.counter.sampled.derived.SampledRateCounter;

public interface DSOGlobalServerStats {

  SampledCounter getReadOperationRateCounter();

  SampledCounter getTransactionCounter();

  SampledCounter getEvictionRateCounter();

  SampledCounter getExpirationRateCounter();

  ObjectManagerStats getObjectManagerStats();

  SampledCounter getBroadcastCounter();

  SampledCounter getGlobalLockRecallCounter();

  SampledRateCounter getChangesPerBroadcastCounter();

  SampledRateCounter getTransactionSizeCounter();

  SampledCounter getGlobalLockCounter();
  
  SampledCumulativeCounter getServerMapGetSizeRequestsCounter();

  SampledCumulativeCounter getServerMapGetValueRequestsCounter();

  SampledCounter getOperationCounter();
}
