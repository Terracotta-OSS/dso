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
import com.tc.license.ProductID;
import com.tc.net.NodeID;

public class NodeStateEventContext implements MultiThreadedEventContext {
  public static final int CREATE = 0;
  public static final int REMOVE = 1;

  private final int       type;
  private final NodeID    nodeID;
  private final ProductID productId;

  public NodeStateEventContext(int type, NodeID nodeID, final ProductID productId) {
    this.type = type;
    this.nodeID = nodeID;
    this.productId = productId;
    if ((type != CREATE) && (type != REMOVE)) { throw new IllegalArgumentException("invalid type: " + type); }
  }

  public int getType() {
    return type;
  }

  public NodeID getNodeID() {
    return nodeID;
  }

  @Override
  public Object getKey() {
    return nodeID;
  }

  public ProductID getProductId() {
    return productId;
  }
}
