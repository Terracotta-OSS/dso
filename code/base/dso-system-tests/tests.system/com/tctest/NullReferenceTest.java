/*
 * All content copyright (c) 2003-2006 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package com.tctest;

public class NullReferenceTest extends TransparentTestBase {

  private static final int NODE_COUNT = 5;

  public void setUp() throws Exception {
    super.setUp();
    getTransparentAppConfig().setMutatorCount(NODE_COUNT).setIntensity(1);
    initializeTestRunner();
  }

  protected Class getApplicationClass() {
    return NullReferenceTestApp.class;
  }

}
