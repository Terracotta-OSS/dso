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
import java.util.List;

/**
 * A {@link CollectionComparer}that requires the collections to be in the same order.
 */
public class OrderedCollectionComparer extends CollectionComparer {

  public OrderedCollectionComparer(EqualityComparator comparator, Stringifier describer) {
    super(comparator, describer);
  }

  @Override
  protected CollectionMismatch[] doComparison(Object[] collectionOne, Object[] collectionTwo) {
    List mismatches = new ArrayList();

    for (int i = 0; i < collectionOne.length; ++i) {
      Object objectOne = collectionOne[i];

      if (i >= collectionTwo.length) {
        mismatches.add(new MissingObjectCollectionMismatch(objectOne, true, i, describer()));
      } else {
        Object objectTwo = collectionTwo[i];
        boolean isEqual = isEqual(objectOne, true, objectTwo, true, i, i);

        if (!isEqual) {
          mismatches.add(new UnequalObjectCollectionMismatch(objectOne, objectTwo, true, i, i, describer()));
        }
      }
    }

    for (int i = collectionOne.length; i < collectionTwo.length; ++i) {
      mismatches.add(new MissingObjectCollectionMismatch(collectionTwo[i], false, i, describer()));
    }

    return (CollectionMismatch[]) mismatches.toArray(new CollectionMismatch[mismatches.size()]);
  }

}