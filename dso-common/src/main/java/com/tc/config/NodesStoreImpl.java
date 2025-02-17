/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
package com.tc.config;

import com.tc.config.schema.ActiveServerGroupConfig;
import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.config.schema.setup.L2ConfigurationSetupManager;
import com.tc.net.GroupID;
import com.tc.net.TCSocketAddress;
import com.tc.net.groups.Node;
import com.tc.object.config.schema.L2DSOConfig;
import com.tc.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class NodesStoreImpl implements NodesStore, TopologyChangeListener {
  private final Set<Node>                                   nodes;
  private final CopyOnWriteArraySet<TopologyChangeListener> listeners               = new CopyOnWriteArraySet<TopologyChangeListener>();
  private L2ConfigurationSetupManager                       configSetupManager;
  private volatile HashMap<String, GroupID>                 nodeNameToGidMap      = new HashMap<String, GroupID>();
  private volatile HashSet<String>                          nodeNamesForThisGroup = new HashSet<String>();
  private volatile HashMap<String, String>                  nodeNamesToServerNames  = new HashMap<String, String>();

  /**
   * used for tests
   */
  public NodesStoreImpl(Set<Node> nodes) {
    this.nodes = Collections.synchronizedSet(nodes);
  }

  public NodesStoreImpl(Set<Node> nodes, Set<String> nodeNamesForThisGroup,
                        HashMap<String, GroupID> serverNodeNameToGidMap, L2ConfigurationSetupManager configSetupManager) {
    this(nodes);
    this.nodeNamesForThisGroup.addAll(nodeNamesForThisGroup);
    this.nodeNameToGidMap = serverNodeNameToGidMap;
    this.configSetupManager = configSetupManager;
    initNodeNamesToServerNames();
  }

  @Override
  public void topologyChanged(ReloadConfigChangeContext context) {
    this.nodes.addAll(context.getNodesAdded());
    this.nodes.removeAll(context.getNodesRemoved());
    initNodeNamesToServerNames();

    for (TopologyChangeListener listener : listeners) {
      listener.topologyChanged(context);
    }
  }

  @Override
  public void registerForTopologyChange(TopologyChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public Node[] getAllNodes() {
    Assert.assertTrue(this.nodes.size() > 0);
    return this.nodes.toArray(new Node[this.nodes.size()]);
  }

  private void initNodeNamesToServerNames() {
    HashMap<String, String> tempNodeNamesToServerNames = new HashMap<String, String>();
    String[] serverNames = configSetupManager.allCurrentlyKnownServers();
    for (String serverName : serverNames) {
      try {
        L2DSOConfig l2Config = configSetupManager.dsoL2ConfigFor(serverName);
        String host = l2Config.tsaGroupPort().getBind();
        if (TCSocketAddress.WILDCARD_IP.equals(host) || TCSocketAddress.WILDCARD_IPv6.equals(host)) {
          host = l2Config.host();
        }
        if (host.contains(":")) {
          host = "[" + host + "]";
        }
        tempNodeNamesToServerNames.put(host + ":" + l2Config.tsaPort().getIntValue(), serverName);
      } catch (ConfigurationSetupException e) {
        throw new RuntimeException(e);
      }
    }
    this.nodeNamesToServerNames = tempNodeNamesToServerNames;
  }

  @Override
  public String getServerNameFromNodeName(String nodeName) {

    return nodeNamesToServerNames.get(nodeName);
  }

  /**
   * ServerNamesOfThisGroup methods ...
   */

  @Override
  public boolean hasServerInGroup(String serverName) {
    return nodeNamesForThisGroup.contains(serverName);
  }

  void updateServerNames(ReloadConfigChangeContext context) {
    HashSet<String> tmp = (HashSet<String>) nodeNamesForThisGroup.clone();

    for (Node n : context.getNodesAdded()) {
      tmp.add(n.getServerNodeName());
    }

    for (Node n : context.getNodesRemoved()) {
      tmp.remove(n.getServerNodeName());
    }

    this.nodeNamesForThisGroup = tmp;
  }

  /**
   * ServerNameGroupIDInfo methods ....
   */

  @Override
  public boolean hasServerInCluster(String name) {
    return nodeNameToGidMap.containsKey(name);
  }

  @Override
  public GroupID getGroupIDFromNodeName(String name) {
    return nodeNameToGidMap.get(name);
  }

  @Override
  public String getGroupNameFromNodeName(String nodeName) {
    if (configSetupManager == null) { return null; }
    ActiveServerGroupConfig asgc = configSetupManager.activeServerGroupsConfig()
        .getActiveServerGroupForL2(nodeNamesToServerNames.get(nodeName));
    if (asgc == null) { return null; }
    return asgc.getGroupName();
  }

  void updateServerNames(ReloadConfigChangeContext context, GroupID gid) {
    HashMap<String, GroupID> tempMap = (HashMap<String, GroupID>) nodeNameToGidMap.clone();
    for (Node n : context.getNodesAdded()) {
      tempMap.put(n.getServerNodeName(), gid);
    }

    for (Node n : context.getNodesRemoved()) {
      tempMap.remove(n.getServerNodeName());
    }
    this.nodeNameToGidMap = tempMap;
  }
}
