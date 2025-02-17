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
package com.tc.util.diff;

import com.tc.exception.ImplementMe;
import com.tc.test.EqualityChecker;
import com.tc.test.TCTestCase;
import com.tc.util.Stringifier;
import com.tc.util.TCAssertionError;

/**
 * Unit test for {@link BasicObjectDifference}.
 */
public class BasicObjectDifferenceTest extends TCTestCase {

  private DifferenceContext context;

  @Override
  public void setUp() throws Exception {
    this.context = DifferenceContext.createInitial().sub("a");
  }

  private static class OtherDiff implements Differenceable {
    private final String a;

    public OtherDiff(String a) {
      this.a = a;
    }

    @Override
    public boolean equals(Object that) {
      return (that instanceof OtherDiff) && ((OtherDiff) that).a.equals(this.a);
    }

    @Override
    public void addDifferences(DifferenceContext context, Object that) {
      throw new ImplementMe();
    }
  }

  private static class SubOtherDiff extends OtherDiff {
    public SubOtherDiff(String a) {
      super(a);
    }
  }

  public void testConstruction() throws Exception {
    try {
      new BasicObjectDifference(null, "a", "b");
      fail("Didn't get NPE on no context");
    } catch (NullPointerException npe) {
      // ok
    }

    try {
      new BasicObjectDifference(this.context, null, null);
      fail("Didn't get TCAE on both null");
    } catch (TCAssertionError tcae) {
      // ok
    }

    try {
      new BasicObjectDifference(this.context, "a", "a");
      fail("Didn't get TCAE on both equal");
    } catch (TCAssertionError tcae) {
      // ok
    }

    try {
      OtherDiff diff = new OtherDiff("a");
      OtherDiff diff2 = new OtherDiff("a");

      new BasicObjectDifference(this.context, diff, diff2);
      fail("Didn't get TCAE on equal arguments");
    } catch (TCAssertionError tcae) {
      // ok
    }

    // Both equal, but not same class
    new BasicObjectDifference(this.context, new OtherDiff("a"), new SubOtherDiff("a"));
    // Only one is Differenceable
    new BasicObjectDifference(this.context, new OtherDiff("a"), "a");
    new BasicObjectDifference(this.context, "a", new OtherDiff("a"));
  }

  public void testABAndToString() throws Exception {
    String one = new String("foobar");
    String two = new String("bazbar");

    BasicObjectDifference diff = new BasicObjectDifference(this.context, one, two);
    assertSame(one, diff.a());
    assertSame(two, diff.b());

    diff = new BasicObjectDifference(this.context, null, two);
    assertSame(null, diff.a());
    assertSame(two, diff.b());

    diff = new BasicObjectDifference(this.context, one, null);
    assertSame(one, diff.a());
    assertSame(null, diff.b());

    diff = new BasicObjectDifference(this.context, one, two);
    assertTrue(diff.toString().indexOf(one) >= 0);
    assertTrue(diff.toString().indexOf(two) >= 0);
  }

  public void testUsesStringifier() throws Exception {
    Stringifier myStringifier = new Stringifier() {
      @Override
      public String toString(Object o) {
        return "XX" + o + "YY";
      }
    };

    DifferenceContext theContext = DifferenceContext.createInitial(myStringifier);
    assertSame(myStringifier, theContext.stringifier());

    BasicObjectDifference diff = new BasicObjectDifference(theContext, "a", "b");
    assertTrue(diff.toString().indexOf("XXaYY") >= 0);
    assertTrue(diff.toString().indexOf("XXbYY") >= 0);
  }

  public void testEquals() throws Exception {
    Stringifier myStringifier = new Stringifier() {
      @Override
      public String toString(Object o) {
        return "XX" + o + "YY";
      }
    };

    DifferenceContext theContext = DifferenceContext.createInitial(myStringifier);

    DifferenceContext secondContext = DifferenceContext.createInitial().sub("b");
    DifferenceContext thirdContext = DifferenceContext.createInitial().sub("b");

    BasicObjectDifference[] one = new BasicObjectDifference[] { new BasicObjectDifference(theContext, "a", "b"),
        new BasicObjectDifference(theContext, "a", null), new BasicObjectDifference(theContext, null, "b"),
        new BasicObjectDifference(secondContext, "a", "b"), new BasicObjectDifference(theContext, "c", "d"), };

    BasicObjectDifference[] two = new BasicObjectDifference[] { new BasicObjectDifference(theContext, "a", "b"),
        new BasicObjectDifference(theContext, "a", null), new BasicObjectDifference(theContext, null, "b"),
        new BasicObjectDifference(thirdContext, "a", "b"), new BasicObjectDifference(theContext, "c", "d"), };

    EqualityChecker.checkArraysForEquality(one, two, false);
  }

}