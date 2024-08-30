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
package com.tc.l2.ha;

import com.tc.util.Assert;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class WeightGeneratorFactory {
  
  private final List generators = new ArrayList();
  
  public static final WeightGenerator RANDOM_WEIGHT_GENERATOR = new WeightGenerator() {
    @Override
    public long getWeight() {
      SecureRandom r = new SecureRandom();
      return r.nextLong();
    }
  };
  
  public WeightGeneratorFactory() {
    super();
  }
  
  public synchronized void add(WeightGenerator g) {
    Assert.assertNotNull(g);
    generators.add(g);
  }
  
  public synchronized void remove(WeightGenerator g) {
    Assert.assertNotNull(g);
    generators.remove(g);
  }
  
  public synchronized long[] generateWeightSequence() {
    long weights[] = new long[generators.size()];
    for (int i = 0; i < weights.length; i++) {
      weights[i] = ((WeightGenerator) generators.get(i)).getWeight();
    }
    return weights;
  }
  
  public synchronized long[] generateMaxWeightSequence() {
    long weights[] = new long[generators.size()];
    for (int i = 0; i < weights.length; i++) {
      weights[i] = Long.MAX_VALUE;
    }
    return weights;
  }
  
  public static WeightGeneratorFactory createDefaultFactory() {
    WeightGeneratorFactory wgf = new WeightGeneratorFactory();
    wgf.add(RANDOM_WEIGHT_GENERATOR);
    wgf.add(RANDOM_WEIGHT_GENERATOR);
    return wgf;
  }

  public static interface WeightGenerator {
    public long getWeight();
  }

}
