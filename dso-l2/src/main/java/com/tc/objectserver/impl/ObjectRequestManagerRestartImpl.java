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
package com.tc.objectserver.impl;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.net.ClientID;
import com.tc.net.NodeID;
import com.tc.object.ObjectID;
import com.tc.object.ObjectRequestServerContext;
import com.tc.object.ObjectRequestServerContext.LOOKUP_STATE;
import com.tc.object.tx.ServerTransactionID;
import com.tc.objectserver.api.ObjectManager;
import com.tc.objectserver.api.ObjectRequestManager;
import com.tc.objectserver.tx.AbstractServerTransactionListener;
import com.tc.objectserver.tx.ServerTransactionManager;
import com.tc.text.PrettyPrinter;
import com.tc.util.ObjectIDSet;
import com.tc.util.State;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

public class ObjectRequestManagerRestartImpl extends AbstractServerTransactionListener implements ObjectRequestManager {

  private final static State                      INIT                 = new State("INITIAL");
  private final static State                      STARTING             = new State("STARTING");
  private final static State                      STARTED              = new State("STARTED");

  private final static TCLogger                   logger               = TCLogging
                                                                           .getLogger(ObjectRequestManagerRestartImpl.class);

  private final ObjectRequestManager              delegate;
  private final ServerTransactionManager          transactionManager;
  private final ObjectManager                     objectManager;

  private final Set                               resentTransactionIDs = Collections.synchronizedSet(new HashSet());
  private final Queue<ObjectRequestServerContext> pendingRequests      = new LinkedBlockingQueue<ObjectRequestServerContext>();
  private volatile State                          state                = INIT;

  public ObjectRequestManagerRestartImpl(final ObjectManager objectMgr,
                                         final ServerTransactionManager transactionManager,
                                         final ObjectRequestManager delegate) {
    this.objectManager = objectMgr;
    this.delegate = delegate;
    this.transactionManager = transactionManager;
    transactionManager.addTransactionListener(this);
  }

  @Override
  public void transactionManagerStarted(final Set cids) {
    this.state = STARTING;
    this.objectManager.start();
    moveToStartedIfPossible();
  }

  private void moveToStartedIfPossible() {
    if (this.state == STARTING && this.resentTransactionIDs.isEmpty()) {
      this.state = STARTED;
      this.transactionManager.removeTransactionListener(this);
      processPending();
    }
  }

  @Override
  public void addResentServerTransactionIDs(final Collection sTxIDs) {
    if (this.state != INIT) { throw new AssertionError("Cant add Resent transactions after start up ! " + sTxIDs.size()
                                                       + "Txns : " + this.state); }
    this.resentTransactionIDs.addAll(sTxIDs);
    logger.info("resentTransactions = " + sTxIDs.size());
  }

  @Override
  public void transactionApplied(final ServerTransactionID stxID, final ObjectIDSet newObjectsCreated) {
    processResentTxnComplete(stxID);
  }

  protected void processResentTxnComplete(final ServerTransactionID stxID) {
    if (this.state != STARTING) { return; } // Skips in passive
    this.resentTransactionIDs.remove(stxID);
    moveToStartedIfPossible();
  }

  protected void processResentTxnComplete(final Set<ServerTransactionID> incomingTxnIds) {
    if (this.state == STARTED) { return; }
    for (final ServerTransactionID txnID : incomingTxnIds) {
      processResentTxnComplete(txnID);
    }
  }

  @Override
  public void clearAllTransactionsFor(final NodeID client) {
    if (this.state == STARTED) { return; }
    synchronized (this.resentTransactionIDs) {
      for (final Iterator iter = this.resentTransactionIDs.iterator(); iter.hasNext();) {
        final ServerTransactionID stxID = (ServerTransactionID) iter.next();
        if (stxID.getSourceID().equals(client)) {
          iter.remove();
        }
      }
    }
    moveToStartedIfPossible();
  }

  private void processPending() {
    logger.info("Processing Pending Lookups = " + this.pendingRequests.size());
    ObjectRequestServerContext lookupContext;
    while ((lookupContext = this.pendingRequests.poll()) != null) {
      logger.info("Processing pending Looking up : " + toString(lookupContext));
      this.delegate.requestObjects(lookupContext);
    }
  }

  public String toString(final ObjectRequestServerContext c) {
    return c.getClass().getName() + " [ " + c.getClientID() + " :  " + c.getRequestedObjectIDs() + " : "
           + c.getRequestID() + " : " + c.getRequestDepth() + " : " + c.getRequestingThreadName() + "] ";
  }

  @Override
  public void requestObjects(final ObjectRequestServerContext requestContext) {
    if (this.state != STARTED) {
      this.pendingRequests.add(requestContext);
      if (logger.isDebugEnabled()) {
        logger.debug("RequestObjectManager is not started, lookup has been added to pending request: "
                     + toString(requestContext));
      }
      return;
    }
    this.delegate.requestObjects(requestContext);
  }

  @Override
  public void sendObjects(final ClientID requestedNodeID, final Collection objs, final ObjectIDSet requestedObjectIDs,
                          final ObjectIDSet missingObjectIDs, final LOOKUP_STATE lookupState, final int maxRequestDepth) {

    this.delegate
        .sendObjects(requestedNodeID, objs, requestedObjectIDs, missingObjectIDs, lookupState, maxRequestDepth);

  }

  @Override
  public PrettyPrinter prettyPrint(final PrettyPrinter out) {
    out.print(this.getClass().getSimpleName()).flush();
    out.indent().print("State: " + this.state).flush();
    out.indent().print("ResentTransactionIDs : " + this.resentTransactionIDs).flush();
    out.indent().print("PendingRequests: " + this.pendingRequests.size()).flush();
    for (final ObjectRequestServerContext objReqServerContext : this.pendingRequests) {
      out.duplicateAndIndent().indent().print(toString(objReqServerContext)).flush();
    }
    out.indent().print("ObjectRequestManager: ").visit(this.delegate).flush();
    return out;
  }

  @Override
  public int getLiveObjectCount() {
    return this.objectManager.getLiveObjectCount();
  }

  @Override
  public Iterator getRootNames() {
    return this.objectManager.getRootNames();
  }

  @Override
  public Iterator getRoots() {
    return this.objectManager.getRoots();
  }

  @Override
  public ObjectID lookupRootID(final String name) {
    return this.objectManager.lookupRootID(name);
  }

}
