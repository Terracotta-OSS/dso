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
package com.tc.objectserver.context;

import com.tc.async.api.EventContext;
import com.tc.net.NodeID;
import com.tc.object.dna.impl.ObjectStringSerializer;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.object.locks.LockID;
import com.tc.object.tx.ServerTransactionID;
import com.tc.object.tx.TransactionID;
import com.tc.object.tx.TxnBatchID;
import com.tc.object.tx.TxnType;
import com.tc.objectserver.locks.NotifiedWaiters;
import com.tc.objectserver.managedobject.ApplyTransactionInfo;
import com.tc.objectserver.tx.ServerTransaction;

import java.util.List;
import java.util.Map;

/**
 * Context need to broadcast the transaction to the interested nodes
 *
 * @author steve
 */
public class BroadcastChangeContext implements EventContext {
  private final ServerTransaction   tx;
  private final GlobalTransactionID lowGlobalTransactionIDWatermark;
  private final NotifiedWaiters     notifiedWaiters;
  private final ApplyTransactionInfo      applyInfo;

  public BroadcastChangeContext(ServerTransaction tx,
                                GlobalTransactionID lowGlobalTransactionIDWatermark, NotifiedWaiters notifiedWaiters,
                                ApplyTransactionInfo applyInfo) {
    this.tx = tx;
    this.lowGlobalTransactionIDWatermark = lowGlobalTransactionIDWatermark;
    this.notifiedWaiters = notifiedWaiters;
    this.applyInfo = applyInfo;
  }

  public ApplyTransactionInfo getApplyInfo() {
    return applyInfo;
  }

  public List getChanges() {
    return tx.getChanges();
  }

  public LockID[] getLockIDs() {
    return tx.getLockIDs();
  }

  public NodeID getNodeID() {
    return tx.getSourceID();
  }

  public TransactionID getTransactionID() {
    return tx.getTransactionID();
  }

  public ServerTransactionID getServerTransactionID() {
    return tx.getServerTransactionID();
  }

  public TxnBatchID getBatchID() {
    return tx.getBatchID();
  }

  public TxnType getTransactionType() {
    return tx.getTransactionType();
  }

  public GlobalTransactionID getGlobalTransactionID() {
    return tx.getGlobalTransactionID();
  }

  public GlobalTransactionID getLowGlobalTransactionIDWatermark() {
    return this.lowGlobalTransactionIDWatermark;
  }

  public ObjectStringSerializer getSerializer() {
    return tx.getSerializer();
  }

  public NotifiedWaiters getNewlyPendingWaiters() {
    return notifiedWaiters;
  }
  
  public Map getNewRoots() {
    return tx.getNewRoots();
  }

}
