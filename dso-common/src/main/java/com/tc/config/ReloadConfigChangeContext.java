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
package com.tc.config;

import com.tc.net.groups.Node;

import java.util.ArrayList;
import java.util.List;

public class ReloadConfigChangeContext {
  private final List<Node> nodesAdded = new ArrayList<Node>();
  private final List<Node> nodesRemoved = new ArrayList<Node>();
  
  public void update(ReloadConfigChangeContext context) {
    nodesAdded.addAll(context.nodesAdded);
    nodesRemoved.addAll(context.nodesRemoved);
  }

  public List<Node> getNodesAdded() {
    return nodesAdded;
  }

  public List<Node> getNodesRemoved() {
    return nodesRemoved;
  }

  @Override
  public String toString() {
    return "ReloadConfigChangeContext [nodesAdded=" + nodesAdded + ", nodesRemoved=" + nodesRemoved + "]";
  }
}
