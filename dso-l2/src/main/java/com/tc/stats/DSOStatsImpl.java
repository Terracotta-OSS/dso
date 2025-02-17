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
package com.tc.stats;

import com.tc.objectserver.core.api.DSOGlobalServerStats;
import com.tc.objectserver.core.impl.ServerManagementContext;
import com.tc.stats.api.DSOStats;
import com.tc.stats.counter.sampled.SampledCounter;
import com.tc.stats.counter.sampled.derived.SampledRateCounter;

import java.lang.reflect.Method;

/**
 * This is the root interface to the global DSO Server statistics.
 * 
 * @see StatsSupport
 * @see DSOStats
 */

public class DSOStatsImpl implements DSOStats {

  private final DSOGlobalServerStats serverStats;
  private final SampledCounter       faultRate;
  private final SampledCounter       txnRate;
  private final SampledCounter       globalLockRecallRate;
  private final SampledRateCounter   transactionSizeRate;
  private final SampledCounter       broadcastRate;

  public DSOStatsImpl(ServerManagementContext context) {
    this.serverStats = context.getServerStats();
    this.faultRate = serverStats.getReadOperationRateCounter();
    this.txnRate = serverStats.getTransactionCounter();
    this.globalLockRecallRate = serverStats.getGlobalLockRecallCounter();
    this.transactionSizeRate = serverStats.getTransactionSizeCounter();
    this.broadcastRate = serverStats.getBroadcastCounter();
  }

  @Override
  public long getReadOperationRate() {
    return faultRate.getMostRecentSample().getCounterValue();
  }

  @Override
  public long getTransactionRate() {
    return txnRate.getMostRecentSample().getCounterValue();
  }

  @Override
  public long getEvictionRate() {
    return serverStats.getEvictionRateCounter().getMostRecentSample().getCounterValue();
  }

  @Override
  public long getExpirationRate() {
    return serverStats.getExpirationRateCounter().getMostRecentSample().getCounterValue();
  }

  @Override
  public long getGlobalLockRecallRate() {
    return globalLockRecallRate.getMostRecentSample().getCounterValue();
  }

  @Override
  public long getTransactionSizeRate() {
    return transactionSizeRate.getMostRecentSample().getCounterValue();
  }

  @Override
  public long getBroadcastRate() {
    return broadcastRate.getMostRecentSample().getCounterValue();
  }

  @Override
  public Number[] getStatistics(String[] names) {
    int count = names.length;
    Number[] result = new Number[count];
    Method method;

    for (int i = 0; i < count; i++) {
      try {
        method = getClass().getMethod("get" + names[i], new Class[] {});
        result[i] = (Number) method.invoke(this, new Object[] {});
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return result;
  }

  @Override
  public long getGlobalServerMapGetSizeRequestsCount() {
    return serverStats.getServerMapGetSizeRequestsCounter().getCumulativeValue();
  }

  @Override
  public long getGlobalServerMapGetSizeRequestsRate() {
    return serverStats.getServerMapGetSizeRequestsCounter().getMostRecentSample().getCounterValue();
  }

  @Override
  public long getGlobalServerMapGetValueRequestsCount() {
    return serverStats.getServerMapGetValueRequestsCounter().getCumulativeValue();
  }

  @Override
  public long getGlobalServerMapGetValueRequestsRate() {
    return serverStats.getServerMapGetValueRequestsCounter().getMostRecentSample().getCounterValue();
  }

  @Override
  public long getWriteOperationRate() {
    return serverStats.getOperationCounter().getMostRecentSample().getCounterValue();
  }
}
