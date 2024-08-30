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
package com.tc.l2.objectserver;

import com.tc.net.groups.MessageID;
import com.tc.object.tx.ServerTransactionID;

public interface L2ObjectSyncAckManager {

  /**
   * Startup the object sync with sync messages coming from a particular node.
   */
  public void reset();

  /**
   * Add an object sync message to be ACKed upon completion.
   */
  public void addObjectSyncMessageToAck(final ServerTransactionID stxnID, final MessageID requestID);

  /**
   * Complete the object sync
   */
  public void objectSyncComplete();

  /**
   * ACK the object sync txn inline for use when the object sync transaction is ignored (when the L2 is already
   * PASSIVE-STANDBY).
   */
  public void ackObjectSyncTxn(final ServerTransactionID stxnID);
}
