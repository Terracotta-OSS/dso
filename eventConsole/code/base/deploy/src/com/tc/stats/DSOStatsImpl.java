/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.stats;

import com.tc.objectserver.api.ObjectManagerStats;
import com.tc.objectserver.core.api.DSOGlobalServerStats;
import com.tc.objectserver.core.impl.ServerManagementContext;
import com.tc.stats.counter.sampled.SampledCounter;
import com.tc.stats.counter.sampled.derived.SampledRateCounter;

import java.lang.reflect.Method;

/**
 * This is the root interface to the global DSO Server statistics.
 * 
 * @see StatsSupport
 * @see DSOStats
 */

public class DSOStatsImpl extends StatsSupport implements DSOStats {

  private final DSOGlobalServerStats serverStats;
  private final SampledCounter       faultRate;
  private final SampledCounter       flushRate;
  private final ObjectManagerStats   objMgrStats;
  private final SampledCounter       txnRate;
  private final SampledCounter       globalLockRecallRate;
  private final SampledRateCounter   transactionSizeRate;
  private final SampledCounter       broadcastRate;

  public DSOStatsImpl(ServerManagementContext context) {
    this.serverStats = context.getServerStats();
    this.objMgrStats = serverStats.getObjectManagerStats();
    this.faultRate = serverStats.getObjectFaultCounter();
    this.flushRate = serverStats.getObjectFlushCounter();
    this.txnRate = serverStats.getTransactionCounter();
    this.globalLockRecallRate = serverStats.getGlobalLockRecallCounter();
    this.transactionSizeRate = serverStats.getTransactionSizeCounter();
    this.broadcastRate = serverStats.getBroadcastCounter();
  }

  public long getObjectFaultRate() {
    return faultRate.getMostRecentSample().getCounterValue();
  }

  public long getObjectFlushRate() {
    return flushRate.getMostRecentSample().getCounterValue();
  }

  public long getTransactionRate() {
    return txnRate.getMostRecentSample().getCounterValue();
  }

  public double getCacheHitRatio() {
    return objMgrStats.getCacheHitRatio();
  }

  public long getCacheMissRate() {
    return objMgrStats.getCacheMissRate().getCounterValue();
  }

  public long getFlushedRate() {
    return objMgrStats.getFlushedRate().getCounterValue();
  }
  
  public long getGlobalLockRecallRate() {
    return globalLockRecallRate.getMostRecentSample().getCounterValue();
  }

  public long getTransactionSizeRate() {
    return transactionSizeRate.getMostRecentSample().getCounterValue();
  }

  public long getBroadcastRate() {
    return broadcastRate.getMostRecentSample().getCounterValue();
  }

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

 
}
