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
package com.tc.objectserver.tx;

import com.tc.async.api.EventContext;
import com.tc.bytes.TCByteBuffer;
import com.tc.net.NodeID;
import com.tc.object.ObjectID;
import com.tc.object.dna.impl.ObjectStringSerializer;
import com.tc.object.tx.ServerTransactionID;

import java.util.List;
import java.util.Set;

public interface TransactionBatchContext extends EventContext {

  public abstract Set<ServerTransactionID> getTransactionIDs();

  public abstract Set<ObjectID> getNewObjectIDs();

  public abstract TransactionBatchReader getTransactionBatchReader();

  public abstract ObjectStringSerializer getSerializer();

  public abstract NodeID getSourceNodeID();

  public abstract int getNumTxns();

  public abstract List<ServerTransaction> getTransactions();

  public abstract TCByteBuffer[] getBackingBuffers();

}