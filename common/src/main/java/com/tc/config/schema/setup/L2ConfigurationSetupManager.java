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
package com.tc.config.schema.setup;

import com.tc.config.schema.ActiveServerGroupConfig;
import com.tc.config.schema.ActiveServerGroupsConfig;
import com.tc.config.schema.CommonL2Config;
import com.tc.config.schema.SecurityConfig;
import com.tc.object.config.schema.L2DSOConfig;
import com.tc.operatorevent.TerracottaOperatorEventLogger;
import com.tc.server.ServerConnectionValidator;

import java.io.InputStream;

/**
 * Knows how to set up configuration for L2.
 */
public interface L2ConfigurationSetupManager {
  String[] processArguments();

  CommonL2Config commonl2Config();

  L2DSOConfig dsoL2Config();

  ActiveServerGroupsConfig activeServerGroupsConfig();

  ActiveServerGroupConfig getActiveServerGroupForThisL2();

  String describeSources();

  InputStream rawConfigFile();

  InputStream effectiveConfigFile();

  String[] allCurrentlyKnownServers();

  String getL2Identifier();

  SecurityConfig getSecurity();

  CommonL2Config commonL2ConfigFor(String name) throws ConfigurationSetupException;

  L2DSOConfig dsoL2ConfigFor(String name) throws ConfigurationSetupException;

  TopologyReloadStatus reloadConfiguration(ServerConnectionValidator serverConnectionValidator,
                                           TerracottaOperatorEventLogger opeventlogger)
      throws ConfigurationSetupException;

  boolean isSecure();

  /**
   * To avoid split brain scenarios during startup of multiple nodes, one of the nodes must be explicitly
   * designated as the active so that the nodes that are not designated do not even attempt to become the
   * active(which could cause a split brain if there is a network partition between the nodes)
   */
  boolean isDesignatedActive();

  boolean isSafeModeConfigured();
}
