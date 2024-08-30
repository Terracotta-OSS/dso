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
package com.tc.config.test.schema;

import java.util.HashMap;
import java.util.Map;

public class GroupConfigBuilder extends BaseConfigBuilder {

  private final String      groupName;
  private L2ConfigBuilder[] l2s;
  private Integer           electionTime = null;

  public GroupConfigBuilder(String groupName) {
    super(5, new String[] { "election-time" });
    this.groupName = groupName;
  }

  public void setElectionTime(int value) {
    setProperty("election-time", value);
    this.electionTime = value;
  }

  @Override
  public String toString() {
    String out = "";

    Map attr = new HashMap();
    if (groupName != null) attr.put("group-name", groupName);
    if (electionTime != null) {
      attr.put("election-time", electionTime.toString());
    }

    out += openElement("mirror-group", attr);

    for (L2ConfigBuilder l2 : l2s) {
      out += l2.toString();
    }

    out += closeElement("mirror-group");

    return out;
  }

  public void setL2s(L2ConfigBuilder[] l2Builders) {
    l2s = l2Builders;
  }

  public L2ConfigBuilder[] getL2s() {
    return l2s;
  }
}
