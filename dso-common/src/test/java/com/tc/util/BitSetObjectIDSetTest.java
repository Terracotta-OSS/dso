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
package com.tc.util;

import com.tc.io.TCByteBufferInputStream;
import com.tc.io.TCByteBufferOutputStream;
import org.junit.Test;

import com.tc.object.ObjectID;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BitSetObjectIDSetTest extends ObjectIDSetTestBase {
  @Override
  protected ObjectIDSet create() {
    return new BitSetObjectIDSet();
  }

  @Override
  protected ObjectIDSet create(final Collection<ObjectID> copy) {
    return new BitSetObjectIDSet(copy);
  }

  @Test
  public void testSerializeBitSetObjectIDSetDeserializeBasicObjectIDSet() throws Exception {
    BitSetObjectIDSet origin = new BitSetObjectIDSet();

    origin.add(new ObjectID(Long.MIN_VALUE));
    origin.add(new ObjectID(Long.MAX_VALUE));

    BasicObjectIDSet target = convertToBasicObjectIDSet(origin);

    assertEquals(2, target.size());
    Iterator<ObjectID> iterator = target.iterator();
    assertTrue(iterator.hasNext());
    ObjectID oid1 = iterator.next();
    assertEquals(Long.MIN_VALUE, oid1.toLong());
    assertTrue(iterator.hasNext());
    ObjectID oid2 = iterator.next();
    assertEquals(Long.MAX_VALUE, oid2.toLong());
    assertFalse(iterator.hasNext());
  }

  private BasicObjectIDSet convertToBasicObjectIDSet(BitSetObjectIDSet idSet) throws IOException {
    TCByteBufferOutputStream outputStream = new TCByteBufferOutputStream();
    idSet.serializeTo(outputStream);
    TCByteBufferInputStream inputStream = new TCByteBufferInputStream(outputStream.toArray());
    BasicObjectIDSet target = new BasicObjectIDSet();
    target.deserializeFrom(inputStream);
    return target;
  }

  @Test
  public void testCloneExpanding() throws Exception {
    ExpandingBitSetObjectIDSet expanding = new ExpandingBitSetObjectIDSet();
    for (int i = -1000000; i < 1000000; i++) {
      expanding.add(new ObjectID(i));
    }

    BitSetObjectIDSet bitSetOIDSet = new BitSetObjectIDSet(expanding);
    Iterator<ObjectID> iter = bitSetOIDSet.iterator();
    for (int i = -1000000; i < 1000000; i++) {
      assertTrue(iter.hasNext());
      assertEquals(i, iter.next().toLong());
    }
    assertFalse(iter.hasNext());
  }

  @Test
  public void testPerformance() {
    long seed = new SecureRandom().nextLong();
    final Random r = new Random(seed);
    ObjectIDSet set = create();
    Set<ObjectID> hashSet = new HashSet<ObjectID>();

    for (int i = 0; i < 800000; i++) {
      final long l = r.nextLong();
      final ObjectID id = new ObjectID(l);
      hashSet.add(id);
    }

    final long t1 = System.currentTimeMillis();
    for (final ObjectID objectID : hashSet) {
      set.add(objectID);
    }
    final long t2 = System.currentTimeMillis();

    for (final ObjectID objectID : hashSet) {
      set.contains(objectID);
    }
    final long t3 = System.currentTimeMillis();

    for (final ObjectID objectID : hashSet) {
      set.remove(objectID);
    }
    final long t4 = System.currentTimeMillis();

    final Set<ObjectID> hashSet2 = new HashSet<ObjectID>();
    for (int i = 0; i < 800000; i++) {
      hashSet2.add(new ObjectID(r.nextLong()));
    }

    final long t5 = System.currentTimeMillis();
    set.addAll(hashSet);
    final long t6 = System.currentTimeMillis();
    set.removeAll(hashSet);
    final long t7 = System.currentTimeMillis();

    for (int i = 0; i < 800000; i++) {
      set.add(new ObjectID(r.nextLong()));
    }

    final long t8 = System.currentTimeMillis();
    for (Iterator<ObjectID> i = set.iterator(); i.hasNext(); ) {
      i.next();
    }
    final long t9 = System.currentTimeMillis();
    int j = 0;
    for (Iterator<ObjectID> i = set.iterator(); i.hasNext(); ) {
      i.next();
      if (j++ % 2 == 0) {
        i.remove();
      }
    }
    final long t10 = System.currentTimeMillis();

    System.out.println("Times for ObjectIDSet type " + set.getClass().getSimpleName());
    System.out.println("add-> " + (t2 - t1) + " contains->"
                       + (t3 - t2) + " remove->" + (t4 - t3));
    System.out.println("addAll-> " + (t6 - t5) + " removeAll->"
                       + (t7 - t6));
    System.out.println("iteration->" + (t9 - t8) + " iterator remove->" + (t10 - t9));
  }

}