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
package com.tc.objectserver.persistence;

import com.tc.object.gtx.GlobalTransactionID;
import com.tc.objectserver.gtx.GlobalTransactionDescriptor;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedSet;

/**
 * @author tim
 */
public class NullTransactionPersistor implements TransactionPersistor {
  @Override
  public Collection<GlobalTransactionDescriptor> loadAllGlobalTransactionDescriptors() {
    return Collections.EMPTY_LIST;
  }

  @Override
  public void saveGlobalTransactionDescriptor(final GlobalTransactionDescriptor gtx) {
    // Do nothing
  }

  @Override
  public void deleteAllGlobalTransactionDescriptors(final SortedSet<GlobalTransactionID> globalTransactionIDs) {
    // do nothing
  }
}
