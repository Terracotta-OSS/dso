/* 
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at 
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is 
 *      Terracotta, Inc., a Software AG company
 */
package com.tc.objectserver.metadata;

import com.tc.objectserver.managedobject.ApplyTransactionInfo;
import com.tc.objectserver.tx.ServerTransaction;
import com.tc.objectserver.tx.ServerTransactionManager;

/**
 * Manager to process Metadata from a DNA
 * 
 * @author Nabib El-Rahman
 */
public interface MetaDataManager {

  /**
   * Process metadata.
   * 
   * @param txn transaction associated with metadata reader.
   * @param applyInfo applyinfo associated with the txn.
   * @return boolean if all meta data processing is complete
   */
  public boolean processMetaData(ServerTransaction txn, ApplyTransactionInfo applyInfo);

  public void setTransactionManager(ServerTransactionManager transactionManager);

}
