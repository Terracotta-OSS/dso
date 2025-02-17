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
package com.tc.test.collections;

import org.apache.commons.lang3.builder.EqualsBuilder;

import com.tc.util.Assert;
import com.tc.util.Stringifier;

/**
 * Represents a single mismatch among collections.
 */
public abstract class CollectionMismatch {
  private final Object      originating;
  private final Object      comparedAgainst;
  private final boolean     originatingIsInCollectionOne;

  private final int         originatingIndex;
  private final int         comparedAgainstIndex;

  private final Stringifier describer;

  protected CollectionMismatch(Object originating, Object comparedAgainst, boolean originatingIsInCollectionOne,
                               int originatingIndex, int comparedAgainstIndex, Stringifier describer) {
    Assert.eval(originatingIndex >= 0);
    Assert.assertNotNull(describer);

    this.originating = originating;
    this.comparedAgainst = comparedAgainst;
    this.originatingIsInCollectionOne = originatingIsInCollectionOne;

    this.originatingIndex = originatingIndex;
    this.comparedAgainstIndex = comparedAgainstIndex;

    this.describer = describer;
  }

  protected final Object originating() {
    return this.originating;
  }

  protected final Object comparedAgainst() {
    return this.comparedAgainst;
  }
  
  protected final Stringifier describer() {
    return this.describer;
  }

  public boolean originatingIsInCollectionOne() {
    return this.originatingIsInCollectionOne;
  }

  protected final String originatingCollection() {
    return "collection " + (this.originatingIsInCollectionOne ? "one" : "two");
  }

  protected final String originatingString() {
    return "(" + originatingCollection() + ", index " + this.originatingIndex + "): " + describeOriginating();
  }

  protected final String describeOriginating() {
    return this.describer.toString(this.originating);
  }

  protected final String comparedAgainstCollection() {
    return "collection " + (this.originatingIsInCollectionOne ? "two" : "one");
  }

  protected final String comparedAgainstString() {
    return "(" + comparedAgainstCollection() + ", index " + this.comparedAgainstIndex + "): "
           + describeComparedAgainst();
  }

  protected final String describeComparedAgainst() {
    return this.describer.toString(this.comparedAgainst);
  }

  @Override
  public abstract String toString();

  @Override
  public boolean equals(Object that) {
    if (!(that instanceof CollectionMismatch)) return false;

    CollectionMismatch misThat = (CollectionMismatch) that;

    return new EqualsBuilder().append(this.originating, misThat.originating).append(this.comparedAgainst,
                                                                                    misThat.comparedAgainst)
        .append(this.originatingIsInCollectionOne, misThat.originatingIsInCollectionOne)
        .append(this.originatingIndex, misThat.originatingIndex).append(this.comparedAgainstIndex,
                                                                        misThat.comparedAgainstIndex).isEquals();
  }
}