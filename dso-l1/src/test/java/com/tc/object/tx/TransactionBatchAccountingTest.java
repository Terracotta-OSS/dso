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
package com.tc.object.tx;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import junit.framework.TestCase;

public class TransactionBatchAccountingTest extends TestCase {
  private TransactionBatchAccounting acct;
  private Sequence                   sequence;

  @Override
  public void setUp() throws Exception {
    acct = new TransactionBatchAccounting();
    sequence = new Sequence();
  }

  public void testBasics() throws Exception {
    final List incompleteBatchIDs = new LinkedList();
    // try adding an empty batch
    Batch batch1 = new Batch(new TxnBatchID(sequence.next()));
    acct.addBatch(batch1.batchID, batch1.transactionIDs);
    // there should be no incomplete batches
    assertEquals(Collections.EMPTY_LIST, acct.addIncompleteBatchIDsTo(new LinkedList()));
    // the min incomplete batch id should be the null id.
    assertEquals(TxnBatchID.NULL_BATCH_ID, acct.getMinIncompleteBatchID());

    // try adding a batch with a two transactions
    TransactionID txID1 = new TransactionID(sequence.next());
    TransactionID txID2 = new TransactionID(sequence.next());

    Batch batch2 = new Batch(new TxnBatchID(sequence.next()));
    batch2.addTransactionID(txID1);
    batch2.addTransactionID(txID2);
    incompleteBatchIDs.add(batch2.batchID);
    acct.addBatch(batch2.batchID, batch2.transactionIDs);

    TransactionID txID3 = new TransactionID(sequence.next());
    Batch batch3 = new Batch(new TxnBatchID(sequence.next()));
    batch3.addTransactionID(txID3);
    incompleteBatchIDs.add(batch3.batchID);
    acct.addBatch(batch3.batchID, batch3.transactionIDs);

    TransactionID txID4 = new TransactionID(sequence.next());
    Batch batch4 = new Batch(new TxnBatchID(sequence.next()));
    batch4.addTransactionID(txID4);
    incompleteBatchIDs.add(batch4.batchID);
    acct.addBatch(batch4.batchID, batch4.transactionIDs);

    // LWM is the lowest txn id, since there were no acknowledgements
    assertEquals(txID1, acct.getLowWaterMark());
    
    // check the incomplete batches
    assertEquals(incompleteBatchIDs, acct.addIncompleteBatchIDsTo(new LinkedList()));
    assertEquals(incompleteBatchIDs.get(0), acct.getMinIncompleteBatchID());

    // ACK the first transaction in the multi-transaction batch
    assertFalse(acct.acknowledge(batch2.batchID,Collections.singletonList(txID1)));
    // there should still be no completed batches
    assertEquals(incompleteBatchIDs, acct.addIncompleteBatchIDsTo(new LinkedList()));
    
    //LWM moved up
    assertEquals(txID2, acct.getLowWaterMark());

    // ACK the last transaction in the multi-transaction batch. This should cause that batch to become complete AND
    // cause all of its constituent transactions to become completed.
    assertTrue(acct.acknowledge(batch2.batchID,Collections.singletonList(txID2)));
    incompleteBatchIDs.remove(batch2.batchID);
    assertEquals(incompleteBatchIDs, acct.addIncompleteBatchIDsTo(new LinkedList()));
    assertEquals(incompleteBatchIDs.get(0), acct.getMinIncompleteBatchID());
    
    //LWM moved up
    assertEquals(txID3, acct.getLowWaterMark());
    
    // LWM remains the same
    assertEquals(txID3, acct.getLowWaterMark());

    // ACK another transaction
    assertTrue(acct.acknowledge(batch3.batchID,Collections.singletonList(txID3)));
    incompleteBatchIDs.remove(batch3.batchID);
    assertEquals(incompleteBatchIDs, acct.addIncompleteBatchIDsTo(new LinkedList()));
    assertEquals(incompleteBatchIDs.get(0), acct.getMinIncompleteBatchID());
    
    //LWM moved up
    assertEquals(txID4, acct.getLowWaterMark());
    
    // ACK the last transaction
    assertTrue(acct.acknowledge(batch4.batchID,Collections.singletonList(txID4)));
    incompleteBatchIDs.remove(batch4.batchID);
    assertEquals(Collections.EMPTY_LIST, incompleteBatchIDs);
    assertEquals(incompleteBatchIDs, acct.addIncompleteBatchIDsTo(new LinkedList()));
    assertEquals(TxnBatchID.NULL_BATCH_ID, acct.getMinIncompleteBatchID());

    //LWM moved up
    assertEquals(txID4.next(), acct.getLowWaterMark());
    
  }

  /**
   * Tests that the set of incomplete batch ids comes out in the same order it come in.
   */
  public void testAckAccounting() throws Exception {
    List<TxnBatchID> batchIDs = new LinkedList<TxnBatchID>();
    Map<TxnBatchID, TransactionID> firsts = new HashMap<TxnBatchID, TransactionID>();
    List<TxnBatchID> incomplete = new LinkedList<TxnBatchID>();
    for (int i = 0; i < 1000; i++) {
      TxnBatchID batchID = new TxnBatchID(sequence.next());
      ArrayList<TransactionID> transactionIDs = new ArrayList<TransactionID>();
      for (int j = 0; j < 10; j++) {
        transactionIDs.add(new TransactionID(sequence.next()));
      }
      firsts.put(batchID,transactionIDs.get(0));
      acct.addBatch(batchID, transactionIDs);
      batchIDs.add(batchID);

    }
    
    for ( TxnBatchID bid : batchIDs ) {
      acct.acknowledge(bid, Collections.singletonList(firsts.get(bid)));
    }
    
    assertEquals(batchIDs, acct.addIncompleteBatchIDsTo(incomplete));
    
    for ( TxnBatchID bid : incomplete ) {
      acct.addBatch(bid, new ArrayList<TransactionID>(acct.getTransactionIdsFor(bid)));
    }
    
    incomplete.clear();
    
    assertEquals(batchIDs, acct.addIncompleteBatchIDsTo(incomplete));
  }
  

  /**
   * Tests that the set of incomplete batch ids comes out in the same order it come in.
   */
  public void testBatchOrdering() throws Exception {
    List batchIDs = new LinkedList();
    for (int i = 0; i < 1000; i++) {
      TxnBatchID batchID = new TxnBatchID(sequence.next());
      ArrayList transactionIDs = new ArrayList();
      for (int j = 0; j < 10; j++) {
        transactionIDs.add(new TransactionID(sequence.next()));
      }
      acct.addBatch(batchID, transactionIDs);
      batchIDs.add(batchID);

      assertEquals(batchIDs, acct.addIncompleteBatchIDsTo(new LinkedList()));
    }
  }  

  private static final class Sequence {
    private final AtomicLong sequence = new AtomicLong(0);

    public long next() {
      return sequence.getAndIncrement();
    }
  }

  private static final class Batch {

    private final TxnBatchID batchID;
    private final List        transactionIDs = new ArrayList();

    public Batch(TxnBatchID batchID) {
      this.batchID = batchID;
    }

    public void addTransactionID(TransactionID txID) {
      transactionIDs.add(txID);
    }
  }

}
