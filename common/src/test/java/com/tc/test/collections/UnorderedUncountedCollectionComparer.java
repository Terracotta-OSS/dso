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

import com.tc.util.EqualityComparator;
import com.tc.util.Stringifier;

import java.util.List;

/**
 * An {@link UnorderedCollectionComparer}that further ignores whether the two collections have different numbers of
 * instances of the same object, as long as both collections have at least one of that object. (In other words, [ 'A',
 * 'A', 'B', 'C' ] compares equal to [ 'A', 'B', 'C', 'C' ], but not [ 'A', 'A', 'C' ].)
 */
public class UnorderedUncountedCollectionComparer extends UnorderedCollectionComparer {

  public UnorderedUncountedCollectionComparer(EqualityComparator comparator, Stringifier describer) {
    super(comparator, describer);
  }

  @Override
  protected void mismatchedNumbers(Object[] collectionOne, List mismatches, int i, int numberInOne, int numberInTwo) {
    // Nothing to do here.
  }
  
}