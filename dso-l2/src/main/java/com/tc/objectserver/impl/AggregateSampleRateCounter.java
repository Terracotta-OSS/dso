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

import com.tc.stats.counter.sampled.SampledCounterConfig;
import com.tc.stats.counter.sampled.SampledCounterImpl;
import com.tc.stats.counter.sampled.derived.SampledRateCounter;

/**
 *
 * @author mscott
 */
public class AggregateSampleRateCounter extends SampledCounterImpl implements SampledRateCounter {
    
    private long numerator;
    private long denominator;
    private static SampledCounterConfig config = new SampledCounterConfig(5, 100, false, 0);
    
    public AggregateSampleRateCounter() {
        super(config);
    }

    @Override
    public synchronized void increment(long numeratorParam, long denominatorParam) {
        this.numerator += numeratorParam;
        this.denominator += denominatorParam;
        super.increment();
    }

    @Override
    public synchronized void decrement(long numeratorParam, long denominatorParam) {
        this.numerator -= numeratorParam;
        this.denominator -= denominatorParam;
        super.decrement();
    }

    @Override
    public synchronized void setValue(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
        super.setValue(0);
    }

    @Override
    public synchronized void setNumeratorValue(long newValue) {
        this.numerator = newValue;
    }

    @Override
    public synchronized void setDenominatorValue(long newValue) {
        this.denominator = newValue;
    }
    
    @Override
    public synchronized long getValue() {
      return this.numerator;
    }
    
    public synchronized long getNumeratorValue() {
        return this.numerator;
    }
     
    public synchronized long getDenominatorValue() {
        return this.denominator;
    }
    
    public long getCount() {
        return super.getValue();
    }

    @Override
    public String toString() {
        return "evictions=" + numerator + ", time=" + denominator + ", segments=" + super.getValue();
    }
    
}
