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
package com.tc.objectserver.context;

import com.tc.async.api.MultiThreadedEventContext;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.objectserver.tx.TxnObjectGrouping;

import java.util.Collection;

/**
 * @author tim
 */
public class FlushApplyCommitContext implements MultiThreadedEventContext {
  private final TxnObjectGrouping grouping;

  public FlushApplyCommitContext(final TxnObjectGrouping grouping) {
    this.grouping = grouping;
  }

  public Collection<ManagedObject> getObjectsToRelease() {
    return grouping.getObjects();
  }

  @Override
  public Object getKey() {
    return grouping;
  }
}
