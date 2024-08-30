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
package com.tc.objectserver.managedobject;

import com.tc.object.dna.api.LogicalChangeID;
import com.tc.object.dna.api.LogicalChangeResult;
import com.tc.util.Assert;

import java.util.HashMap;
import java.util.Map;

public class DefaultResultRecorderImpl implements ApplyResultRecorder {
  private final Map<LogicalChangeID, LogicalChangeResult> changeResults = new HashMap<LogicalChangeID, LogicalChangeResult>();

  @Override
  public void recordResult(LogicalChangeID changeID, LogicalChangeResult result) {
    if (changeID.isNull()) {
      // L1 not interested in the result
      return;
    }
    Assert.assertNull(changeResults.put(changeID, result));
  }

  @Override
  public Map<LogicalChangeID, LogicalChangeResult> getResults() {
    return changeResults;
  }

  @Override
  public void recordResults(Map<LogicalChangeID, LogicalChangeResult> results) {
    changeResults.putAll(results);
  }

  @Override
  public boolean needPersist() {
    for (LogicalChangeResult logicalChangeResult : changeResults.values()) {
      if (logicalChangeResult.isSuccess()) {
        return true;
      }
    }
    return false;
  }

}
