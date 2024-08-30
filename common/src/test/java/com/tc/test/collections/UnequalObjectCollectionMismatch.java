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

import com.tc.util.Stringifier;
import com.tc.util.diff.DifferenceBuilder;
import com.tc.util.diff.Differenceable;

/**
 * A {@link CollectionMismatch}that is used when two objects aren't equal to each other.
 */
class UnequalObjectCollectionMismatch extends CollectionMismatch {

  public UnequalObjectCollectionMismatch(Object originating, Object comparedAgainst,
                                         boolean originatingIsInCollectionOne, int originatingIndex,
                                         int comparedAgainstIndex, Stringifier describer) {
    super(originating, comparedAgainst, originatingIsInCollectionOne, originatingIndex, comparedAgainstIndex, describer);
  }

  @Override
  public String toString() {
    if (originating() != null && comparedAgainst() != null && (originating() instanceof Differenceable)
        && (comparedAgainst() instanceof Differenceable)) {
      // formatting
      return "Unequal objects: differences are: "
             + DifferenceBuilder.describeDifferences((Differenceable) originating(),
                                                     (Differenceable) comparedAgainst(), describer()) + "\n"
             + originatingString() + "\nis not equal to\n" + comparedAgainstString() + "\n";
    }

    return "Unequal objects: " + originatingString() + " is not equal to " + comparedAgainstString();
  }
  
}