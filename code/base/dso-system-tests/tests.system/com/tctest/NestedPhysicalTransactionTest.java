/*
 * All content copyright (c) 2003-2006 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package com.tctest;

public class NestedPhysicalTransactionTest extends TransparentTestBase implements TestConfigurator {

  private static final int NODE_COUNT = 2;
  
  protected Class getApplicationClass() {
    return NestedPhysicalTransactionTestApp.class;
  }

  public void doSetUp(TransparentTestIface t) throws Exception {
    t.getTransparentAppConfig().setClientCount(NODE_COUNT).setIntensity(1);
    t.initializeTestRunner();
  }

}
