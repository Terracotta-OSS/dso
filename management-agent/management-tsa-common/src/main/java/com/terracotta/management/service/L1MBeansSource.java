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

import com.terracotta.management.web.proxy.ProxyException;

/**
 * @author Ludovic Orban
 */
public interface L1MBeansSource {

  /**
   * Check if the current server contains the tunneled JMX MBeans
   * @return true if the current server contains the tunneled JMX MBeans, false otherwise
   */
  boolean containsJmxMBeans();

  /**
   * Get the name of the L2 containing the tunneled JMX MBeans
   * @return the name, or null if there is no server containing the MBeans
   * @throws ServiceExecutionException
   */
  String getActiveL2ContainingMBeansName() throws ServiceExecutionException;

  /**
   * Notify that the current request should be proxied to the L2 containing the L1 MBeans. This method always
   * returns by throwing an exception.
   * @throws ProxyException thrown to notify that the current request should be proxied. This exception should be
   *                        caught and processed to perform the actual proxying.
   * @throws ServiceExecutionException
   */
  void proxyClientRequest() throws ProxyException, ServiceExecutionException;

}
