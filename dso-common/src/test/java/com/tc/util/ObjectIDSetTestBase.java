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
package com.tc.util;

import org.junit.Test;

import com.tc.bytes.TCByteBuffer;
import com.tc.exception.TCRuntimeException;
import com.tc.io.TCByteBufferInput;
import com.tc.io.TCByteBufferInputStream;
import com.tc.io.TCByteBufferOutput;
import com.tc.io.TCByteBufferOutputStream;
import com.tc.object.ObjectID;
import com.tc.test.TCTestCase;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class ObjectIDSetTestBase {

  protected abstract ObjectIDSet create();

  protected abstract ObjectIDSet create(Collection<ObjectID> copy);

  @Test
  public void testObjectIDSetConcurrentIteratorRemove() throws Exception {
    ObjectIDSet set = create();
    set.add(new ObjectID(0));

    Iterator<ObjectID> iterator = set.iterator();

    for (Iterator<?> it = set.iterator(); it.hasNext(); it.next(), it.remove());

    try {
      while (iterator.hasNext()) {
        iterator.next();
      }
    } catch (ConcurrentModificationException e) {
      //acceptable
    }
  }

  @Test
  public void testContain() {
    final ObjectIDSet set = create();
    final HashSet<ObjectID> hashSet = new HashSet<ObjectID>();

    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("testContain : Seed for Random is " + seed);
    final Random r = new Random(seed);

    for (int i = 0; i < 100000; i++) {
      final ObjectID oid = new ObjectID(r.nextLong());
      set.add(oid);
      hashSet.add(oid);
    }

    for (final ObjectID oid : hashSet) {
      Assert.assertTrue(set.contains(oid));
    }
  }

  @Test
  public void testFailingAddAll() {
    ObjectIDSet oidSet1 = create();
    HashSet hashSet = new HashSet();
    long failedIDs[] = new long[] { 1884, 1371, 595, 440, 730, 1382, 1781, 217, 1449, 1043, 1556, 1679, 347, 860, 1020,
        1619, 1801, 1146, 769, 19, 532, 655, 692, 1268, 1793, 1533, 1616, 1702, 1241, 1754, 633, 1192, 166, 1312, 179,
        945, 44, 755, 1390, 1070, 431, 293, 1319, 339, 852, 103, 141, 874, 1643, 592, 1477, 242, 1165, 777, 953, 1580,
        554, 866, 1441, 1520, 507, 807, 301, 1327, 609, 1098, 369, 364, 1182, 1062, 36, 714, 74, 1228, 453, 399, 1275,
        581, 1684, 325, 897, 1929, 1566, 1324, 1401, 475, 1846, 298, 1423, 660, 564, 977, 1503, 408, 1625, 1569, 148,
        1054, 1334, 1646, 605, 1198, 336, 249, 13, 392, 1135, 1235, 231, 131, 1037, 76, 1586, 809, 1858, 985, 1345,
        186, 526, 784, 838, 1253, 1542, 485, 998, 620, 1096, 910, 1416, 5, 1839, 958, 557, 239, 164, 241, 1898, 1728,
        113, 1029, 1772, 383, 675, 518, 698, 1261, 1764, 1143, 105, 1379, 68, 639, 918, 801, 1206, 1511, 1594, 1149,
        792, 1917, 493, 1006, 54, 1080, 172, 1791, 320, 1267, 757, 746, 1316, 1854, 290, 572, 683, 467, 1298, 1535,
        631, 738, 690, 341, 1342, 1649, 1495, 1361, 123, 832, 1216, 1432, 1705, 446, 891, 1640, 808, 1812, 851, 1530,
        650, 1537, 218, 367, 1088, 636, 1244, 1452, 1893, 266, 1023, 1782, 22, 178, 1166, 535, 1794, 296, 1697, 1370,
        575, 437, 1297, 612, 1189, 1128, 510, 422, 786, 1063, 1179, 677, 816, 950, 1292, 668, 1442, 1885, 596, 83,
        1522, 432, 1579, 1721, 1802, 357, 865, 1992, 966, 43, 551, 942, 1480, 454, 108, 1668, 153, 794, 1254, 1353,
        1909, 1284, 833, 311, 1694, 1387, 1439, 193, 841, 1867, 1219, 1553, 132, 1488, 462, 661, 35, 1040, 707, 824,
        1733, 974, 1550, 376, 1615, 733, 1820, 589, 1759, 100, 1676, 258, 1015, 1457, 1109, 1622, 859, 1101, 413, 1048,
        140, 30, 607, 1561, 201, 352, 543, 928, 1227, 715, 287, 1174, 1687, 502, 1305, 881, 1741, 400, 1331, 621, 819,
        1855, 328, 1171, 645, 91, 1093, 1053, 1545, 389, 1404, 776, 990, 1278, 1916, 1396, 1606, 902, 1504, 478, 1587,
        51, 210, 185, 1236, 722, 1262, 1671, 1748, 1775, 335, 250, 1859, 1415, 1388, 461, 75, 1487, 519, 628, 1323,
        1836, 1908, 1710, 762, 1158, 848, 14, 1136, 588, 1197, 1032, 999, 486, 1118, 1144, 1656, 384, 1071, 1584, 1465,
        1380, 67, 234, 701, 747, 913, 674, 161, 344, 1713, 873, 1315, 1828, 1079, 754, 405, 1007, 494, 580, 116, 934,
        421, 1641, 6, 279, 856, 1205, 1664, 1339, 1152, 1901, 319, 693, 1362, 226, 1790, 739, 567, 445, 1514, 1985,
        1087, 682, 1431, 169, 982, 1270, 1874, 59, 1496, 470, 896, 124, 827, 1213, 1595 };

    for (long failedID : failedIDs) {
      ObjectID oid = new ObjectID(failedID, 1);
      oidSet1.add(oid);
      hashSet.add(oid);
    }

    Assert.assertEquals(435, oidSet1.size());
    Assert.assertEquals(hashSet.size(), oidSet1.size());
    Assert.assertEquals(failedIDs.length, oidSet1.size());

    ObjectIDSet oidSet2 = create();
    oidSet2.add(new ObjectID(410, 1));

    boolean added = oidSet1.addAll(oidSet2);
    Assert.assertTrue(added);

    hashSet.addAll(oidSet2);

    Assert.assertEquals(failedIDs.length + 1, oidSet1.size());
    Assert.assertEquals(hashSet, oidSet1);
  }

//  public void testFailingAddAll2() {
//    TCByteBufferOutput thisIDs = new TCByteBufferOutputStream();
//    thisIDs.writeInt(4);
//    thisIDs.writeLong(72057594037928192l);
//    thisIDs.writeInt(1);
//    thisIDs.writeLong(parseLong("10000000000000000000000000000000000000000000000000000000", 2));
//    thisIDs.writeLong(72057594037928768l);
//
//    long thisIDs[] = new long[] { 72057594037928192l,
//        parseLong("10000000000000000000000000000000000000000000000000000000", 2), 72057594037928768l,
//        parseLong("10", 2), 72057594037929216l, Long.parseLong("10000", 2), 72057594037929600l,
//        parseLong("1000000000000000000000000000000", 2) };
//    long otherIDs[] = new long[] { 72057594037927936l,
//        parseLong("1111000011110000101101001111001011110000111100101101001011011", 2), 72057594037928000l,
//        parseLong("1111000010110100101001011011010010101101100001111000111100001111", 2), 72057594037928064l,
//        parseLong("101101101111001010010110110101101001000111010011111000011110000", 2), 72057594037928128l,
//        parseLong("1010010110100100000111100001111001011010000111100111101001011010", 2), 72057594037928192l,
//        parseLong("1111000001110001011000000110000111100001111010011110000111100101", 2), 72057594037928256l,
//        parseLong("111100011110010110100000111101011011010110101111000101011000", 2), 72057594037928320l,
//        parseLong("1010011110000110100001011010011010110110101101010001111010110101", 2), 72057594037928384l,
//        parseLong("1111001011110010110100101101111100001111000011110010110100001", 2), 72057594037928448l,
//        parseLong("10010110100100000011010010110010111100001111010011010010110100", 2), 72057594037928512l,
//        parseLong("100101001011011111000010100101110001111000011110111101000011111", 2), 72057594037928576l,
//        parseLong("1111000011110010101101001101010011000000111000101101001111110000", 2), 72057594037928640l,
//        parseLong("111100001111010010110100101100101111001001001011110010111100", 2), 72057594037928704l,
//        parseLong("101101111111010111000001110000110000010110000101101001111010011", 2), 72057594037928768l,
//        parseLong("1010010000001110001111100011110001111001101101011010011010010100", 2), 72057594037928832l,
//        parseLong("111100001011000010110100111100101101000011110000110101101001", 2), 72057594037928896l,
//        parseLong("11110010111100101101001011010011010000111100001111010010010011", 2), 72057594037928960l,
//        parseLong("1111000011110000111110000111100000111011010110110100101101001010", 2), 72057594037929024l,
//        parseLong("1000111100001111000111100001111111000010101101001011011010110100", 2), 72057594037929088l,
//        parseLong("111100100100101101001011000011111100001111010011010010111100001", 2), 72057594037929152l,
//        parseLong("1101001001010110010110100001101110000110100101101001111000011110", 2), 72057594037929216l,
//        parseLong("10110110101101100011111000111111100011110000011010010111100100", 2), 72057594037929280l,
//        parseLong("111100101111001111110010111000001111000001111010100100000111001", 2), 72057594037929344l,
//        parseLong("1000011110100111100001101101101010010110100101100001011010010110", 2), 72057594037929408l,
//        parseLong("110100100101001111011010010100101101001011010011111100001101001", 2), 72057594037929472l,
//        parseLong("1111100001111001111100001111000010010110101101100111011010010101", 2), 72057594037929536l,
//        parseLong("11110100101101000111100011110100101001011010011000111101101000", 2), 72057594037929600l,
//        parseLong("1101000011010010111100001111000010110111110000111101001111000011", 2), 72057594037929664l,
//        parseLong("111100000111101101001000010010110101101001111010010111100101100", 2), 72057594037929728l,
//        parseLong("1000011100000111010110110101101101011011110010011100001110000010", 2), 72057594037929792l,
//        parseLong("1101001011010000111100001101001011010010101101001011100001111001", 2), 72057594037929856l,
//        parseLong("10110101001111010010110100101101001011010010110100100100101100", 2), 72057594037929920l,
//        parseLong("1111000011010011", 2) };
//
//    ObjectIDSet oidSet = createBitSetObjectFrom(thisIDs);
//    ObjectIDSet other = createBitSetObjectFrom(otherIDs);
//
//    HashSet oidHashSet = new HashSet(oidSet);
//    HashSet otherHashSet = new HashSet(other);
//
//    oidSet.addAll(other);
//    oidHashSet.addAll(otherHashSet);
//
//    Assert.assertEquals(oidSet.size(), oidHashSet.size());
//    Assert.assertEquals(oidSet, oidHashSet);
//  }

  private long parseLong(String longStr, int base) {
    if (longStr.length() < 64) {
      return Long.parseLong(longStr, base);
    } else {
      long l = Long.parseLong(longStr.substring(1), base);
      long highBit = 1L << 63;
      l |= highBit;
      Assert.assertEquals(longStr, Long.toBinaryString(l));
      return l;
    }
  }

  private ObjectIDSet createBitSetObjectFrom(long[] ids) {
    ObjectIDSet oidSet = new BitSetObjectIDSet();
    assertEquals(0, ids.length % 2);
    int size = getSizeFromBitSetIDs(ids);
    TCByteBufferOutputStream stream = new TCByteBufferOutputStream();
    stream.writeInt(size);
    for (long id : ids) {
      stream.writeLong(id);
    }
    stream.close();
    TCByteBuffer[] data = stream.toArray();
    TCByteBufferInputStream input = new TCByteBufferInputStream(data);
    try {
      oidSet.deserializeFrom(input);
    } catch (IOException e) {
      throw new TCRuntimeException(e);
    }
    return oidSet;
  }

  private int getSizeFromBitSetIDs(long[] ids) {
    int size = 0;
    for (int i = 1; i < ids.length; i += 2) {
      size += Long.bitCount(ids[i]);
    }
    return size;
  }

  @Test
  public void testIterator() {
    final ObjectIDSet set = create();
    final TreeSet<ObjectID> treeSet = new TreeSet<ObjectID>();

    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("testIterator : Seed for Random is " + seed);
    final Random r = new Random(seed);

    for (int i = 0; i < 100000; i++) {
      final ObjectID oid = new ObjectID(r.nextLong());
      set.add(oid);
      treeSet.add(oid);
    }

    Assert.assertEquals(treeSet.size(), set.size());

    final Iterator<ObjectID> tsIterator = treeSet.iterator();
    final Iterator<ObjectID> expandingBitSetIterator = set.iterator();

    while (tsIterator.hasNext()) {
      final ObjectID oid = tsIterator.next();
      Assert.assertEquals(oid.toLong(), expandingBitSetIterator.next().toLong());
    }

    Assert.assertFalse(expandingBitSetIterator.hasNext());
  }

  public void testNegativeIds() {
    final ObjectIDSet set = create();
    final TreeSet<ObjectID> treeSet = new TreeSet<ObjectID>();

    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("testNegativeIds : Seed for Random is " + seed);
    final Random r = new Random(seed);

    for (int i = 0; i < 100000; i++) {
      final ObjectID oid = new ObjectID(r.nextLong());
      set.add(oid);
      treeSet.add(oid);
    }

    Assert.assertEquals(treeSet, set);

    for (int i = 0; i < 100000; i++) {
      final ObjectID oid = new ObjectID(r.nextLong());
      set.remove(oid);
      treeSet.remove(oid);
    }

    Assert.assertEquals(treeSet, set);

    for (int i = 0; i < 1000000; i++) {
      final ObjectID oid = new ObjectID(r.nextLong());
      Assert.assertEquals(treeSet.contains(oid), set.contains(oid));
    }
  }

  @Test
  public void testFirstAndLast() {
    final ObjectIDSet set = create();
    final TreeSet<ObjectID> treeSet = new TreeSet<ObjectID>();

    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("testFirstAndLast : Seed for Random is " + seed);
    final Random r = new Random(seed);

    for (int i = 0; i < 10000; i++) {
      final ObjectID oid = new ObjectID(r.nextLong());
      set.add(oid);
      treeSet.add(oid);
    }

    Assert.assertEquals(treeSet.first(), set.first());

    Assert.assertEquals(treeSet.last(), set.last());
  }

  @Test
  public void testRemove() {
    ObjectIDSet set = create();
    set.add(new ObjectID(10));
    set.add(new ObjectID(14));
    set.add(new ObjectID(1));
    set.add(new ObjectID(18));
    set.add(new ObjectID(75));
    set.add(new ObjectID(68));
    set.add(new ObjectID(175));
    set.add(new ObjectID(205));

    // data : [ Range(0,1000100010000000010) Range(64,100000010000)
    // Range(128,100000000000000000000000000000000000000000000000) Range(192,10000000000000)]
    // ids: 1, 10, 14, 18, 68, 75, 175. 205

    final Iterator<ObjectID> iterator = set.iterator();
    iterateElements(iterator, 4);
    iterator.remove();
    Assert.assertEquals(68, iterator.next().toLong());

    iterateElements(iterator, 1);
    iterator.remove();
    Assert.assertEquals(175, iterator.next().toLong());
    iterator.remove();
    Assert.assertEquals(205, iterator.next().toLong());
    Assert.assertFalse(iterator.hasNext());

    // testing random removes

    set = new BitSetObjectIDSet();
    final HashSet<ObjectID> hashSet = new HashSet<ObjectID>();

    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("testRemove : Seed for Random is " + seed);
    final Random r = new Random(seed);

    for (int i = 0; i < 10000; i++) {
      final ObjectID oid = new ObjectID(r.nextLong());
      set.add(oid);
      hashSet.add(oid);
    }

    Assert.assertEquals(hashSet, set);

    for (int i = 0; i < 10000; i++) {
      final ObjectID oid = new ObjectID(r.nextLong());
      set.remove(oid);
      hashSet.remove(oid);
    }

    Assert.assertEquals(hashSet, set);
  }

  @Test
  public void testRemoveAll() {
    for (int i = 0; i < 10; i++) {
      long seed = new SecureRandom().nextLong();
      System.out.println("Testing with seed " + seed);
      timeAndTestRemoveAll(seed);
    }
  }

  private void timeAndTestRemoveAll(long seed) {
    final TreeSet expected = new TreeSet();
    final TreeSet big = new TreeSet();
    final TreeSet small = new TreeSet();

    ObjectIDSet set = create();
    final Random r = new Random(seed);

    for (int i = 0; i < 1000000; i++) {
      final long l = r.nextInt(55555555);
      final ObjectID id = new ObjectID(l);
      if (i % 2 == 0) {
        // 500,0000
        big.add(id);
      }
      if (i % 3 == 0) {
        // 333,000
        set.add((id));
        expected.add(id);
      }
      if (i % 100 == 0) {
        small.add(id);
      }
    }

    final long t1 = System.currentTimeMillis();
    set.removeAll(small);
    final long t2 = System.currentTimeMillis();
    expected.removeAll(small);
    final long t3 = System.currentTimeMillis();
    assertEquals(expected, set);

    final long t4 = System.currentTimeMillis();
    set.removeAll(big);
    final long t5 = System.currentTimeMillis();
    expected.removeAll(big);
    final long t6 = System.currentTimeMillis();
    assertEquals(expected, set);

    System.out.println("Time taken for removeAll "+ set.getClass().getSimpleName() + ":HashSet -> " + (t2 - t1) + ":"
                       + (t3 - t2) + " millis  for small collection, " + (t5 - t4) + ":"
                       + (t6 - t5) + " millis for large collection");
  }

  @Test
  public void testSortedSetObjectIDSet() throws Exception {
    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("SORTED TEST : Seed for Random is " + seed);
    final Random r = new Random(seed);
    final TreeSet ts = new TreeSet();
    final SortedSet expandingBitSetBased = create();
    for (int i = 0; i < 10000; i++) {
      final long l = r.nextLong();
      // if (l < 0) {
      // l = -l;
      // }
      final ObjectID id = new ObjectID(l);
      final boolean b1 = ts.add(id);
      final boolean b4 = expandingBitSetBased.add(id);
      assertEquals(b1, b4);
      assertEquals(ts.size(), expandingBitSetBased.size());
    }

    // verify sorted
    Iterator<ObjectID> i = ts.iterator();
    Iterator<ObjectID> expandingBitSetIterator = expandingBitSetBased.iterator();
    while (i.hasNext()) {
      ObjectID reference = i.next();
      ObjectID oid3 = expandingBitSetIterator.next();
      assertEquals(reference, oid3);
    }
  }

  public void basicTest(final int distRange, final int iterationCount) {
    final long test_start = System.currentTimeMillis();
    final Set<ObjectID> s = new HashSet<ObjectID>();
    final Set<ObjectID> small = create();
    final String cname = small.getClass().getName();
    System.err.println("Running tests for " + cname + " distRange = " + distRange + " iterationCount = "
                       + iterationCount);
    assertTrue(small.isEmpty());
    assertTrue(small.size() == 0);
    final SecureRandom sr = new SecureRandom();
//    final long seed = sr.nextLong();
    long seed = 8320691879158973228L;
    System.err.println("Seed for Random is " + seed);
    final Random r = new Random(seed);
    for (int i = 0; i < iterationCount; i++) {
      final long l = r.nextInt(distRange);
      final ObjectID id = new ObjectID(l);
      s.add(id);
      small.add(id);
      assertEquals(s.size(), small.size());
    }
    final Iterator sit = small.iterator();
    final List<ObjectID> all = new ArrayList<ObjectID>();
    all.addAll(s);
    while (sit.hasNext()) {
      final ObjectID i = (ObjectID) sit.next();
      Assert.eval("FAILED:" + i.toString(), s.remove(i));
    }
    Assert.eval(s.size() == 0);

    // test retain all
    final Set<ObjectID> odds = new HashSet<ObjectID>();
    final Set<ObjectID> evens = new HashSet<ObjectID>();
    for (int i = 0; i < all.size(); i++) {
      if (i % 2 == 0) {
        evens.add(all.get(i));
      } else {
        odds.add(all.get(i));
      }
    }

    boolean b = small.retainAll(odds);
    assertTrue(b);
    Assert.assertEquals(odds.size(), small.size());
    assertEquals(odds, small);
    b = small.retainAll(evens);
    assertTrue(b);
    assertEquals(0, small.size());
    small.addAll(all); // back to original state

    // test new set creation (which uses cloning
    long start = System.currentTimeMillis();
    final Set<ObjectID> copy = create(all);
    System.err.println("Time to add all IDs from a collection to a new " + cname + " = "
                       + (System.currentTimeMillis() - start) + " ms");
    start = System.currentTimeMillis();
    final Set clone = create(small);
    System.err.println("Time to add all IDs from an ObjectIDSet to a new " + cname + " = "
                       + (System.currentTimeMillis() - start) + " ms");

    Collections.shuffle(all);
    for (final ObjectID rid : all) {
      Assert.eval(small.contains(rid));
      Assert.eval(clone.contains(rid));
      Assert.eval(copy.contains(rid));
      if (!small.remove(rid)) { throw new AssertionError("couldn't remove:" + rid); }
      if (small.contains(rid)) { throw new AssertionError(rid); }
      if (!clone.remove(rid)) {
        throw new AssertionError("couldn't remove:" + rid);
      }
      if (clone.contains(rid)) { throw new AssertionError(rid); }
      if (!copy.remove(rid)) { throw new AssertionError("couldn't remove:" + rid); }
      if (copy.contains(rid)) { throw new AssertionError(rid); }
    }
    for (final ObjectID rid : all) {
      Assert.eval(!small.contains(rid));
      if (small.remove(rid)) { throw new AssertionError("shouldn't have removed:" + rid); }
      if (small.contains(rid)) { throw new AssertionError(rid); }
      if (clone.remove(rid)) { throw new AssertionError("shouldn't have removed:" + rid); }
      if (clone.contains(rid)) { throw new AssertionError(rid); }
      if (copy.remove(rid)) { throw new AssertionError("shouldn't have removed:" + rid); }
      if (copy.contains(rid)) { throw new AssertionError(rid); }
    }
    Assert.eval(s.size() == 0);
    Assert.eval(small.size() == 0);
    Assert.eval(copy.size() == 0);
    Assert.eval(clone.size() == 0);
    System.err.println("Time taken to run basic Test for " + small.getClass().getName() + " is "
                       + (System.currentTimeMillis() - test_start) + " ms");
  }

  @Test
  public void testSerializationObjectIDSet2() throws Exception {
    for (int i = 0; i < 20; i++) {
      final Set<ObjectID> s = createRandomSetOfObjectIDs();
      serializeAndVerify(s);
    }
  }

  private void serializeAndVerify(final Set<ObjectID> s) throws Exception {
    final ObjectIDSet org = create(s);
    assertEquals(s, org);

    final ObjectIDSet ser = serializeAndRead(org);
    assertEquals(s, ser);
    assertEquals(org, ser);
  }

  private ObjectIDSet serializeAndRead(final ObjectIDSet org) throws Exception {
    final TCByteBufferOutputStream out = new TCByteBufferOutputStream();
    org.serializeTo(out);
    System.err.println("Written ObjectIDSet2 size : " + org.size());
    final TCByteBufferInputStream in = new TCByteBufferInputStream(out.toArray());
    final ObjectIDSet oids = create();
    oids.deserializeFrom(in);
    System.err.println("Read  ObjectIDSet2 size : " + oids.size());
    return oids;
  }

  private Set createRandomSetOfObjectIDs() {
    final Set s = new HashSet();
    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("Random Set creation : Seed for Random is " + seed);
    final Random r = new Random(seed);
    for (int i = 0; i < r.nextLong(); i++) {
      s.add(new ObjectID(r.nextLong()));
    }
    System.err.println("Created a set of size : " + s.size());
    return s;
  }

  @Test
  public void testObjectIDSet() {
//    basicTest(100000, 100000, ObjectIDSetType.RANGE_BASED_SET);
//    basicTest(500000, 100000, ObjectIDSetType.RANGE_BASED_SET);
//    basicTest(100000, 1000000, ObjectIDSetType.RANGE_BASED_SET);
//
//    basicTest(100000, 100000, ObjectIDSetType.BITSET_BASED_SET);
//    basicTest(500000, 100000, ObjectIDSetType.BITSET_BASED_SET);
//    basicTest(100000, 1000000, ObjectIDSetType.BITSET_BASED_SET);

    basicTest(100000, 100000);
    basicTest(500000, 100000);
    basicTest(100000, 1000000);
  }

  @Test
  public void testObjectIDSetDump() {
    final ObjectIDSet set = create();

    System.err.println(" toString() : " + set);

    for (int i = 0; i < 100; i++) {
      set.add(new ObjectID(i));
    }
    System.err.println(" toString() : " + set);

    for (int i = 0; i < 100; i += 2) {
      set.remove(new ObjectID(i));
    }
    System.err.println(" toString() : " + set);
  }

  @Test
  public void testObjectIdSetConcurrentModification() {
    ObjectIDSet objIdSet = create();
    int num = 0;
    for (num = 0; num < 50; num++) {
      objIdSet.add(new ObjectID(num));
    }

    Iterator iterator = objIdSet.iterator();
    objIdSet.add(new ObjectID(num));
    try {
      iterateElements(iterator);
      throw new AssertionError("We should have got the ConcurrentModificationException");
    } catch (final ConcurrentModificationException cme) {
      System.out.println("Caught Expected Exception " + cme.getClass().getName());
    }

    iterator = objIdSet.iterator();
    objIdSet.remove(new ObjectID(0));
    try {
      iterateElements(iterator);
      throw new AssertionError("We should have got the ConcurrentModificationException");
    } catch (final ConcurrentModificationException cme) {
      System.out.println("Caught Expected Exception " + cme.getClass().getName());
    }

    iterator = objIdSet.iterator();
    objIdSet.clear();
    try {
      iterateElements(iterator);
      throw new AssertionError("We should have got the ConcurrentModificationException");
    } catch (final ConcurrentModificationException cme) {
      System.out.println("Caught Expected Exception " + cme.getClass().getName());
    }
  }

  private long iterateElements(final Iterator iterator) throws ConcurrentModificationException {
    return iterateElements(iterator, -1);
  }

  private long iterateElements(final Iterator iterator, final long count) throws ConcurrentModificationException {
    long itrCount = 0;
    while ((iterator.hasNext()) && (count < 0 || itrCount < count)) {
      itrCount++;
      System.out.print(((ObjectID) iterator.next()).toLong() + ", ");
    }
    System.out.print("\n\n");
    return itrCount;
  }

  @Test
  public void testObjectIDSetIteratorFullRemove() {
    ObjectIDSet oidSet = create();
    final Set<ObjectID> all = new TreeSet<ObjectID>();
    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("Running iteratorRemoveTest for " + oidSet.getClass().getName() + " and seed is " + seed);
    final Random r = new Random(seed);
    for (int i = 0; i < 5000; i++) {
      final long l = r.nextLong();
      final ObjectID id = new ObjectID(l);
      all.add(id);
      oidSet.add(id);
    }

    Assert.assertEquals(all.size(), oidSet.size());
    for (final ObjectID rid : all) {
      Assert.eval(oidSet.contains(rid));
      for (final Iterator j = oidSet.iterator(); j.hasNext(); ) {
        final ObjectID crid = (ObjectID)j.next();
        if (crid.equals(rid)) {
          j.remove();
          break;
        }
      }
    }
    Assert.assertEquals(oidSet.size(), 0);
  }

  @Test
  public void testObjectIDSetIteratorSparseRemove() {
    ObjectIDSet oidSet = create();
    // TreeSet<ObjectID> ts = new TreeSet<ObjectID>();
    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("Running iteratorRemoveTest for " + oidSet.getClass().getName() + " and seed is " + seed);
    final Random r = new Random(seed);
    for (int i = 0; i < 1000; i++) {
      ObjectID id;
      do {
        final long l = r.nextLong();
        id = new ObjectID(l);
      } while (oidSet.contains(id));
      // ts.add(id);
      oidSet.add(id);
    }

    System.out.println(oidSet + "\n\n");
    // check if ObjectIDSet has been inited with 1000 elements
    Iterator oidSetIterator = oidSet.iterator();
    assertEquals(1000, iterateElements(oidSetIterator));

    long visitedCount = 0;
    long removedCount = 0;
    oidSetIterator = oidSet.iterator();

    // visit first 100 elements
    visitedCount += iterateElements(oidSetIterator, 100);
    assertEquals(100, visitedCount);

    // remove the 100th element
    oidSetIterator.remove();
    removedCount += 1;

    // visit next 100 elements
    visitedCount += iterateElements(oidSetIterator, 100);
    assertEquals(100 + 100, visitedCount);

    // remove the 200th element
    oidSetIterator.remove();
    removedCount += 1;

    // visit next 100 elements
    visitedCount += iterateElements(oidSetIterator, 100);
    assertEquals(100 + 100 + 100, visitedCount);

    // visit rest of the elements
    visitedCount += iterateElements(oidSetIterator);
    assertEquals(1000, visitedCount);

    // check the backing Set for removed elements
    oidSetIterator = oidSet.iterator();
    final long totalElements = iterateElements(oidSetIterator);
    assertEquals((visitedCount - removedCount), totalElements);
  }

  @Test
  public void testObjectIDSetIteratorRemoveSpecailCases() {
    final List<ObjectID> longList = new ArrayList<ObjectID>();
    longList.add(new ObjectID(25));
    longList.add(new ObjectID(26));
    longList.add(new ObjectID(27));
    longList.add(new ObjectID(28));
    longList.add(new ObjectID(9));
    longList.add(new ObjectID(13));
    longList.add(new ObjectID(12));
    longList.add(new ObjectID(14));
    longList.add(new ObjectID(18));
    longList.add(new ObjectID(2));
    longList.add(new ObjectID(23));
    longList.add(new ObjectID(47));
    longList.add(new ObjectID(35));
    longList.add(new ObjectID(10));
    longList.add(new ObjectID(1));
    longList.add(new ObjectID(4));
    longList.add(new ObjectID(15));
    longList.add(new ObjectID(8));
    longList.add(new ObjectID(56));
    longList.add(new ObjectID(11));
    longList.add(new ObjectID(10));
    longList.add(new ObjectID(33));
    longList.add(new ObjectID(17));
    longList.add(new ObjectID(29));
    longList.add(new ObjectID(19));
    // Data : 1 2 4 8 9 10 11 12 13 14 15 17 18 19 23 25 26 27 28 29 33 35 47 56

    /**
     * ObjectIDSet { (oids:ranges) = 24:10 , compression ratio = 1.0 } [ Range(1,2) Range(4,4) Range(8,15) Range(17,19)
     * Range(23,23) Range(25,29) Range(33,33) Range(35,35) Range(47,47) Range(56,56)]
     */

    final int totalElements = longList.size() - 1;

    oidSetIteratorRemoveSpecialCasesTest(totalElements, create(longList));
  }

  private void oidSetIteratorRemoveSpecialCasesTest(final int totalElements, final Set<ObjectID> objectIDSet)
      throws AssertionError {
    Iterator<ObjectID> i = objectIDSet.iterator();
    assertEquals(totalElements, iterateElements(i));

    final List<ObjectID> longSortList = new ArrayList<ObjectID>();
    i = objectIDSet.iterator();
    while (i.hasNext()) {
      longSortList.add(i.next());
    }

    // remove first element in a range. eg: 8 from (8,15)
    removeElementFromIterator(objectIDSet.iterator(), totalElements, longSortList.indexOf(new ObjectID(8)) + 1, 9);
    objectIDSet.add(new ObjectID(8)); // get back to original state

    // remove last element in a range. eg: 19 from (17,19)
    removeElementFromIterator(objectIDSet.iterator(), totalElements, longSortList.indexOf(new ObjectID(19)) + 1, 23);
    objectIDSet.add(new ObjectID(19));

    // remove the only element in the range. eg: 33 from (33,33)
    removeElementFromIterator(objectIDSet.iterator(), totalElements, longSortList.indexOf(new ObjectID(33)) + 1, 35);
    objectIDSet.add(new ObjectID(33));

    // remove the least element
    removeElementFromIterator(objectIDSet.iterator(), totalElements, longSortList.indexOf(new ObjectID(1)) + 1, 2);
    objectIDSet.add(new ObjectID(1));

    // remove the max element; element will be removed, but while going to next element, exception expected
    try {
      removeElementFromIterator(objectIDSet.iterator(), totalElements, longSortList.indexOf(new ObjectID(56)) + 1, -99);
      throw new AssertionError("Expected to throw an exception");
    } catch (final NoSuchElementException noSE) {
      // expected
    } finally {
      objectIDSet.add(new ObjectID(56));
    }

    // remove the non existing element; exception expected
    try {
      removeElementFromIterator(objectIDSet.iterator(), totalElements, longSortList.indexOf(new ObjectID(16)) + 1, -99);
      throw new AssertionError("Expected to throw an exception");
    } catch (final IllegalStateException ise) {
      // expected
    }

    i = objectIDSet.iterator();
    assertEquals(5, iterateElements(i, 5));
    objectIDSet.add(new ObjectID(99));
    try {
      assertEquals(5, iterateElements(i, 1));
      throw new AssertionError("Expected to throw an exception");
    } catch (final ConcurrentModificationException cme) {
      // expected
    } finally {
      objectIDSet.remove(new ObjectID(99));
    }
  }

  @Test
  public void testAddAll() {
    final int SIZE_MILLION = 1000000;
    final ObjectIDSet set = create();

    // validate addAll
    addToReferencesRandom(set, SIZE_MILLION);
    int randomSize = set.size();

    final ObjectIDSet set2 = create();

    long startTime = System.currentTimeMillis();
    set2.addAll(set);
    long addAllTime = System.currentTimeMillis() - startTime;

    System.out.println("Set.addAll random total time took: " + addAllTime + " ms. ");

    // validate addAll
    assertEquals(randomSize, set2.size());

    for (final ObjectID id : set) {
      assertTrue(set2.contains(id));
    }

    // /do serial
    final ObjectIDSet setSerial = create();
    addToReferencesSerial(setSerial, SIZE_MILLION);

    assertEquals(SIZE_MILLION, setSerial.size());

    startTime = System.currentTimeMillis();
    set2.addAll(setSerial);
    addAllTime = System.currentTimeMillis() - startTime;

    System.out.println("Set.addAll serial total time took: " + addAllTime + " ms. ");

    // validate addAll
    assertEquals(randomSize + SIZE_MILLION, set2.size());

    for (final ObjectID id : setSerial) {
      assertTrue(set2.contains(id));
    }

    // now lets add to serial, and see if the random set exist in it
    setSerial.addAll(set);

    for (ObjectID id : set) {
      assertTrue(setSerial.contains(id));
    }

  }

  @Test
  public void testAddAllPerformance() {
    final int SIZE_10_MILLION = 10000000;
    final ObjectIDSet set = create();
    final ObjectIDSet set2 = create();
    addToReferencesRandom(set, SIZE_10_MILLION);
    addToReferencesSerial(set2, SIZE_10_MILLION);
    int bitSize = set.size();
    int bitSize2 = set2.size();

    long startTime = System.currentTimeMillis();
    set.addAll(set2);
    long addAllTime = System.currentTimeMillis() - startTime;

    System.out.println("Set.addAll performance random total time took: " + addAllTime + " ms. ");
    assertEquals(bitSize + bitSize2, set.size());
  }

  private void addToReferencesSerial(ObjectIDSet set, int size) {
    for (int i = 2 * size; i < size + (2 * size); i++) {
      set.add(new ObjectID(i));
    }
  }

  private void addToReferencesRandom(final ObjectIDSet set, final int size) {
    final SecureRandom sr = new SecureRandom();
    final long seed = sr.nextLong();
    System.err.println("testContain : Seed for Random is " + seed);
    final Random r = new Random(seed);

    for (int i = 0; i < size; i++) {
      set.add(new ObjectID(r.nextInt(size)));
    }
  }

  private void removeElementFromIterator(final Iterator i, final int totalElements, final long indexOfRemoveElement,
                                         final int nextExpectedElement) {
    long visitedElements = 0;
    visitedElements += iterateElements(i, indexOfRemoveElement);
    i.remove();
    assertEquals(nextExpectedElement, ((ObjectID) i.next()).toLong());
    visitedElements += iterateElements(i);
    assertEquals(visitedElements, totalElements - 1);
  }

  @Test
  public void testSerializeToBasicObjectIDSet() throws Exception {
    ObjectIDSet objectIDSet = create();
    Random r = new Random();
    for (int i = 0; i < 10000; i++) {
      objectIDSet.add(new ObjectID(Math.abs(r.nextLong() % 10000)));
    }
    TCByteBufferOutput output = new TCByteBufferOutputStream();
    objectIDSet.serializeTo(output);
    TCByteBufferInput input = new TCByteBufferInputStream(output.toArray());
    ObjectIDSet basicObjectIDSet = (ObjectIDSet) new BasicObjectIDSet().deserializeFrom(input);
    assertThat(objectIDSet, is(basicObjectIDSet));
  }

  @Test
  public void testCloneBasicObjectIDSet() throws Exception {
    ObjectIDSet objectIDSet = create();
    Random r = new Random();
    for (int i = 0; i < 10000; i++) {
      objectIDSet.add(new ObjectID(Math.abs(r.nextLong() % 10000)));
    }
    TCByteBufferOutput output = new TCByteBufferOutputStream();
    objectIDSet.serializeTo(output);
    TCByteBufferInput input = new TCByteBufferInputStream(output.toArray());
    ObjectIDSet other = create((ObjectIDSet)new BasicObjectIDSet().deserializeFrom(input));
    assertThat(objectIDSet, is(other));
  }
}
