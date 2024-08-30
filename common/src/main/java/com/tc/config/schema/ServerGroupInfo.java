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
package com.tc.config.schema;

import java.io.Serializable;

public class ServerGroupInfo implements Serializable {
  private final L2Info[] members;
  private final String   name;
  private final int      id;
  private final boolean  isCoordinator;

  public ServerGroupInfo(L2Info[] members, String name, int id, boolean isCoordinator) {
    this.members = members;
    this.name = name;
    this.id = id;
    this.isCoordinator = isCoordinator;
  }

  public L2Info[] members() {
    return members;
  }

  public String name() {
    return name;
  }

  public int id() {
    return id;
  }

  public boolean isCoordinator() {
    return isCoordinator;
  }
}
