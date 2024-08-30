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
package com.tc.objectserver.core.api;

import com.tc.io.TCByteBufferOutputStream;
import com.tc.object.ObjectID;
import com.tc.object.dna.api.DNA;
import com.tc.object.dna.api.DNA.DNAType;
import com.tc.object.dna.api.DNAException;
import com.tc.object.dna.impl.ObjectStringSerializer;
import com.tc.object.tx.TransactionID;
import com.tc.objectserver.api.ObjectInstanceMonitor;
import com.tc.objectserver.impl.ManagedObjectReference;
import com.tc.objectserver.managedobject.ApplyTransactionInfo;
import com.tc.objectserver.managedobject.ManagedObjectStateSerializer;
import com.tc.objectserver.managedobject.ManagedObjectTraverser;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.Set;

public interface ManagedObject {

  public ObjectID getID();

  public ManagedObjectReference getReference();

  public Set<ObjectID> getObjectReferences();

  public void apply(DNA dna, TransactionID txnID, ApplyTransactionInfo includeIDs,
                    ObjectInstanceMonitor instanceMonitor, boolean ignoreIfOlderDNA) throws DNAException;

  public void toDNA(TCByteBufferOutputStream out, ObjectStringSerializer serializer, DNAType type);

  public boolean isDirty();

  public void setIsDirty(boolean isDirty);

  public boolean isNew();

  public void setIsNew(boolean isNew);

  public ManagedObjectState getManagedObjectState();

  public void addObjectReferencesTo(ManagedObjectTraverser traverser);

  public long getVersion();

  public void serializeTo(ObjectOutput out, ManagedObjectStateSerializer stateSerializer) throws IOException;
}