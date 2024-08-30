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

/**
 * A {@link CollectionMismatch}that is used when one collection is missing an object that's present in the other
 * collection.
 */
class MissingObjectCollectionMismatch extends CollectionMismatch {
  public MissingObjectCollectionMismatch(Object originating, boolean originatingIsInCollectionOne,
                                         int originatingIndex, Stringifier describer) {
    super(originating, null, originatingIsInCollectionOne, originatingIndex, -1, describer);
  }

  @Override
  public String toString() {
    return "Missing object: there is no counterpart in " + comparedAgainstCollection() + " for " + originatingString();
  }
}