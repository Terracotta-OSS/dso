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
package com.tc.object.config;

import org.terracotta.groupConfigForL1.ServerGroupsDocument;

import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.net.GroupID;
import com.terracottatech.config.L1ReconnectPropertiesDocument;

import java.util.Map;

public interface ConfigInfoFromL2 {

  public ServerGroupsDocument getServerGroupsFromL2() throws ConfigurationSetupException;

  public L1ReconnectPropertiesDocument getL1ReconnectPropertiesFromL2() throws ConfigurationSetupException;

  public Map<String, GroupID> getGroupNameIDMapFromL2() throws ConfigurationSetupException;
}
