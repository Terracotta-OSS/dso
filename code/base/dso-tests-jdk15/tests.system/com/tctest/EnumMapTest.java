/*
 * All content copyright (c) 2003-2007 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tctest;

public final class EnumMapTest extends TransparentTestBase {

  private static final int NODE_COUNT = EnumMapTestApp.Fruit.values().length;

  public void doSetUp(final TransparentTestIface tt) throws Exception {
    tt.getTransparentAppConfig().setMutatorCount(NODE_COUNT);
    tt.initializeTestRunner();
  }

  protected Class getApplicationClass() {
    return EnumMapTestApp.class;
  }

}
