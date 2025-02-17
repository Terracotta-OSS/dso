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
package com.tc.net;

import java.util.Arrays;
import java.util.Collection;

/**
 * This class's purpose is to give a definite order to a set of GroupIDs and determine the coordinator GroupID, that way
 * you don't have to encode the GroupIDs mapping in many places.
 */
public class OrderedGroupIDs {

  private final GroupID[] groupIDs;

  public OrderedGroupIDs(GroupID[] gids) {
    this.groupIDs = gids;
    Arrays.sort(groupIDs);
  }

  public Collection<GroupID> getGroupIDSet() {
    return Arrays.asList(groupIDs);
  }

  public GroupID[] getGroupIDs() {
    return this.groupIDs;
  }

  public int length() {
    return this.groupIDs.length;
  }

  public GroupID getGroup(int i) {
    return this.groupIDs[i];
  }

  public int getGroupIDIndex(GroupID gid) {
    return Arrays.binarySearch(this.groupIDs, gid);
  }

  public GroupID getActiveCoordinatorGroup() {
    // This assumption that index 0 is coordinator group should not be exposed anywhere else.
    return this.groupIDs[0];
  }

}
