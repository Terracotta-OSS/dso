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
import org.terracotta.management.resource.ResponseEntityV2;

import com.terracotta.management.resource.LogEntityV2;

import java.util.Set;

/**
 * An interface for service implementations providing TSA logs querying facilities.
 * <br>
 * The timestamp string describes a time since <i>now</i>.
 * The grammar for the timestamp string is as follows:
 * <pre>&lt;numeric value&gt;&lt;unit&gt;</pre>
 * Unit must be in this list:
 * <ul>
 * <li><b>d</b> for days</li>
 * <li><b>h</b> for hours</li>
 * <li><b>m</b> for minutes</li>
 * <li><b>s</b> for seconds</li>
 * </ul>
 * <br>
 * For instance, these strings are valid:
 * <ul>
 * <li><b>2d</b> means in the last 2 days</li>
 * <li><b>24h</b> means in the last 24 hours</li>
 * <li><b>1m</b> means in the last minute</li>
 * <li><b>10s</b> means in the last 10 seconds</li>
 * </ul>
 *
 * @author Ludovic Orban
 */
public interface LogsServiceV2 {

  /**
   * Get all the logs of the specified servers
   * @param serverNames A set of server names, null meaning all of them.
   * @return a collection of logs
   * @throws org.terracotta.management.ServiceExecutionException
   */
  ResponseEntityV2<LogEntityV2> getLogs(Set<String> serverNames) throws ServiceExecutionException;

  /**
   * Get the logs of the specified servers
   * @param serverNames A set of server names, null meaning all of them.
   * @param sinceWhen A timestamp used to filter out logs, only the ones newer than or at the
   *                  specified timestamp will be returned.
   * @return a collection of logs
   * @throws org.terracotta.management.ServiceExecutionException
   */
  ResponseEntityV2<LogEntityV2> getLogs(Set<String> serverNames, long sinceWhen) throws ServiceExecutionException;

  /**
   * Get the logs of the specified servers
   * @param serverNames A set of server names, null meaning all of them.
   * @param sinceWhen A string describing a timestamp that will be parsed.
   * @return a collection of logs
   * @throws org.terracotta.management.ServiceExecutionException
   */
  ResponseEntityV2<LogEntityV2> getLogs(Set<String> serverNames, String sinceWhen) throws ServiceExecutionException;


}
