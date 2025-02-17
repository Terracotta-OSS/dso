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
package com.tc.objectserver.tx;

public class TransactionState {
  private static final int             APPLY_COMMITTED            = 0x01;
  private static final int             BROADCAST_COMPLETED        = 0x02;
  private static final int             TXN_RELAYED                = 0x04;
  private static final int             PROCESS_METADATA_COMPLETED = 0x08;

  public static final int              PASSIVE_START_STATE        = BROADCAST_COMPLETED | TXN_RELAYED;

  private static final int             TXN_PROCESSING_COMPLETE    = (APPLY_COMMITTED | BROADCAST_COMPLETED
                                                                     | TXN_RELAYED | PROCESS_METADATA_COMPLETED);

  public static final TransactionState COMPLETED_STATE            = new TransactionState(TXN_PROCESSING_COMPLETE);

  private int                          state;

  public TransactionState() {
    this(0x00);
  }

  public TransactionState(final int state) {
    this.state = state;
  }

  public void applyAndCommitSkipped() {
    this.state |= APPLY_COMMITTED;
  }

  public boolean isComplete() {
    return (this.state == TXN_PROCESSING_COMPLETE);
  }

  public void broadcastCompleted() {
    this.state |= BROADCAST_COMPLETED;
  }

  public void processMetaDataCompleted() {
    state |= PROCESS_METADATA_COMPLETED;
  }

  public void applyCommitted() {
    this.state |= APPLY_COMMITTED;
  }

  public void relayTransactionComplete() {
    state |= TXN_RELAYED;
  }

  public boolean isRelayComplete() {
    return (state & TXN_RELAYED) != 0;
  }

  public boolean isApplyCommitted() {
    return (state & APPLY_COMMITTED) != 0;
  }

  @Override
  public String toString() {
    return "TransactionState = [ "
           + ((state & APPLY_COMMITTED) == APPLY_COMMITTED ? " APPLY_COMMITED : " : " : ")
           + ((state & TXN_RELAYED) == TXN_RELAYED ? " TXN_RELAYED : " : " : ")
           + ((state & BROADCAST_COMPLETED) == BROADCAST_COMPLETED ? " BROADCAST_COMPLETED : " : " : ")
           + ((state & PROCESS_METADATA_COMPLETED) == PROCESS_METADATA_COMPLETED ? " PROCESS_METADATA_COMPLETED ] "
               : " ] ");
  }
}