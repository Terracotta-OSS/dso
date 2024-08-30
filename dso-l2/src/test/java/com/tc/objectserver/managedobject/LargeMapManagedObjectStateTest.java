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
package com.tc.objectserver.managedobject;

import com.tc.object.ObjectID;
import com.tc.object.TestDNACursor;
import com.tc.object.TestDNAWriter;
import com.tc.object.dna.api.DNA.DNAType;
import com.tc.objectserver.core.api.ManagedObjectState;
import com.tc.util.Assert;

public class LargeMapManagedObjectStateTest extends AbstractTestManagedObjectState {

  // override due to difference on dehydrate
  @Override
  protected void basicDehydrate(final TestDNACursor cursor, final int objCount, final ManagedObjectState state) {
    final TestDNAWriter dnaWriter = new TestDNAWriter();
    state.dehydrate(this.objectID, dnaWriter, DNAType.L1_FAULT);
    Assert.assertEquals(objCount, dnaWriter.getActionCount());
  }

  public void testObjectList1() throws Exception {
    final String className = ManagedObjectStateStaticConfig.SERIALIZED_CLUSTER_OBJECT.getClientClassName();
    final TestDNACursor cursor = new TestDNACursor();
    byte[] value = new byte[1024 * 1024];
    for (int x=0;x<value.length;x++) {
      value[x] = 1;
    }

    cursor.addEntireArray(value);
    final ManagedObjectState state = ManagedObjectStateFactory.getInstance().createState(new ObjectID(0), new ObjectID(1),
                                                                                         className, cursor);
    state.apply(new ObjectID(3), cursor, new ApplyTransactionInfo());
    basicReadWriteEqual(ManagedObjectStateStaticConfig.SERIALIZED_CLUSTER_OBJECT.getStateObjectType(), state);
  }

}
