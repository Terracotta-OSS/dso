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

import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.StageManager;
import com.tc.async.impl.ConfigurationContextImpl;
import com.tc.async.impl.StageManagerImpl;
import com.tc.bytes.TCByteBuffer;
import com.tc.config.NodesStore;
import com.tc.config.NodesStoreImpl;
import com.tc.exception.TCShutdownServerException;
import com.tc.io.TCByteBufferInput;
import com.tc.io.TCByteBufferOutput;
import com.tc.l2.ha.WeightGeneratorFactory;
import com.tc.l2.msg.GCResultMessage;
import com.tc.l2.msg.L2StateMessage;
import com.tc.l2.msg.ObjectSyncMessage;
import com.tc.l2.state.Enrollment;
import com.tc.lang.TCThreadGroup;
import com.tc.lang.ThrowableHandler;
import com.tc.lang.ThrowableHandlerImpl;
import com.tc.logging.TCLogging;
import com.tc.net.NodeID;
import com.tc.net.ServerID;
import com.tc.net.core.security.TCSecurityManager;
import com.tc.net.protocol.tcm.ChannelEvent;
import com.tc.net.protocol.tcm.ChannelEventListener;
import com.tc.net.protocol.tcm.MessageChannel;
import com.tc.net.protocol.tcm.TCMessageType;
import com.tc.net.protocol.transport.NullConnectionPolicy;
import com.tc.net.proxy.TCPProxy;
import com.tc.object.ObjectID;
import com.tc.object.dna.impl.ObjectStringSerializer;
import com.tc.object.dna.impl.ObjectStringSerializerImpl;
import com.tc.object.tx.ServerTransactionID;
import com.tc.object.tx.TransactionID;
import com.tc.objectserver.dgc.api.GarbageCollectionInfo;
import com.tc.properties.TCPropertiesImpl;
import com.tc.test.TCTestCase;
import com.tc.util.BitSetObjectIDSet;
import com.tc.util.ObjectIDSet;
import com.tc.util.PortChooser;
import com.tc.util.TCCollections;
import com.tc.util.UUID;
import com.tc.util.concurrent.NoExceptionLinkedQueue;
import com.tc.util.concurrent.QueueFactory;
import com.tc.util.concurrent.ThreadUtil;
import com.tc.util.runtime.ThreadDump;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.Answers.RETURNS_MOCKS;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

public class TCGroupManagerImplTest extends TCTestCase {

  private final static String      LOCALHOST      = "localhost";

  private int                      ports[];
  private int                      groupPorts[];
  private TCGroupManagerImpl       groups[];
  private TestGroupMessageListener listeners[];
  private TestGroupEventListener   groupEventListeners[];
  private Node                     nodes[];
  private ThrowableHandler         throwableHandler;
  private AtomicReference<Throwable> error;

  private void setupGroups(int n) throws Exception {
    ports = new int[n];
    groupPorts = new int[n];
    groups = new TCGroupManagerImpl[n];
    listeners = new TestGroupMessageListener[n];
    groupEventListeners = new TestGroupEventListener[n];
    nodes = new Node[n];
    error = new AtomicReference<Throwable>();
    throwableHandler = new ThrowableHandlerImpl(TCLogging.getLogger(getClass())) {
      @Override
      public void handleThrowable(final Thread thread, final Throwable t) {
        if (error.compareAndSet(null, t)) {
          t.printStackTrace();
        }
      }
    };

    PortChooser pc = new PortChooser();
    for (int i = 0; i < n; ++i) {
      ports[i] = pc.chooseRandom2Port();
      groupPorts[i] = ports[i] + 1;
      nodes[i] = new Node(LOCALHOST, ports[i], groupPorts[i]);
    }
    for (int i = 0; i < n; ++i) {
      StageManager stageManager = new StageManagerImpl(new TCThreadGroup(throwableHandler),
                                                       new QueueFactory());
      groups[i] = new TCGroupManagerImpl(new NullConnectionPolicy(), LOCALHOST, ports[i], groupPorts[i], stageManager, null);
      ConfigurationContext context = new ConfigurationContextImpl(stageManager);
      stageManager.startAll(context, Collections.EMPTY_LIST);
      groups[i].setDiscover(new TCGroupMemberDiscoveryStatic(groups[i]));
      groupEventListeners[i] = new TestGroupEventListener(groups[i]);
      groups[i].registerForGroupEvents(groupEventListeners[i]);
      System.out.println("Starting " + groups[i]);
      listeners[i] = new TestGroupMessageListener(2000);
    }
  }

  private void tearGroups() throws Exception {
    for (TCGroupManagerImpl group : groups) {
      System.out.println("Shutting down " + group);
      group.shutdown();
    }
    ThreadUtil.reallySleep(200);
    throwExceptionIfNecessary();
  }

  private void throwExceptionIfNecessary() {
    Throwable t = error.get();
    if (t != null) {
      // Delete this block when ENG-418 is fixed
      if (t instanceof AssertionError && t.getCause() instanceof InterruptedException) {
        System.out.println("Ignoring exception until ENG-418 is fixed: ");
        t.printStackTrace();
        return;
      }
      throw new RuntimeException(t);
    }
  }

  public void testBasicChannelOpenClose() throws Exception {
    setupGroups(2);

    groups[0].setDiscover(new NullTCGroupMemberDiscovery());
    groups[1].setDiscover(new NullTCGroupMemberDiscovery());
    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, nodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    groups[0].join(nodes[0], nodeStore);
    groups[1].join(nodes[1], nodeStore);
    // open test
    groups[0].openChannel(LOCALHOST, groupPorts[1], new NullChannelEventListener());

    Thread.sleep(2000);

    assertEquals(1, groups[0].size());
    assertEquals(1, groups[1].size());
    TCGroupMember member1 = getMember(groups[0], 0);
    TCGroupMember member2 = getMember(groups[1], 0);
    assertTrue("Expected  " + member1.getLocalNodeID() + " but got " + member2.getPeerNodeID(), member1
        .getLocalNodeID().equals(member2.getPeerNodeID()));
    assertTrue("Expected  " + member1.getPeerNodeID() + " but got " + member2.getLocalNodeID(), member1.getPeerNodeID()
        .equals(member2.getLocalNodeID()));

    // close test
    member1.getChannel().close();

    while ((groups[0].size() != 0) || (groups[1].size() != 0)) {
      System.err.println("XXX " + member1 + "; size: " + groups[0].size());
      System.err.println("XXX " + member2 + "; size: " + groups[1].size());
      ThreadUtil.reallySleep(5000);
    }

    tearGroups();
  }

  public void testCallbackPort() throws Exception {
    setupGroups(2);

    int proxyPort = new PortChooser().chooseRandomPort();
    TCPProxy proxy = new TCPProxy(proxyPort, InetAddress.getByName(LOCALHOST), groupPorts[1], 0, false, null);
    proxy.start();

    groups[0].setDiscover(new NullTCGroupMemberDiscovery());
    groups[1].setDiscover(new NullTCGroupMemberDiscovery());
    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, nodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    groups[0].join(nodes[0], nodeStore);
    groups[1].join(nodes[1], nodeStore);
    // open test
    groups[0].openChannel(LOCALHOST, proxyPort, new NullChannelEventListener());

    Thread.sleep(2000);

    assertEquals(1, groups[0].size());
    assertEquals(1, groups[1].size());
    TCGroupMember member1 = getMember(groups[0], 0);
    TCGroupMember member2 = getMember(groups[1], 0);
    assertTrue("Expected  " + member1.getLocalNodeID() + " but got " + member2.getPeerNodeID(), member1
        .getLocalNodeID().equals(member2.getPeerNodeID()));
    assertTrue("Expected  " + member1.getPeerNodeID() + " but got " + member2.getLocalNodeID(), member1.getPeerNodeID()
        .equals(member2.getLocalNodeID()));

    proxy.setDelay(Integer.MAX_VALUE);

    long minTimeToSayDead = TCPropertiesImpl.getProperties().getLong("l2.healthcheck.l1.ping.idletime")
                            + (TCPropertiesImpl.getProperties().getLong("l2.healthcheck.l1.ping.interval") * TCPropertiesImpl
                                .getProperties().getLong("l2.healthcheck.l1.ping.probes"));
    // giving more buffer time, to catch problem if any
    minTimeToSayDead += (3 * TCPropertiesImpl.getProperties().getLong("l2.healthcheck.l1.ping.interval"));

    // HC will not say the other end is DEAD as the callback port from the client TCGroupMgr is available
    System.out.println("Sleeping for min time " + minTimeToSayDead);
    ThreadUtil.reallySleep(minTimeToSayDead);
    System.out.println("checking the client state");
    assertEquals(1, groups[1].size());

    // eventually after full probe, the node has to goto DEAD state
    while (groups[1].size() != 0) {
      System.out.println(".");
      ThreadUtil.reallySleep(TCPropertiesImpl.getProperties().getLong("l2.healthcheck.l1.ping.interval")
                             * TCPropertiesImpl.getProperties().getLong("l2.healthcheck.l1.ping.probes"));
    }
    tearGroups();
  }

  public void testOpenZappedNode() throws Exception {
    setupGroups(2);

    groups[0].addZappedNode(groups[1].getLocalNodeID());

    MockZapNodeRequestProcessor proc1 = new MockZapNodeRequestProcessor();
    MockZapNodeRequestProcessor proc2 = new MockZapNodeRequestProcessor();

    groups[0].setZapNodeRequestProcessor(proc1);
    groups[1].setZapNodeRequestProcessor(proc2);

    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, nodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    groups[0].join(nodes[0], nodeStore);
    groups[1].join(nodes[1], nodeStore);

    int proc1OutGoingZapMsg = 0, proc1IncomingZapMsg = 0;
    int proc2OutGoingZapMsg = 0, proc2IncomingZapMsg = 0;

    while (proc1.outgoing.poll(5000) != null) {
      proc1OutGoingZapMsg++;
    }

    while (proc1.incoming.poll(5000) != null) {
      proc1IncomingZapMsg++;
    }

    while (proc2.outgoing.poll(5000) != null) {
      proc2OutGoingZapMsg++;
    }

    while (proc2.incoming.poll(5000) != null) {
      proc2IncomingZapMsg++;
    }

    assertEquals(1, proc1OutGoingZapMsg);
    assertEquals(0, proc1IncomingZapMsg);

    assertEquals(0, proc2OutGoingZapMsg);
    assertEquals(1, proc2IncomingZapMsg);

    tearGroups();
  }

  /*
   * Both open channel to each other, only one direction to keep
   */
  public void testResolveTwoWayConnection() throws Exception {
    setupGroups(2);

    groups[0].setDiscover(new NullTCGroupMemberDiscovery());
    groups[1].setDiscover(new NullTCGroupMemberDiscovery());

    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, nodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);

    groups[0].join(nodes[0], nodeStore);
    groups[1].join(nodes[1], nodeStore);

    groups[0].openChannel(LOCALHOST, groupPorts[1], new NullChannelEventListener());
    groups[1].openChannel(LOCALHOST, groupPorts[0], new NullChannelEventListener());

    // wait one channel to be closed.
    Thread.sleep(2000);

    assertEquals(1, groups[0].size());
    assertEquals(1, groups[1].size());
    TCGroupMember m0 = getMember(groups[0], 0);
    TCGroupMember m1 = getMember(groups[1], 0);
    assertTrue("Expected  " + m0.getLocalNodeID() + " but got " + m1.getPeerNodeID(),
               m0.getLocalNodeID().equals(m1.getPeerNodeID()));
    assertTrue("Expected  " + m0.getPeerNodeID() + " but got " + m1.getLocalNodeID(),
               m0.getPeerNodeID().equals(m1.getLocalNodeID()));

    tearGroups();
  }

  public void testSendTo() throws Exception {
    setupGroups(2);

    TestGroupMessageListener listener1 = new TestGroupMessageListener(2000);
    TestGroupMessageListener listener2 = new TestGroupMessageListener(2000);
    groups[0].registerForMessages(ObjectSyncMessage.class, listener1);
    groups[1].registerForMessages(ObjectSyncMessage.class, listener2);

    groups[0].setDiscover(new NullTCGroupMemberDiscovery());
    groups[1].setDiscover(new NullTCGroupMemberDiscovery());

    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, nodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    groups[0].join(nodes[0], nodeStore);
    groups[1].join(nodes[1], nodeStore);

    groups[0].openChannel(LOCALHOST, groupPorts[1], new NullChannelEventListener());
    Thread.sleep(1000);
    TCGroupMember member0 = getMember(groups[0], 0);
    TCGroupMember member1 = getMember(groups[1], 0);

    ObjectSyncMessage sMesg = createTestObjectSyncMessage();
    groups[0].sendTo(member0.getPeerNodeID(), sMesg);
    ObjectSyncMessage rMesg = (ObjectSyncMessage) listener2.getNextMessageFrom(groups[0].getLocalNodeID());
    assertTrue(cmpObjectSyncMessage(sMesg, rMesg));

    sMesg = createTestObjectSyncMessage();
    groups[1].sendTo(member1.getPeerNodeID(), sMesg);
    rMesg = (ObjectSyncMessage) listener1.getNextMessageFrom(groups[1].getLocalNodeID());
    assertTrue(cmpObjectSyncMessage(sMesg, rMesg));

    tearGroups();
  }

  private ObjectSyncMessage createTestObjectSyncMessage() {
    ObjectIDSet dnaOids = new BitSetObjectIDSet();
    for (long i = 1; i <= 100; ++i) {
      dnaOids.add(new ObjectID(i));
    }
    int count = 10;
    TCByteBuffer[] serializedDNAs = new TCByteBuffer[] {};
    ObjectStringSerializer objectSerializer = new ObjectStringSerializerImpl();
    Map roots = new HashMap();
    long sID = 10;
    ObjectSyncMessage message = new ObjectSyncMessage(new ServerTransactionID(new ServerID("hello", new byte[] { 34, 33, (byte) 234 }),
                                               new TransactionID(342)), dnaOids, count, serializedDNAs,
                       objectSerializer, roots, sID, TCCollections.EMPTY_OBJECT_ID_SET);
    return (message);
  }

  private boolean cmpObjectSyncMessage(ObjectSyncMessage o1, ObjectSyncMessage o2) {
    return ((o1.getDnaCount() == o2.getDnaCount()) && o1.getOids().equals(o2.getOids())
            && o1.getRootsMap().equals(o2.getRootsMap()) && (o1.getType() == o2.getType())
            && o1.getMessageID().equals(o2.getMessageID()) && o1.getServerTransactionID()
        .equals(o2.getServerTransactionID()));
  }

  private TCGroupMember getMember(TCGroupManagerImpl mgr, int idx) {
    return new ArrayList<TCGroupMember>(mgr.getMembers()).get(idx);
  }

  public void testJoin() throws Exception {
    int nGrp = 2;
    setupGroups(nGrp);

    groups[0].registerForMessages(ObjectSyncMessage.class, listeners[0]);
    groups[1].registerForMessages(ObjectSyncMessage.class, listeners[1]);

    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, nodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    groups[0].join(nodes[0], nodeStore);
    groups[1].join(nodes[1], nodeStore);
    waitForMembersToJoin();

    GroupMessage sMesg = createTestObjectSyncMessage();
    TCGroupMember member = getMember(groups[0], 0);
    groups[0].sendTo(member.getPeerNodeID(), sMesg);
    GroupMessage rMesg = listeners[1].getNextMessageFrom(groups[0].getLocalNodeID());
    assertTrue(cmpObjectSyncMessage((ObjectSyncMessage) sMesg, (ObjectSyncMessage) rMesg));

    sMesg = createTestObjectSyncMessage();
    member = getMember(groups[1], 0);
    groups[1].sendTo(member.getPeerNodeID(), sMesg);
    rMesg = listeners[0].getNextMessageFrom(groups[1].getLocalNodeID());
    assertTrue(cmpObjectSyncMessage((ObjectSyncMessage) sMesg, (ObjectSyncMessage) rMesg));

    tearGroups();
  }

  private GCResultMessage createGCResultMessage() {
    ObjectIDSet oidSet = new BitSetObjectIDSet();
    for (long i = 1; i <= 100; ++i) {
      oidSet.add(new ObjectID(i));
    }
    GCResultMessage message = new GCResultMessage(new GarbageCollectionInfo(), oidSet);
    return (message);
  }

  private boolean cmpGCResultMessage(GCResultMessage o1, GCResultMessage o2) {
    return (o1.getType() == o2.getType() && o1.getMessageID().equals(o2.getMessageID())
            && o1.getGCedObjectIDs().equals(o2.getGCedObjectIDs()) && o1.getGCInfo().getGarbageCollectionID().toLong() == o2
        .getGCInfo().getGarbageCollectionID().toLong());
  }

  public void testSendToAll() throws Exception {
    int nGrp = 5;
    setupGroups(nGrp);
    HashMap<NodeID, TestGroupMessageListener> listenerMap = new HashMap<NodeID, TestGroupMessageListener>();

    for (int i = 0; i < nGrp; ++i) {
      groups[i].registerForMessages(GCResultMessage.class, listeners[i]);
      listenerMap.put(groups[i].getLocalNodeID(), listeners[i]);
    }
    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, nodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    for (int i = 0; i < nGrp; ++i) {
      groups[i].join(nodes[i], nodeStore);
    }
    waitForMembersToJoin();

    // test with one to one first
    GroupMessage sMesg = createGCResultMessage();
    TCGroupMember member = getMember(groups[0], 0);
    groups[0].sendTo(member.getPeerNodeID(), sMesg);
    TestGroupMessageListener listener = listenerMap.get(member.getPeerNodeID());
    GroupMessage rMesg = listener.getNextMessageFrom(groups[0].getLocalNodeID());
    assertTrue(cmpGCResultMessage((GCResultMessage) sMesg, (GCResultMessage) rMesg));

    sMesg = createGCResultMessage();
    member = getMember(groups[1], 0);
    groups[1].sendTo(member.getPeerNodeID(), sMesg);
    listener = listenerMap.get(member.getPeerNodeID());
    rMesg = listener.getNextMessageFrom(groups[1].getLocalNodeID());
    assertTrue(cmpGCResultMessage((GCResultMessage) sMesg, (GCResultMessage) rMesg));

    // test with broadcast
    sMesg = createGCResultMessage();
    groups[0].sendAll(sMesg);
    for (int i = 0; i < groups[0].size(); ++i) {
      TCGroupMember m = getMember(groups[0], i);
      TestGroupMessageListener l = listenerMap.get(m.getPeerNodeID());
      rMesg = l.getNextMessageFrom(groups[0].getLocalNodeID());
      assertTrue(cmpGCResultMessage((GCResultMessage) sMesg, (GCResultMessage) rMesg));
    }

    ThreadUtil.reallySleep(200);
    tearGroups();
  }

  private L2StateMessage createL2StateMessage() {
    long weights[] = new long[] { 1, 23, 44, 78 };
    Enrollment enroll = new Enrollment(new ServerID("test", UUID.getUUID().toString().getBytes()), true, weights);
    L2StateMessage message = new L2StateMessage(L2StateMessage.START_ELECTION, enroll);
    return (message);
  }

  private boolean cmpL2StateMessage(L2StateMessage o1, L2StateMessage o2) {
    return (o1.getEnrollment().equals(o2.getEnrollment()) && (o1.getType() == o2.getType()) && o1.getMessageID()
        .equals(o2.getMessageID()));
  }

  public void testSendToAndWait() throws Exception {
    int nGrp = 5;
    setupGroups(nGrp);
    HashMap<NodeID, TestGroupMessageListener> listenerMap = new HashMap<NodeID, TestGroupMessageListener>();

    for (int i = 0; i < nGrp; ++i) {
      listeners[i] = new ResponseL2StateMessageListener(groups[i], 1000);
      groups[i].registerForMessages(L2StateMessage.class, listeners[i]);
      listenerMap.put(groups[i].getLocalNodeID(), listeners[i]);
    }

    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, nodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    for (int i = 0; i < nGrp; ++i) {
      groups[i].join(nodes[i], nodeStore);
    }
    waitForMembersToJoin();

    for (int i = 0; i < groups[0].getMembers().size(); ++i) {
      GroupMessage sMesg = createL2StateMessage();
      TCGroupMember member = getMember(groups[0], i);
      groups[0].sendToAndWaitForResponse(member.getPeerNodeID(), sMesg);
      TestGroupMessageListener listener = listenerMap.get(member.getPeerNodeID());
      GroupMessage rMesg = listener.getNextMessageFrom(groups[0].getLocalNodeID());
      assertTrue(cmpL2StateMessage((L2StateMessage) sMesg, (L2StateMessage) rMesg));

      sMesg = createL2StateMessage();
      member = getMember(groups[1], i);
      groups[1].sendToAndWaitForResponse(member.getPeerNodeID(), sMesg);
      listener = listenerMap.get(member.getPeerNodeID());
      rMesg = listener.getNextMessageFrom(groups[1].getLocalNodeID());
      assertTrue(cmpL2StateMessage((L2StateMessage) sMesg, (L2StateMessage) rMesg));
    }

    ThreadUtil.reallySleep(200);
    tearGroups();
  }

  public void testSendAllAndWait() throws Exception {
    final int nGrp = 5;
    setupGroups(nGrp);
    HashMap<NodeID, TestGroupMessageListener> listenerMap = new HashMap<NodeID, TestGroupMessageListener>();

    for (int i = 0; i < nGrp; ++i) {
      listeners[i] = new ResponseL2StateMessageListener(groups[i], 1000);
      groups[i].registerForMessages(L2StateMessage.class, listeners[i]);
      listenerMap.put(groups[i].getLocalNodeID(), listeners[i]);
    }
    for (int i = 0; i < nGrp; ++i) {
      Set<Node> nodeSet = new HashSet<Node>();
      Collections.addAll(nodeSet, nodes);
      NodesStore nodeStore = new NodesStoreImpl(nodeSet);
      groups[i].join(nodes[i], nodeStore);
    }

    waitForMembersToJoin();

    for (int m = 0; m < nGrp; ++m) {
      TCGroupManagerImpl ms = groups[m];
      GroupMessage sMesg = createL2StateMessage();
      ms.sendAllAndWaitForResponse(sMesg);
      for (int i = 0; i < ms.getMembers().size(); ++i) {
        TCGroupMember member = getMember(ms, i);
        TestGroupMessageListener listener = listenerMap.get(member.getPeerNodeID());
        GroupMessage rMesg = listener.getNextMessageFrom(ms.getLocalNodeID());
        assertTrue(cmpL2StateMessage((L2StateMessage) sMesg, (L2StateMessage) rMesg));
      }
    }

    ThreadUtil.reallySleep(200);
    tearGroups();
  }

  public void testZapNode() throws Exception {
    int nGrp = 2;
    MyGroupEventListener eventListeners[] = new MyGroupEventListener[nGrp];
    MyZapNodeRequestProcessor zaps[] = new MyZapNodeRequestProcessor[nGrp];
    NodeID nodeIDs[] = new NodeID[nGrp];
    setupGroups(nGrp);
    HashMap<NodeID, TestGroupMessageListener> listenerMap = new HashMap<NodeID, TestGroupMessageListener>();

    for (int i = 0; i < nGrp; ++i) {
      eventListeners[i] = new MyGroupEventListener();
      groups[i].registerForGroupEvents(eventListeners[i]);
      zaps[i] = new MyZapNodeRequestProcessor();
      groups[i].setZapNodeRequestProcessor(zaps[i]);
      groups[i].registerForMessages(TestMessage.class, listeners[i]);
      listenerMap.put(groups[i].getLocalNodeID(), listeners[i]);
    }

    Set<Node> nodeSet = new HashSet<Node>();
    Collections.addAll(nodeSet, nodes);
    NodesStore nodeStore = new NodesStoreImpl(nodeSet);
    for (int i = 0; i < nGrp; ++i) {
      nodeIDs[i] = groups[i].join(nodes[i], nodeStore);
    }
    waitForMembersToJoin();

    System.err.println("ZAPPING NODE : " + nodeIDs[1]);
    groups[0].zapNode(nodeIDs[1], 01, "test : Zap the other node " + nodeIDs[1] + " from " + nodeIDs[0]);

    Set<Object> zaps1 = new HashSet<Object>();
    Set<Object> zaps2 = new HashSet<Object>();

    zaps1.add(zaps[0].outgoing.take());
    zaps2.add(zaps[1].incoming.take());

    zaps1.add(zaps[0].outgoing.poll(500));
    zaps2.add(zaps[1].incoming.poll(500));

    // The limit is 2 zaps given the above case, make sure there's no 3rd zap going on.
    assertNull(zaps[0].outgoing.poll(500));
    assertNull(zaps[1].incoming.poll(500));

    assertEquals(zaps1, zaps2);

    tearGroups();
  }

  public void testVersionCompatibilityCheck() throws Exception {
    PortChooser portChooser = new PortChooser();
    TCGroupManagerImpl tcGroupManager = spy(new TCGroupManagerImpl(new NullConnectionPolicy(), "localhost",
        portChooser.chooseRandomPort(), portChooser.chooseRandomPort(),
        mock(StageManager.class, withSettings().defaultAnswer(RETURNS_MOCKS)), mock(TCSecurityManager.class)) {
      @Override
      protected void initializeWeights(final WeightGeneratorFactory weightGeneratorFactory) {
        weightGeneratorFactory.add(new WeightGeneratorFactory.WeightGenerator() {
          @Override
          public long getWeight() {
            return 5;
          }
        });
        weightGeneratorFactory.add(new WeightGeneratorFactory.WeightGenerator() {
          @Override
          public long getWeight() {
            return 6;
          }
        });
      }

      @Override
      protected String getVersion() {
        return "4.2.0";
      }
    });
    TCGroupMemberDiscovery discovery = mock(TCGroupMemberDiscovery.class);
    when(discovery.isValidClusterNode(any(NodeID.class))).thenReturn(true);
    tcGroupManager.setDiscover(discovery);

    // Incompatible version, higher weights. Close down the handshake, the other guy will zap himself.
    MessageChannel channel1 = mockMessageChannel();
    tcGroupManager.receivedHandshake(mockHandshakeMessage(channel1, "4.3.1", new long [] { 4, 5 }));
    verify(channel1).close();

    // Incompatible version, lower weights. Zap yourself.
    MessageChannel channel2 = mockMessageChannel();
    try {
      tcGroupManager.receivedHandshake(mockHandshakeMessage(channel2, "4.3.1", new long [] { 6, 1 }));
      fail("Should have zapped here due to low weights");
    } catch (TCShutdownServerException e) {
      // expected
      verify(channel2).close();
    }

    // Compatible version, everything should be fine
    MessageChannel channel3 = mockMessageChannel();
    tcGroupManager.receivedHandshake(mockHandshakeMessage(channel3, "4.2.1", new long[] { Long.MAX_VALUE, Long.MAX_VALUE}));
    verify(channel3, never()).close();
  }

  private TCGroupHandshakeMessage mockHandshakeMessage(MessageChannel messageChannel, String version, long[] weights) {
    TCGroupHandshakeMessage tcGroupHandshakeMessage = mock(TCGroupHandshakeMessage.class);
    when(tcGroupHandshakeMessage.getNodeID()).thenReturn(new ServerID("test", new byte[20]));
    when(tcGroupHandshakeMessage.getVersion()).thenReturn(version);
    when(tcGroupHandshakeMessage.getWeights()).thenReturn(weights);
    when(tcGroupHandshakeMessage.getChannel()).thenReturn(messageChannel);
    return tcGroupHandshakeMessage;
  }

  private MessageChannel mockMessageChannel() {
    final TCGroupHandshakeMessage tcGroupHandshakeMessage = mock(TCGroupHandshakeMessage.class, withSettings().defaultAnswer(RETURNS_MOCKS));
    MessageChannel channel = mock(MessageChannel.class);
    when(channel.getAttachment(anyString())).thenReturn(null);
    when(channel.createMessage(TCMessageType.GROUP_HANDSHAKE_MESSAGE)).thenReturn(tcGroupHandshakeMessage);
    when(tcGroupHandshakeMessage.getChannel()).thenReturn(channel);
    return channel;
  }


  private void waitForMembersToJoin() throws Exception {
    int members = groups.length - 1;
    for (TestGroupEventListener testGroupEventListener : groupEventListeners) {
      testGroupEventListener.waitForJoinedCount(members);
    }
  }

  private void checkMessagesOrdering(final TCGroupManagerImpl mgr1, final TestGroupMessageListener l1,
                                     final TCGroupManagerImpl mgr2, final TestGroupMessageListener l2)
      throws GroupException {

    final Integer upbound = Integer.valueOf(50);

    // setup throwable ThreadGroup to catch AssertError from threads.
    TCThreadGroup threadGroup = new TCThreadGroup(new ThrowableHandlerImpl(null), "TCGroupManagerImplTestGroup");
    ThreadUtil.reallySleep(1000);

    Thread t1 = new SenderThread(threadGroup, "Node-0", mgr1, upbound);
    Thread t2 = new SenderThread(threadGroup, "Node-1", mgr2, upbound);
    Thread vt1 = new ReceiverThread(threadGroup, "Node-0", l1, upbound, mgr2.getLocalNodeID());
    Thread vt2 = new ReceiverThread(threadGroup, "Node-1", l2, upbound, mgr1.getLocalNodeID());

    System.err.println("*** Start sending ordered messages....");
    t1.start();
    t2.start();
    vt1.start();
    vt2.start();

    try {
      t1.join();
      t2.join();
      vt1.join();
      vt2.join();
    } catch (InterruptedException x) {
      throw new GroupException("Join interrupted:" + x);
    }
    System.err.println("*** Done with messages ordering test");

  }

  private static final class SenderThread extends Thread {
    private final TCGroupManagerImpl mgr;
    private final Integer            upbound;
    private Integer                  index = Integer.valueOf(0);
    private final NodeID             toNode;

    public SenderThread(ThreadGroup group, String name, TCGroupManagerImpl mgr, Integer upbound) {
      this(group, name, mgr, upbound, ServerID.NULL_ID);
    }

    public SenderThread(ThreadGroup group, String name, TCGroupManagerImpl mgr, Integer upbound, NodeID toNode) {
      super(group, name);
      this.mgr = mgr;
      this.upbound = upbound;
      this.toNode = toNode;
    }

    @Override
    public void run() {
      while (index <= upbound) {
        TestMessage msg = new TestMessage(index.toString());
        if (index % 10 == 0) System.err.println("*** " + getName() + " sends " + index);
        try {
          if (toNode.isNull()) {
            mgr.sendAll(msg);
          } else {
            mgr.sendTo(toNode, msg);
          }
        } catch (Exception x) {
          System.err.println("Got exception : " + getName() + " " + x.getMessage());
          x.printStackTrace();
          throw new RuntimeException("sendAll GroupException:" + x);
        }
        ++index;
      }
    }
  }

  private static final class ReceiverThread extends Thread {
    private final TestGroupMessageListener l;
    private final Integer                  upbound;
    private Integer                        index = Integer.valueOf(0);
    private final NodeID                   fromNode;

    public ReceiverThread(ThreadGroup group, String name, TestGroupMessageListener l, Integer upbound, NodeID fromNode) {
      super(group, name);
      this.l = l;
      this.upbound = upbound;
      this.fromNode = fromNode;
    }

    @Override
    public void run() {
      while (index <= upbound) {
        TestMessage msg;
        try {
          msg = (TestMessage) l.getNextMessageFrom(fromNode);
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
        if (index % 10 == 0) System.err.println("*** " + getName() + " receives " + msg);
        assertEquals(new TestMessage(index.toString()), msg);
        index++;
      }
    }

  }

  public void testMessagesOrdering() throws Exception {

    int nGrp = 2;
    NodeID nodeIDs[] = new NodeID[nGrp];
    setupGroups(nGrp);

    for (int i = 0; i < nGrp; ++i) {
      groups[i].registerForMessages(TestMessage.class, listeners[i]);
    }
    for (int i = 0; i < nGrp; ++i) {
      Set<Node> nodeSet = new HashSet<Node>();
      Collections.addAll(nodeSet, nodes);
      NodesStore nodeStore = new NodesStoreImpl(nodeSet);
      nodeIDs[i] = groups[i].join(nodes[i], nodeStore);
    }
    waitForMembersToJoin();

    try {
      checkMessagesOrdering(groups[0], listeners[0], groups[1], listeners[1]);
    } catch (Exception e) {
      System.out.println("***** message order check failed: " + e.getStackTrace());
      ThreadDump.dumpThreadsMany(3, 500);
      throw e;
    }

    tearGroups();
  }

  private static class MessagePackage {
    private final GroupMessage message;
    private final NodeID       nodeID;

    MessagePackage(NodeID nodeID, GroupMessage message) {
      this.message = message;
      this.nodeID = nodeID;
    }

    GroupMessage getMessage() {
      return this.message;
    }

    NodeID getNodeID() {
      return this.nodeID;
    }
  }

  private class TestGroupMessageListener implements GroupMessageListener {
    private final long                                timeout;
    private final LinkedBlockingQueue<MessagePackage> queue = new LinkedBlockingQueue(100);

    TestGroupMessageListener(long timeout) {
      this.timeout = timeout;
    }

    @Override
    public void messageReceived(NodeID fromNode, GroupMessage msg) {
      queue.add(new MessagePackage(fromNode, msg));
    }

    public MessagePackage poll() throws InterruptedException {
      return (queue.poll(timeout, TimeUnit.MILLISECONDS));
    }

    public GroupMessage getNextMessageFrom(NodeID nodeID) throws InterruptedException {
      MessagePackage pkg = poll();
      assertNotNull("Failed to receive message from " + nodeID, pkg);
      assertTrue(nodeID.equals(pkg.getNodeID()));
      return (pkg.getMessage());
    }
  }

  private class ResponseL2StateMessageListener extends TestGroupMessageListener {
    TCGroupManagerImpl manager;

    ResponseL2StateMessageListener(TCGroupManagerImpl manager, long timeout) {
      super(timeout);
      this.manager = manager;
    }

    @Override
    public void messageReceived(NodeID fromNode, GroupMessage msg) {
      super.messageReceived(fromNode, msg);
      L2StateMessage message = (L2StateMessage) msg;
      GroupMessage resultAgreed = L2StateMessage.createResultAgreedMessage(message, message.getEnrollment());
      try {
        manager.sendTo(message.messageFrom(), resultAgreed);
      } catch (GroupException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private static final class MyZapNodeRequestProcessor implements ZapNodeRequestProcessor {

    public NoExceptionLinkedQueue outgoing = new NoExceptionLinkedQueue();
    public NoExceptionLinkedQueue incoming = new NoExceptionLinkedQueue();

    @Override
    public boolean acceptOutgoingZapNodeRequest(NodeID nodeID, int type, String reason) {
      outgoing.put(reason);
      return true;
    }

    @Override
    public void incomingZapNodeRequest(NodeID nodeID, int zapNodeType, String reason, long[] weights) {
      incoming.put(reason);
    }

    @Override
    public long[] getCurrentNodeWeights() {
      return new long[0];
    }

    @Override
    public void addZapEventListener(ZapEventListener listener) {
      //
    }
  }

  static final class MockZapNodeRequestProcessor implements ZapNodeRequestProcessor {

    public NoExceptionLinkedQueue outgoing = new NoExceptionLinkedQueue();
    public NoExceptionLinkedQueue incoming = new NoExceptionLinkedQueue();
    private static final int      weight   = 0;

    @Override
    public boolean acceptOutgoingZapNodeRequest(NodeID nodeID, int type, String reason) {
      outgoing.put(reason);
      return true;
    }

    @Override
    public void incomingZapNodeRequest(NodeID nodeID, int zapNodeType, String reason, long[] weights) {
      incoming.put(reason);
    }

    @Override
    public long[] getCurrentNodeWeights() {
      long[] rv = new long[] { weight };
      return rv;
    }

    @Override
    public void addZapEventListener(ZapEventListener listener) {
      //
    }
  }

  private static final class MyGroupEventListener implements GroupEventsListener {

    @Override
    public void nodeJoined(NodeID nodeID) {
      System.err.println("\n### nodeJoined -> " + nodeID);
    }

    @Override
    public void nodeLeft(NodeID nodeID) {
      System.err.println("\n### nodeLeft -> " + nodeID);
    }
  }

  private static final class TestMessage extends AbstractGroupMessage {

    // to make serialization sane
    public TestMessage() {
      super(0);
    }

    public TestMessage(String message) {
      super(0);
      this.msg = message;
    }

    @Override
    protected void basicDeserializeFrom(TCByteBufferInput in) throws IOException {
      msg = in.readString();
    }

    @Override
    protected void basicSerializeTo(TCByteBufferOutput out) {
      out.writeString(msg);
    }

    String msg;

    @Override
    public int hashCode() {
      return msg.hashCode();
    }

    @Override
    public boolean equals(Object o) {
      if (o instanceof TestMessage) {
        TestMessage other = (TestMessage) o;
        return this.msg.equals(other.msg);
      }
      return false;
    }

    @Override
    public String toString() {
      return "TestMessage [ " + msg + "]";
    }
  }

  private static class TestGroupEventListener implements GroupEventsListener {
    private final TCGroupManagerImpl manager;
    private int joined = 0;

    TestGroupEventListener(TCGroupManagerImpl manager) {
      this.manager = manager;
    }

    @Override
    public synchronized void nodeJoined(NodeID nodeID) {
      joined++;
      System.out.println("XXX " + manager.getLocalNodeID() + " Node joined: " + nodeID);
      notifyAll();
    }

    synchronized void waitForJoinedCount(int i) throws InterruptedException {
      while (joined != i) {
        wait();
      }
    }

    @Override
    public void nodeLeft(NodeID nodeID) {
      System.out.println("XXX " + manager.getLocalNodeID() + " Node left: " + nodeID);
    }
  }

  private static class NullChannelEventListener implements ChannelEventListener {

    @Override
    public void notifyChannelEvent(ChannelEvent event) {
      return;
    }
  }

}
