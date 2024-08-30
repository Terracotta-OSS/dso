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
package com.terracotta.management.resource;

/**
 * A {@link org.terracotta.management.resource.AbstractEntityV2} representing a TSA server or client
 * thread dump from the management API.
 *
 * @author Ludovic Orban
 */
public class ThreadDumpEntityV2 extends AbstractTsaEntityV2 {

  public enum NodeType {
    CLIENT, SERVER
  }

  private String sourceId;

  private String dump;

  private NodeType nodeType;

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public void setNodeType(NodeType type) {
    nodeType = type;
  }

  public String getDump() {
    return dump;
  }

  public void setDump(String dump) {
    this.dump = dump;
  }
  
  public NodeType getNodeType() {
    return nodeType;
  }
}
