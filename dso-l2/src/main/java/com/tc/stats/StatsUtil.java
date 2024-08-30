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
package com.tc.stats;

import com.tc.stats.counter.Counter;
import com.tc.stats.counter.sampled.SampledCounter;
import com.tc.stats.counter.sampled.TimeStampedCounterValue;
import com.tc.stats.statistics.CountStatistic;
import com.tc.stats.statistics.CountStatisticImpl;

// TODO: remove me

public class StatsUtil {

  public static CountStatistic makeCountStat(SampledCounter counter) {
    TimeStampedCounterValue sample = counter.getMostRecentSample();
    return makeCountStat(sample);
  }

  public static CountStatistic makeCountStat(TimeStampedCounterValue sample) {
    CountStatisticImpl stat = new CountStatisticImpl();
    // TODO: we could include the min/max/avg in the returned stat
    stat.setLastSampleTime(sample.getTimestamp());
    stat.setCount(sample.getCounterValue());
    return stat;
  }

  public static CountStatistic makeCountStat(Counter counter) {
    CountStatisticImpl stat = new CountStatisticImpl();
    stat.setLastSampleTime(System.currentTimeMillis());
    stat.setCount(counter.getValue());
    return stat;
  }

}
