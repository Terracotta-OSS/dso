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
package com.tcclient.cluster;

import java.io.Serializable;

/**
 * Describes a node in the Terracotta DSO cluster.
 *
 * @since 3.0.0
 */
public interface DsoNode extends Serializable {

  /**
   * Returns the unique string identifier that corresponds to the node.
   * <p>
   * This identifier is unique for the life-time of the cluster. However, if the cluster is completely shut down and
   * brought back up again, these identifiers might be recycled.
   *
   * @return string identifier for the node
   */
  public String getId();

  /**
   * Returns the IP address of the node.
   * <p>
   * This operation talks back to the server array the first time it's called. The result is cached for further use.
   *
   * @return the IP address of the node
   */
  public String getIp();

  /**
   * Returns the host name of the node.
   * <p>
   * This operation talks back to the server array the first time it's called. The result is cached for further use.
   *
   * @return the host name of the node
   */
  public String getHostname();
}