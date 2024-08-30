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
package com.terracotta.management.service;

import org.terracotta.management.ServiceExecutionException;
import org.terracotta.management.l1bridge.RemoteCallDescriptor;

import java.util.Map;
import java.util.Set;

/**
 * An interface for service implementations providing an abstraction of remote agents bridging.
 *
 * @author Ludovic Orban
 */
public interface RemoteAgentBridgeService {

  /**
   * Get the connected remote agents node names. This call does not go over the network.
   *
   * @return a set of connected remote agents node names.
   * @throws ServiceExecutionException
   */
  Set<String> getRemoteAgentNodeNames() throws ServiceExecutionException;

  /**
   * Get the connected remote agent node details. This goes over the network to fetch details.
   *
   * @param remoteAgentName the remote agent node name.
   * @return a map of attributes as values.
   * @throws ServiceExecutionException
   */
  Map<String, String> getRemoteAgentNodeDetails(String remoteAgentName) throws ServiceExecutionException;

  /**
   * Get the connected remote agent agency. This may or may not go over the network depending if 'agency'
   * is registered as a key property of the RemoteAgentEndpoint's ObjectName.
   *
   * @param remoteAgentName the remote agent node name.
   * @return the agent's agency.
   * @throws ServiceExecutionException
   */
  String getRemoteAgentAgency(String remoteAgentName) throws ServiceExecutionException;

  /**
   * Invoke an method on the remote agent.
   *
   * @param nodeName the remote agent node name.
   * @param remoteCallDescriptor the remote call descriptor.
   * @return the serialized response.
   * @throws ServiceExecutionException
   */
  byte[] invokeRemoteMethod(String nodeName, RemoteCallDescriptor remoteCallDescriptor) throws ServiceExecutionException;

}
