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

import com.tc.object.LogicalOperation;
import com.tc.object.ObjectID;
import com.tc.object.TCObject;
import com.tc.object.change.TCChangeBuffer;
import com.tc.object.change.TCChangeBufferImpl;
import com.tc.object.dna.api.LogicalChangeID;
import com.tc.object.locks.Notify;
import com.tc.object.metadata.MetaDataDescriptorInternal;
import com.tc.util.Assert;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Client side transaction : Collects all changes by a single thread under a lock
 */
public class ClientTransactionImpl extends AbstractClientTransaction {
  private final Map<ObjectID, TCChangeBuffer> objectChanges = new LinkedHashMap<ObjectID, TCChangeBuffer>();

  private Map                                 newRoots;
  private List                                notifies;

  // used to keep things referenced until the transaction is completely ACKED
  private final Map                           referenced    = new IdentityHashMap();
  private final int                           session;

  public ClientTransactionImpl(int session) {
    super();
    this.session = session;
  }

  @Override
  public boolean isConcurrent() {
    return this.getLockType().isConcurrent();
  }

  @Override
  public boolean hasChangesOrNotifies() {
    return !(objectChanges.isEmpty() && getNewRoots().isEmpty() && getNotifies().isEmpty());
  }

  @Override
  public boolean hasChanges() {
    return !(objectChanges.isEmpty() && getNewRoots().isEmpty());
  }

  @Override
  public Map getNewRoots() {
    return newRoots == null ? Collections.EMPTY_MAP : newRoots;
  }

  @Override
  public List getNotifies() {
    return notifies == null ? Collections.EMPTY_LIST : notifies;
  }

  @Override
  public Map getChangeBuffers() {
    return this.objectChanges;
  }

  @Override
  protected void basicLogicalInvoke(TCObject source, LogicalOperation method, Object[] parameters, LogicalChangeID id) {
    getOrCreateChangeBuffer(source).logicalInvoke(method, parameters, id);
  }

  @Override
  protected void basicCreate(TCObject object) {
    getOrCreateChangeBuffer(object);
  }

  @Override
  protected void basicCreateRoot(String name, ObjectID root) {
    if (newRoots == null) {
      newRoots = new HashMap();
    }
    newRoots.put(name, root);
  }

  private TCChangeBuffer getOrCreateChangeBuffer(TCObject object) {
    addReferenced(object.getPeerObject());

    ObjectID oid = object.getObjectID();

    TCChangeBuffer cb = objectChanges.get(oid);
    if (cb == null) {
      cb = new TCChangeBufferImpl(object);
      objectChanges.put(oid, cb);
    }

    return cb;
  }

  private void addReferenced(Object pojo) {
    Assert.assertNotNull("pojo", pojo);
    referenced.put(pojo, null);
  }

  @Override
  public Collection getReferencesOfObjectsInTxn() {
    return Collections.unmodifiableCollection(referenced.keySet());
  }

  @Override
  public void addNotify(Notify notify) {
    if (!notify.isNull()) {
      if (notifies == null) {
        notifies = new ArrayList();
      }

      notifies.add(notify);
    }
  }

  @Override
  public String toString() {
    return "ClientTransactionImpl@" + System.identityHashCode(this) + " [ " + getTransactionID() + " ]";
  }

  @Override
  public int getNotifiesCount() {
    return getNotifies().size();
  }

  @Override
  protected void basicAddMetaDataDescriptor(TCObject tco, MetaDataDescriptorInternal md) {
    getOrCreateChangeBuffer(tco).addMetaDataDescriptor(md);
  }

  @Override
  public int getSession() {
    return session;
  }

}
