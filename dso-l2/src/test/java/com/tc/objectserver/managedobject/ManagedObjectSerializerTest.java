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
package com.tc.objectserver.managedobject;

import com.tc.io.serializer.TCObjectInputStream;
import com.tc.io.serializer.TCObjectOutputStream;
import com.tc.object.LogicalOperation;
import com.tc.object.ObjectID;
import com.tc.object.TestDNACursor;
import com.tc.object.tx.TransactionID;
import com.tc.objectserver.api.ObjectInstanceMonitor;
import com.tc.objectserver.core.api.ManagedObject;
import com.tc.objectserver.core.api.TestDNA;
import com.tc.objectserver.impl.ObjectInstanceMonitorImpl;
import com.tc.objectserver.persistence.HeapStorageManagerFactory;
import com.tc.objectserver.persistence.Persistor;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

public class ManagedObjectSerializerTest extends TestCase {

  private ObjectID                     id;
  private ManagedObjectStateSerializer stateSerializer;

  public void test() throws Exception {
    ManagedObjectStateFactory.disableSingleton(true);
    Persistor persistor = new Persistor(HeapStorageManagerFactory.INSTANCE);
    persistor.start();
    
    ManagedObjectStateFactory.createInstance(new NullManagedObjectChangeListenerProvider(), persistor);
    this.stateSerializer = new ManagedObjectStateSerializer();
    this.id = new ObjectID(1);

    final ManagedObjectSerializer mos = new ManagedObjectSerializer(this.stateSerializer, persistor.getManagedObjectPersistor());
    final ManagedObjectImpl mo = new ManagedObjectImpl(this.id, persistor.getManagedObjectPersistor());
    assertTrue(mo.isDirty());
    assertTrue(mo.isNew());
    final TestDNA dna = newDNA(1);
    final ObjectInstanceMonitor imo = new ObjectInstanceMonitorImpl();
    mo.apply(dna, new TransactionID(1), new ApplyTransactionInfo(), imo, false);

    final ByteArrayOutputStream baout = new ByteArrayOutputStream();
    final TCObjectOutputStream out = new TCObjectOutputStream(baout);
    mos.serializeTo(mo, out);
    out.flush();
    final ManagedObject mo2 = (ManagedObject) mos
        .deserializeFrom(new TCObjectInputStream(new ByteArrayInputStream(baout.toByteArray())));

    assertFalse(mo2.isDirty());
    mo.setIsDirty(false);
    assertNotSame(mo, mo2);
    assertTrue(mo.isEqual(mo2));
  }

  private TestDNA newDNA(final int fieldSetCount) {
    final TestDNACursor cursor = new TestDNACursor();
    cursor.addArrayAction(new Object[] { new ObjectID(5000) });
    for (int i = 0; i < fieldSetCount; i++) {
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "refField" + i, new ObjectID(1) });
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "booleanField" + i, Boolean.valueOf(true) });
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "byteField" + i, Byte.valueOf((byte) 1) });
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "characterField" + i, Character.valueOf('c') });
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "doubleField" + i, Double.valueOf(100.001d) });
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "floatField" + i, Float.valueOf(100.001f) });
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "integerField" + i, Integer.valueOf(100) });
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "longField" + i, Long.valueOf(100) });
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "stringField" + i, "Some nice string field" + i });
      cursor.addLogicalAction(LogicalOperation.PUT, new Object[] { "shortField" + i, Short.valueOf((short) 1) });
    }
    final TestDNA dna = new TestDNA(cursor, "com.terracotta.toolkit.object.ToolkitObjectStripeImpl");
    return dna;
  }
}
