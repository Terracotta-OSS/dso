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
package com.tc.net.groups;

import com.google.common.base.Throwables;
import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventContext;
import com.tc.async.api.Sink;
import com.tc.async.api.StageManager;
import com.tc.async.impl.ConfigurationContextImpl;
import com.tc.async.impl.MockSink;
import com.tc.async.impl.StageManagerImpl;
import com.tc.config.NodesStore;
import com.tc.config.NodesStoreImpl;
import com.tc.l2.context.StateChangedEvent;
import com.tc.l2.ha.WeightGeneratorFactory;
import com.tc.l2.msg.L2StateMessage;
import com.tc.l2.state.StateManager;
import com.tc.l2.state.StateManagerConfig;
import com.tc.l2.state.StateManagerImpl;
import com.tc.l2.state.sbp.SBPResolverImpl;
import com.tc.lang.TCThreadGroup;
import com.tc.lang.TestThrowableHandler;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.net.NodeID;
import com.tc.net.protocol.transport.NullConnectionPolicy;
import com.tc.objectserver.persistence.TestClusterStatePersistor;
import com.tc.test.TCTestCase;
import com.tc.util.PortChooser;
import com.tc.util.State;
import com.tc.util.concurrent.NoExceptionLinkedQueue;
import com.tc.util.concurrent.QueueFactory;
import com.tc.util.concurrent.ThreadUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TCGroupStateManagerTest extends TCTestCase {

  private final static String   LOCALHOST = "localhost";
  private static final TCLogger logger    = TCLogging.getLogger(StateManagerImpl.class);
  private TCThreadGroup         threadGroup;
  private TestThrowableHandler throwableHandler;

  @Override
  public void setUp() {
    throwableHandler = new TestThrowableHandler(logger);
    threadGroup = new TCThreadGroup(throwableHandler, "StateManagerTestGroup");
  }

  @Override
  protected void tearDown() throws Exception {
    try {
      throwableHandler.throwIfNecessary();
    } catch (Throwable throwable) {
      throw Throwables.propagate(throwable);
    }
  }

  public void testStateManagerTwoServers() throws Exception {
    // 2 nodes join concurrently
    // setup throwable ThreadGroup to catch AssertError from threads.
    nodesConcurrentJoining(2);
  }

  public void testStateManagerThreeServers() throws Exception {
    // 3 nodes join concurrently
    // setup throwable ThreadGroup to catch AssertError from threads.
    nodesConcurrentJoining(3);
  }

  public void testStateManagerSixServers() throws Exception {
    // 6 nodes join concurrently
    // setup throwable ThreadGroup to catch AssertError from threads.
    nodesConcurrentJoining(6);
  }

  public void testStateManagerMixJoinAndElect3() throws Exception {
    // 3 nodes mix join and election
    // setup throwable ThreadGroup to catch AssertError from threads.
    nodesMixJoinAndElect(3);
  }

  public void testStateManagerMixJoinAndElect6() throws Exception {
    // 6 nodes mix join and election
    // setup throwable ThreadGroup to catch AssertError from threads.
    nodesMixJoinAndElect(6);
  }

  public void testStateManagerJoinLater3() throws Exception {
    // first node shall be active and remaining 2 nodes join later
    // setup throwable ThreadGroup to catch AssertError from threads.
    nodesJoinLater(3);
  }

  public void testStateManagerJoinLater6() throws Exception {
    // first node shall be active and remaining 5 nodes join later
    // setup throwable ThreadGroup to catch AssertError from threads.
    nodesJoinLater(6);
  }

  // -----------------------------------------------------------------------

  private void nodesConcurrentJoining(int nodes) throws Exception {
    System.out.println("*** Testing " + nodes + " nodes join at same time.");

    TCGroupManagerImpl[] groupMgr = new TCGroupManagerImpl[nodes];
    ChangeSink[] sinks = new ChangeSink[nodes];
    PortChooser pc = new PortChooser();
    int[] ports = new int[nodes];
    Node[] allNodes = new Node[nodes];
    for (int i = 0; i < nodes; ++i) {
      ports[i] = pc.chooseRandom2Port();
      allNodes[i] = new Node(LOCALHOST, ports[i], ports[i] + 1);
    }

    StateManager[] managers = new StateManager[nodes];
    ElectionThread[] elections = new ElectionThread[nodes];
    L2StateMessageStage[] msgStage = new L2StateMessageStage[nodes];
    for (int i = 0; i < nodes; ++i) {
      managers[i] = createStateManageNode(i, allNodes, sinks, groupMgr, msgStage);
      elections[i] = new ElectionThread(managers[i]);
    }

    // joining
    System.out.println("*** Start Joining...");
    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, allNodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    for (int i = 0; i < nodes; ++i) {
      groupMgr[i].join(allNodes[i], nodeStore);
    }
    ThreadUtil.reallySleep(1000 * nodes);

    System.out.println("*** Start Election...");
    // run them concurrently
    for (int i = 0; i < nodes; ++i) {
      elections[i].start();
    }
    for (int i = 0; i < nodes; ++i) {
      elections[i].join();
    }

    ThreadUtil.reallySleep(1000 * nodes);
    // verification
    int activeCount = 0;
    for (int i = 0; i < nodes; ++i) {
      boolean active = managers[i].isActiveCoordinator();
      if (active) ++activeCount;
      System.out.println("*** Server[" + i + "] state is " + sinks[i]);
    }
    assertEquals("Active coordinator", 1, activeCount);

    shutdown(groupMgr, msgStage);
  }

  private void shutdown(TCGroupManagerImpl[] groupMgr, L2StateMessageStage[] msgStage) {
    // shut them down
    shutdown(groupMgr, msgStage, 0, groupMgr.length);
  }

  private void shutdown(TCGroupManagerImpl[] groupMgr, L2StateMessageStage[] msgStage, int start, int end) {
    for (int i = start; i < end; ++i) {
      try {
        msgStage[i].requestStop();
        ThreadUtil.reallySleep(100);
        groupMgr[i].stop(1000);
      } catch (Exception ex) {
        System.out.println("*** Failed to stop Server[" + i + "] " + groupMgr[i] + " " + ex);
      }
    }
    System.out.println("*** shutdown done");
  }

  private void nodesMixJoinAndElect(int nodes) throws Exception {
    System.out.println("*** Testing " + nodes + " nodes mixed join and election at same time.");

    TCGroupManagerImpl[] groupMgr = new TCGroupManagerImpl[nodes];
    ChangeSink[] sinks = new ChangeSink[nodes];
    PortChooser pc = new PortChooser();
    int[] ports = new int[nodes];
    Node[] allNodes = new Node[nodes];
    for (int i = 0; i < nodes; ++i) {
      ports[i] = pc.chooseRandom2Port();
      allNodes[i] = new Node(LOCALHOST, ports[i], ports[i] + 1);
    }

    StateManager[] managers = new StateManager[nodes];
    ElectionThread[] elections = new ElectionThread[nodes];
    L2StateMessageStage[] msgStage = new L2StateMessageStage[nodes];
    for (int i = 0; i < nodes; ++i) {
      managers[i] = createStateManageNode(i, allNodes, sinks, groupMgr, msgStage);
      elections[i] = new ElectionThread(managers[i]);
    }

    // Joining and Electing
    System.out.println("*** Start Joining and Electing...");
    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, allNodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    groupMgr[0].join(allNodes[0], nodeStore);
    for (int i = 0; i < nodes - 1; ++i) {
      elections[i].start();
      groupMgr[i + 1].join(allNodes[i + 1], nodeStore);
    }
    elections[nodes - 1].start();

    for (int i = 0; i < nodes; ++i) {
      elections[i].join();
    }

    ThreadUtil.reallySleep(1000 * nodes);
    // verification
    int activeCount = 0;
    for (int i = 0; i < nodes; ++i) {
      boolean active = managers[i].isActiveCoordinator();
      if (active) ++activeCount;
      System.out.println("*** Server[" + i + "] state is " + sinks[i]);
    }
    assertEquals("Active coordinator", 1, activeCount);

    shutdown(groupMgr, msgStage);
  }

  private void nodesJoinLater(int nodes) throws Exception {
    System.out.println("*** Testing " + nodes + " nodes join at later time.");

    final LinkedBlockingQueue<NodeID> joinedNodes = new LinkedBlockingQueue<NodeID>();
    NodeID[] ids = new NodeID[nodes];
    ChangeSink[] sinks = new ChangeSink[nodes];
    TCGroupManagerImpl[] groupMgr = new TCGroupManagerImpl[nodes];
    PortChooser pc = new PortChooser();
    int[] ports = new int[nodes];
    Node[] allNodes = new Node[nodes];
    for (int i = 0; i < nodes; ++i) {
      ports[i] = pc.chooseRandom2Port();
      allNodes[i] = new Node(LOCALHOST, ports[i], ports[i] + 1);
    }

    final StateManager[] managers = new StateManager[nodes];
    ElectionThread[] elections = new ElectionThread[nodes];
    L2StateMessageStage[] msgStage = new L2StateMessageStage[nodes];
    for (int i = 0; i < nodes; ++i) {
      managers[i] = createStateManageNode(i, allNodes, sinks, groupMgr, msgStage);
      elections[i] = new ElectionThread(managers[i]);
    }

    // the first node to be the active one
    System.out.println("*** First node joins to be an active node...");
    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, allNodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    ids[0] = groupMgr[0].join(allNodes[0], nodeStore);
    managers[0].startElection();
    ThreadUtil.reallySleep(100);

    // move following join nodes to passive-standby
    groupMgr[0].registerForGroupEvents(new MyGroupEventListener(groupMgr[0].getLocalNodeID()) {
      @Override
      public void nodeJoined(NodeID nodeID) {
        // save nodeID for moving to passive
        joinedNodes.add(nodeID);
      }
    });

    System.out.println("***  Remaining nodes join");
    nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, allNodes);
    nodeStore = new NodesStoreImpl(nodeSet);
    for (int i = 1; i < nodes; ++i) {
      ids[i] = groupMgr[i].join(allNodes[i], nodeStore);
    }

    ThreadUtil.reallySleep(1000);
    int nodesNeedToMoveToPassive = nodes - 1;
    while (nodesNeedToMoveToPassive > 0) {
      NodeID toBePassiveNode = joinedNodes.take();
      System.out.println("*** moveNodeToPassiveStandby -> " + toBePassiveNode);
      managers[0].moveNodeToPassiveStandby(toBePassiveNode);
      --nodesNeedToMoveToPassive;
    }
    assertTrue(nodesNeedToMoveToPassive == 0);

    ThreadUtil.reallySleep(1000 * nodes);
    // verification: first node must be active
    int activeCount = 0;
    for (int i = 0; i < nodes; ++i) {
      boolean active = managers[i].isActiveCoordinator();
      if (active) ++activeCount;
      System.out.println("*** Server[" + i + "] state is " + sinks[i]);
    }
    assertEquals("Active coordinator", 1, activeCount);
    assertTrue("Node-0 must be active coordinator", managers[0].isActiveCoordinator());

    // check API
    try {
      // active is supported not to move itself to passive stand-by
      managers[0].moveNodeToPassiveStandby(ids[0]);
      throw new RuntimeException("moveNodeToPassiveStandy expected to trows an expection");
    } catch (Exception x) {
      // expected
    }

    System.out.println("*** Stop active and re-elect");
    // stop active node
    shutdown(groupMgr, msgStage, 0, 1);

    ElectionIfNecessaryThread reElectThreads[] = new ElectionIfNecessaryThread[nodes];
    for (int i = 1; i < nodes; ++i) {
      reElectThreads[i] = new ElectionIfNecessaryThread(managers[i], ids[0]);
    }
    for (int i = 1; i < nodes; ++i) {
      reElectThreads[i].start();
    }
    for (int i = 1; i < nodes; ++i) {
      reElectThreads[i].join();
    }
    ThreadUtil.reallySleep(1000);

    // verify
    activeCount = 0;
    for (int i = 1; i < nodes; ++i) {
      boolean active = managers[i].isActiveCoordinator();
      if (active) ++activeCount;
      System.out.println("*** Server[" + i + "] (" + (active ? "active" : "non-active") + ")state is " + sinks[i]);
    }
    assertEquals("Active coordinator", 1, activeCount);

    // shut them down
    shutdown(groupMgr, msgStage, 1, nodes);
  }

  private StateManager createStateManageNode(int localIndex, Node[] nodes, ChangeSink[] sinks,
                                             TCGroupManagerImpl[] groupMgr, L2StateMessageStage[] messageStage)
      throws Exception {
    StageManager stageManager = new StageManagerImpl(threadGroup, new QueueFactory());
    TCGroupManagerImpl gm = new TCGroupManagerImpl(new NullConnectionPolicy(), nodes[localIndex].getHost(),
                                                   nodes[localIndex].getPort(), nodes[localIndex].getGroupPort(),
                                                   stageManager, null);
    ConfigurationContext context = new ConfigurationContextImpl(stageManager);
    stageManager.startAll(context, Collections.EMPTY_LIST);
    gm.setDiscover(new TCGroupMemberDiscoveryStatic(gm));

    groupMgr[localIndex] = gm;
    MyGroupEventListener gel = new MyGroupEventListener(gm.getLocalNodeID());
    gm.registerForGroupEvents(gel);
    sinks[localIndex] = new ChangeSink(localIndex);
    MyStateManagerConfig config = new MyStateManagerConfig();
    config.electionTime = 5;
    StateManager mgr = new StateManagerImpl(logger, gm, sinks[localIndex], config, WeightGeneratorFactory
        .createDefaultFactory(), new TestClusterStatePersistor(), new SBPResolverImpl());
    messageStage[localIndex] = new L2StateMessageStage(mgr);
    gm.routeMessages(L2StateMessage.class, messageStage[localIndex].getSink());
    messageStage[localIndex].start();
    return (mgr);
  }

  private static class L2StateMessageStage extends Thread {
    private final MockSink               sink;
    private final NoExceptionLinkedQueue processQ = new NoExceptionLinkedQueue();
    private final StateManager           mgr;
    private volatile boolean             stop     = false;

    public L2StateMessageStage(StateManager mgr) {
      this.mgr = mgr;
      this.sink = new MockSink() {
        @Override
        public void add(EventContext ec) {
          processQ.put(ec);
        }
      };
      setDaemon(true);
      setName("L2StateMessageStageThread");
    }

    public synchronized void requestStop() {
      stop = true;
    }

    public synchronized boolean isStopped() {
      return stop;
    }

    public Sink getSink() {
      return sink;
    }

    @Override
    public void run() {
      while (!isStopped()) {
        L2StateMessage m = (L2StateMessage) processQ.poll(3000);
        if (m != null) {
          mgr.handleClusterStateMessage(m);
        }
      }
    }
  }

  private static class ElectionThread extends Thread {
    private StateManager mgr;

    public ElectionThread(StateManager mgr) {
      setMgr(mgr);
    }

    public void setMgr(StateManager mgr) {
      this.mgr = mgr;
    }

    @Override
    public void run() {
      mgr.startElection();
    }
  }

  private static class MyStateManagerConfig implements StateManagerConfig {
    public int electionTime;

    @Override
    public int getElectionTimeInSecs() {
      return electionTime;
    }
  }

  private static class ElectionIfNecessaryThread extends Thread {
    private final StateManager mgr;
    private final NodeID       disconnectedNode;

    public ElectionIfNecessaryThread(StateManager mgr, NodeID disconnectedNode) {
      this.mgr = mgr;
      this.disconnectedNode = disconnectedNode;
    }

    @Override
    public void run() {
      mgr.startElectionIfNecessary(disconnectedNode);
    }
  }

  private static class ChangeSink extends MockSink {
    private final int         serverIndex;
    private StateChangedEvent event = null;

    public ChangeSink(int index) {
      serverIndex = index;
    }

    @Override
    public void add(EventContext context) {
      event = (StateChangedEvent) context;
      System.out.println("*** Server[" + serverIndex + "]: " + event);
    }

    public State getState() {
      if (event == null) return null;
      return event.getCurrentState();
    }

    @Override
    public String toString() {
      State st = getState();
      return ((st != null) ? st.toString() : "<state unknown>");
    }

  }

  private static class MyGroupEventListener implements GroupEventsListener {

    private final NodeID gmNodeID;

    public MyGroupEventListener(NodeID nodeID) {
      this.gmNodeID = nodeID;
    }

    @Override
    public void nodeJoined(NodeID nodeID) {
      System.err.println("\n### " + gmNodeID + ": nodeJoined -> " + nodeID);
    }

    @Override
    public void nodeLeft(NodeID nodeID) {
      System.err.println("\n### " + gmNodeID + ": nodeLeft -> " + nodeID);
    }

  }

}
