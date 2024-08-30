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

import com.tc.net.ClientID;

import junit.framework.TestCase;


public class TransactionRecordTest extends TestCase {
  
  
  public void tests() {
    
    final ClientID clientID = new ClientID(1);
    final TransactionRecord record = new TransactionRecord();
    
    assertFalse(record.isComplete());
    
    record.applyAndCommitSkipped();
    
    assertFalse(record.isComplete());
    
    record.broadcastCompleted();
    
    assertFalse(record.isComplete());
    
    record.relayTransactionComplete();
    
    assertFalse(record.isComplete());
    
    record.addWaitee(clientID);  
    
    record.processMetaDataCompleted();
      
    assertFalse(record.isComplete());
    
    assertFalse(record.isEmpty());
    
    record.remove(clientID);
    
    assertTrue(record.isEmpty());
    
    assertTrue(record.isComplete());
  }
  
  public void testObjectSync() {
    final ClientID clientID = new ClientID(1);
    final TransactionRecord record = new TransactionRecord(clientID);
    
    assertFalse(record.isComplete());
    assertTrue(!record.isEmpty());
    
    assertTrue(record.remove(clientID));
    
    assertTrue(record.isComplete());
  }

}
