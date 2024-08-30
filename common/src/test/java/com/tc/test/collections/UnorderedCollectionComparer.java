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
package com.tc.test.collections;

import com.tc.util.EqualityComparator;
import com.tc.util.Stringifier;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

/**
 * A {@link CollectionComparer}that doesn't require objects to be in the same order. However, if multiple instances of
 * a single object are present in at least one of the collections, then both collections must have the same number of
 * instances of that object.
 */
public class UnorderedCollectionComparer extends CollectionComparer {

  public UnorderedCollectionComparer(EqualityComparator comparator, Stringifier describer) {
    super(comparator, describer);
  }

  @Override
  protected CollectionMismatch[] doComparison(Object[] collectionOne, Object[] collectionTwo) {
    List mismatches = new ArrayList();

    BitSet oneUsed = new BitSet(collectionOne.length);
    BitSet twoUsed = new BitSet(collectionTwo.length);

    for (int i = 0; i < collectionOne.length; ++i) {
      if (!oneUsed.get(i)) {
        int numberInOne = 1;

        for (int j = i + 1; j < collectionOne.length; ++j) {
          if ((!oneUsed.get(j)) && isEqual(collectionOne[i], true, collectionOne[j], true, i, j)) {
            ++numberInOne;
            oneUsed.set(j);
          }
        }

        int numberInTwo = 0;

        for (int j = 0; j < collectionTwo.length; ++j) {
          if ((!twoUsed.get(j)) && isEqual(collectionOne[i], true, collectionTwo[j], false, i, j)) {
            ++numberInTwo;
            twoUsed.set(j);
          }
        }

        if (numberInOne != numberInTwo) {
          if (numberInTwo > 0) {
            mismatchedNumbers(collectionOne, mismatches, i, numberInOne, numberInTwo);
          } else {
            missingObject(collectionOne, mismatches, i);
          }
        }
      }
    }

    for (int i = 0; i < collectionTwo.length; ++i) {
      if (!twoUsed.get(i)) {
        mismatches.add(new MissingObjectCollectionMismatch(collectionTwo[i], false, i, describer()));
      }
    }

    return (CollectionMismatch[]) mismatches.toArray(new CollectionMismatch[mismatches.size()]);
  }

  private void missingObject(Object[] collectionOne, List mismatches, int i) {
    mismatches.add(new MissingObjectCollectionMismatch(collectionOne[i], true, i, describer()));
  }

  protected void mismatchedNumbers(Object[] collectionOne, List mismatches, int i, int numberInOne, int numberInTwo) {
    mismatches
        .add(new UnequalObjectCountCollectionMismatch(collectionOne[i], i, numberInOne, numberInTwo, describer()));
  }

}