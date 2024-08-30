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

import com.tc.net.GroupID;
import com.tc.object.ObjectID;

/**
 * Helper to aggregate size of all stripes.
 * <p>
 * Not thread safe. Must hold an external lock to manipulate this object.
 */
public class ServerMapGetAllSizeHelper {
  private final GroupID groupID;
  private boolean       done      = false;
  private long          totalSize = 0;
  private int           waitFor;

  public ServerMapGetAllSizeHelper(final ObjectID[] mapIDs) {
    this.groupID = new GroupID(mapIDs[0].getGroupID());
    this.waitFor = mapIDs.length;
  }

  public void addSize(final ObjectID mapID, final int size) {
    this.totalSize += size;
    if (--waitFor == 0) {
      this.done = true;
    }
  }

  public boolean isDone() {
    return this.done;
  }

  public long getTotalSize() {
    return this.totalSize;
  }

  public GroupID getGroupID() {
    return this.groupID;
  }
}
