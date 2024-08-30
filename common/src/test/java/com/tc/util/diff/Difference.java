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
package com.tc.util.diff;

import com.tc.util.Assert;

/**
 * Represents a difference between two objects somewhere in their object graphs.
 */
public abstract class Difference {

  private final DifferenceContext where;

  public Difference(DifferenceContext where) {
    Assert.assertNotNull(where);
    this.where = where;
  }

  public DifferenceContext where() {
    return this.where;
  }

  public abstract Object a();
  public abstract Object b();
  @Override
  public abstract String toString();
  
  @Override
  public boolean equals(Object that) {
    if (! (that instanceof Difference)) return false;
    
    Difference diffThat = (Difference) that;
    
    return this.where.rawEquals(diffThat.where);
  }

}