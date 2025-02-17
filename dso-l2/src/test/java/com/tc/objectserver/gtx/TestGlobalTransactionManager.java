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
package com.tc.objectserver.gtx;

import com.tc.exception.ImplementMe;
import com.tc.net.NodeID;
import com.tc.object.dna.api.LogicalChangeID;
import com.tc.object.dna.api.LogicalChangeResult;
import com.tc.object.gtx.GlobalTransactionID;
import com.tc.object.tx.ServerTransactionID;
import com.tc.text.PrettyPrinter;
import com.tc.util.sequence.Sequence;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class TestGlobalTransactionManager implements ServerGlobalTransactionManager {

  private long                           idSequence   = 0;
  private final Set<ServerTransactionID> commitedSIDs = new HashSet<ServerTransactionID>();

  @Override
  public boolean initiateApply(ServerTransactionID stxID) {
    return !commitedSIDs.contains(stxID);
  }

  @Override
  public void commit(ServerTransactionID stxID) {
    commitedSIDs.add(stxID);
  }

  @Override
  public GlobalTransactionID getLowGlobalTransactionIDWatermark() {
    return GlobalTransactionID.NULL_ID;
  }

  @Override
  public void clearCommitedTransactionsBelowLowWaterMark(ServerTransactionID sid) {
    //
  }

  @Override
  public void clearCommittedTransaction(final ServerTransactionID serverTransactionID) {
    // Does nothing...
  }

  @Override
  public void shutdownNode(NodeID nodeID) {
    //
  }

  public void clear() {
    commitedSIDs.clear();
  }

  @Override
  public GlobalTransactionID getOrCreateGlobalTransactionID(ServerTransactionID serverTransactionID) {
    return new GlobalTransactionID(idSequence++);
  }

  @Override
  public void createGlobalTransactionDescIfNeeded(ServerTransactionID stxnID, GlobalTransactionID globalTransactionID) {
    throw new ImplementMe();
  }

  @Override
  public void shutdownAllClientsExcept(Set cids) {
    //
  }

  @Override
  public Sequence getGlobalTransactionIDSequence() {
    throw new ImplementMe();
  }

  @Override
  public GlobalTransactionIDSequenceProvider getGlobalTransactionIDSequenceProvider() {
    throw new ImplementMe();
  }

  @Override
  public GlobalTransactionID getGlobalTransactionID(ServerTransactionID serverTransactionID) {
    throw new ImplementMe();
  }

  @Override
  public void clearCommitedTransactionsBelowLowWaterMark(GlobalTransactionID lowGlobalTransactionIDWatermark) {
    //
  }

  @Override
  public void registerCallbackOnLowWaterMarkReached(Runnable callback) {
    //
  }

  @Override
  public void recordApplyResults(ServerTransactionID stxnID, Map<LogicalChangeID, LogicalChangeResult> results) {
    //
  }

  @Override
  public Map<LogicalChangeID, LogicalChangeResult> getApplyResults(ServerTransactionID stxnID) {
    return null;
  }

  @Override
  public PrettyPrinter prettyPrint(final PrettyPrinter out) {
    return out;
  }
}