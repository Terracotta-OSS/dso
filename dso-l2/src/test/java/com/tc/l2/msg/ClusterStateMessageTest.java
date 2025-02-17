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
package com.tc.l2.msg;

import com.tc.io.TCByteBufferInputStream;
import com.tc.io.TCByteBufferOutputStream;
import com.tc.l2.ha.ClusterState;
import com.tc.l2.ha.ClusterStateImpl;
import com.tc.net.GroupID;
import com.tc.net.groups.DummyStripeIDStateManager;
import com.tc.net.groups.StripeIDStateManager;
import com.tc.net.protocol.transport.ConnectionID;
import com.tc.net.protocol.transport.ConnectionIDFactory;
import com.tc.objectserver.gtx.GlobalTransactionIDSequenceProvider;
import com.tc.objectserver.handler.GlobalTransactionIDBatchRequestHandler;
import com.tc.objectserver.impl.ConnectionIDFactoryImpl;
import com.tc.objectserver.persistence.ClusterStatePersistor;
import com.tc.objectserver.persistence.HeapStorageManagerFactory;
import com.tc.objectserver.persistence.Persistor;
import com.tc.objectserver.persistence.impl.TestMutableSequence;
import com.tc.util.State;
import com.tc.util.sequence.DGCSequenceProvider;
import com.tc.util.sequence.ObjectIDSequence;

import java.util.Iterator;

import junit.framework.TestCase;

/*
 * This test really belongs in the TC Messaging module but it's dependencies
 * currently prevent that.  It needs some heavy refactoring.
 */
public class ClusterStateMessageTest extends TestCase {
  private static final int CLUSTER_STATE_1 = 1;
  private static final int CLUSTER_STATE_2 = 2;

  private ClusterStateImpl clusterState_1;
  private ClusterStateImpl clusterState_2;

  @Override
  public void setUp() {
    resetClusterState(CLUSTER_STATE_1);
    resetClusterState(CLUSTER_STATE_2);
  }

  @Override
  public void tearDown() {
    clusterState_1 = null;
    clusterState_2 = null;
  }

  private void resetClusterState(int clusterState) {
    Persistor persistor =  new Persistor(HeapStorageManagerFactory.INSTANCE);
    persistor.start();
    ClusterStatePersistor clusterStateStore = persistor.getClusterStatePersistor();
    ObjectIDSequence oidSequence = persistor.getManagedObjectPersistor().getObjectIDSequence();
    ConnectionIDFactory connectionIdFactory = new ConnectionIDFactoryImpl(persistor.getClientStatePersistor());
    GlobalTransactionIDSequenceProvider gidSequenceProvider = new GlobalTransactionIDBatchRequestHandler(
                                                                                                         new TestMutableSequence());
    StripeIDStateManager stripeIDStateManager = new DummyStripeIDStateManager();
    DGCSequenceProvider dgcSequenceProvider = new DGCSequenceProvider(new TestMutableSequence());
    if (clusterState == CLUSTER_STATE_1) {
      clusterState_1 = new ClusterStateImpl(clusterStateStore, oidSequence, connectionIdFactory, gidSequenceProvider,
                                            new GroupID(1), stripeIDStateManager, dgcSequenceProvider);
      clusterState_1.setStripeID("foobar");
    } else {
      clusterState_2 = new ClusterStateImpl(clusterStateStore, oidSequence, connectionIdFactory, gidSequenceProvider,
                                        new GroupID(1), stripeIDStateManager, dgcSequenceProvider);
      clusterState_2.setStripeID("foobar");
    }
  }

  private void validate(ClusterStateMessage csm, ClusterStateMessage csm1) {
    assertEquals(csm.getMessageID(), csm1.getMessageID());
    assertEquals(csm.getType(), csm1.getType());
    assertEquals(csm.inResponseTo(), csm1.inResponseTo());
    assertEquals(csm.messageFrom(), csm1.messageFrom());

    assertEquals(csm.getNextAvailableObjectID(), csm1.getNextAvailableObjectID());
    assertEquals(csm.getNextAvailableGlobalTxnID(), csm1.getNextAvailableGlobalTxnID());
    assertEquals(csm.getClusterID(), csm1.getClusterID());
    assertEquals(csm.getConnectionID(), csm1.getConnectionID());
  }

  private void validate(ClusterState cs, ClusterState cs1) {
    assertEquals(cs.getNextAvailableChannelID(), cs1.getNextAvailableChannelID());
    assertEquals(cs.getNextAvailableGlobalTxnID(), cs1.getNextAvailableGlobalTxnID());
    assertEquals(cs.getNextAvailableObjectID(), cs1.getNextAvailableObjectID());
    assertEquals(cs.getAllConnections().size(), cs1.getAllConnections().size());

    Iterator iter1 = cs1.getAllConnections().iterator();
    for (Object element : cs.getAllConnections()) {
      assertEquals(((ConnectionID) element).getID(), ((ConnectionID) iter1.next()).getID());
    }

    assertEquals(cs.getStripeID(), cs1.getStripeID());
  }

  private ClusterStateMessage writeAndRead(ClusterStateMessage csm) throws Exception {
    TCByteBufferOutputStream bo = new TCByteBufferOutputStream();
    csm.serializeTo(bo);
    System.err.println("Written : " + csm);
    TCByteBufferInputStream bi = new TCByteBufferInputStream(bo.toArray());
    ClusterStateMessage csm1 = new ClusterStateMessage();
    csm1.deserializeFrom(bi);
    System.err.println("Read : " + csm1);
    return csm1;
  }

  private void modifyClusterState(int clusterState) {
    ConnectionID connectionID_1 = new ConnectionID("jvm10", 10, "foo");
    ConnectionID connectionID_2 = new ConnectionID("jvm11", 11, "foo");
    ConnectionID connectionID_3 = new ConnectionID("jvm12", 12, "goo");

    if (clusterState == CLUSTER_STATE_1) {
      clusterState_1.addNewConnection(connectionID_1);
      clusterState_1.addNewConnection(connectionID_2);
      clusterState_1.addNewConnection(connectionID_3);
      clusterState_1.removeConnection(connectionID_2);
      clusterState_1.setNextAvailableChannelID(13);
      clusterState_1.setNextAvailableObjectID(222);
      clusterState_1.setNextAvailableGlobalTransactionID(333);
      clusterState_1.setCurrentState(new State("testing"));
    } else {
      clusterState_2.addNewConnection(connectionID_1);
      clusterState_2.addNewConnection(connectionID_2);
      clusterState_2.addNewConnection(connectionID_3);
      clusterState_2.removeConnection(connectionID_2);
      clusterState_2.setNextAvailableChannelID(13);
      clusterState_2.setNextAvailableObjectID(222);
      clusterState_2.setNextAvailableGlobalTransactionID(333);
      clusterState_2.setCurrentState(new State("testing"));

    }
  }

  public void testBasicSerialization() throws Exception {
    modifyClusterState(CLUSTER_STATE_1);

    ClusterStateMessage csm = ClusterStateMessage.createClusterStateMessage(clusterState_1);
    ClusterStateMessage csm1 = writeAndRead(csm);
    validate(csm, csm1);

    csm = ClusterStateMessage.createOKResponse(new ClusterStateMessage());
    csm1 = writeAndRead(csm);
    validate(csm, csm1);
  }

  public void testInitState() throws Exception {
    ConnectionID connectionID = new ConnectionID("jvm10", 10, "foo");

    // COMPLETE_STATE
    modifyClusterState(CLUSTER_STATE_1);
    ClusterStateMessage csm = ClusterStateMessage.createClusterStateMessage(clusterState_1);
    ClusterStateMessage csm1 = writeAndRead(csm);
    validate(csm, csm1);
    csm1.initState(clusterState_2);
    validate(clusterState_1, clusterState_2);

    // GLOBAL_TRANSACTION_ID
    resetClusterState(CLUSTER_STATE_1);
    resetClusterState(CLUSTER_STATE_2);
    clusterState_1.setNextAvailableGlobalTransactionID(3423);
    csm = (ClusterStateMessage) ClusterStateMessage.createNextAvailableGlobalTransactionIDMessage(clusterState_1);
    csm1 = writeAndRead(csm);
    validate(csm, csm1);
    csm1.initState(clusterState_2);
    validate(clusterState_1, clusterState_2);

    // OBJECT_ID
    resetClusterState(CLUSTER_STATE_1);
    resetClusterState(CLUSTER_STATE_2);
    clusterState_1.setNextAvailableObjectID(6868);
    csm = (ClusterStateMessage) ClusterStateMessage.createNextAvailableObjectIDMessage(clusterState_1);
    csm1 = writeAndRead(csm);
    validate(csm, csm1);
    csm1.initState(clusterState_2);
    validate(clusterState_1, clusterState_2);

    // NEW_CONNECTION_CREATED
    csm = (ClusterStateMessage) ClusterStateMessage.createNewConnectionCreatedMessage(connectionID);
    csm1 = writeAndRead(csm);
    validate(csm, csm1);
    resetClusterState(CLUSTER_STATE_2);
    csm1.initState(clusterState_2);
    assertEquals(connectionID, clusterState_2.getAllConnections().iterator().next());

    // CONNECTION_DESTROYED
    csm = (ClusterStateMessage) ClusterStateMessage.createConnectionDestroyedMessage(connectionID);
    csm1 = writeAndRead(csm);
    validate(csm, csm1);
    resetClusterState(CLUSTER_STATE_2);
    clusterState_2.addNewConnection(connectionID);
    csm1.initState(clusterState_2);
    assertEquals(0, clusterState_2.getAllConnections().size());
  }

}
