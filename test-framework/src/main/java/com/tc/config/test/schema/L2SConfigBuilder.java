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

import java.util.ArrayList;
import java.util.List;

/**
 * Allows you to build valid config for the L2s. This class <strong>MUST NOT</strong> invoke the actual XML beans to do
 * its work; one of its purposes is, in fact, to test that those beans are set up correctly.
 */
public class L2SConfigBuilder extends BaseConfigBuilder {

  private GroupConfigBuilder[]           groups;
  private GarbageCollectionConfigBuilder gc;
  private boolean                        restartable = false;

  public L2SConfigBuilder() {
    super(1, new String[] { "groups", "garbage-collection", "client-reconnect-window", "restartable", 
                            "failover-priority" });
  }

  public void setGroups(GroupConfigBuilder[] groups) {
    if (this.groups != null) { throw new IllegalStateException("groups already set"); }
    this.groups = groups;
    setProperty("groups", groups);
  }

  public L2ConfigBuilder[] getL2s() {
    List<L2ConfigBuilder> l2s = new ArrayList<L2ConfigBuilder>();

    if (groups != null) {
      for (GroupConfigBuilder group : groups) {
        for (L2ConfigBuilder l2 : group.getL2s()) {
          l2s.add(l2);
        }
      }
    }

    return l2s.toArray(new L2ConfigBuilder[l2s.size()]);
  }

  public void setL2s(L2ConfigBuilder[] l2Builder) {
    if (groups != null) {
      //
      throw new IllegalStateException("groups have already been set. The L2s must be set in the groups config");
    }

    GroupConfigBuilder group = new GroupConfigBuilder("auto-generated");
    group.setL2s(l2Builder);
    setGroups(new GroupConfigBuilder[] { group });
  }

  public void setGarbageCollection(GarbageCollectionConfigBuilder gc) {
    this.gc = gc;
    setProperty("garbage-collection", gc);
  }

  public void setRestartable(boolean data) {
    setProperty("restartable", data);
    restartable = data;
  }

  public void setFailoverPriority(String priority) {
    setProperty("failover-priority",  priority);
  }

  public void setReconnectWindowForPrevConnectedClients(int secs) {
    setProperty("client-reconnect-window", secs);
  }

  public GroupConfigBuilder[] getGroups() {
    return groups;
  }

  @Override
  public String toString() {
    String out = "";

    if (isSet("groups")) {
      for (GroupConfigBuilder group : groups) {
        out += group.toString();
      }
    }

    if (isSet("garbage-collection")) {
      out += gc.toString();
    }

    out += getRestartable();

    if (isSet("failover-priority")) {
      out += element("failover-priority");
    }

    if (isSet("client-reconnect-window")) {
      out += element("client-reconnect-window");
    }

    return out;
  }

  private String getRestartable() {
    if (!restartable) return "\n";
    return "\n<restartable enabled=\"" + restartable + "\"/>\n";
  }

  public static L2SConfigBuilder newMinimalInstance() {
    return new L2SConfigBuilder();
  }

}
