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

/**
 * @author Anthony Dahanne
 *
 * This service allows you to retrieve the agentId of the L1, from its remote address.
 * Several L1s can be started from a single VM (a L1 for each CM), and while they share their agentId,
 * they each have their own RemoteAddress (toolkit connection)
 *
 */
public interface L1AgentIdRetrievalServiceV2 {
  String getAgentIdFromRemoteAddress(String remoteAddress, String clientID) throws ServiceExecutionException;
}
