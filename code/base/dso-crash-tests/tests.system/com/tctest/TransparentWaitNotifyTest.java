/*
 * All content copyright (c) 2003-2006 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package com.tctest;

public class TransparentWaitNotifyTest extends TransparentTestBase {
  private static final int NODE_COUNT           = 2;
  private static final int LOOP_ITERATION_COUNT = 1;

  public void doSetUp(TransparentTestIface t) throws Exception {
    t.getTransparentAppConfig().setClientCount(NODE_COUNT).setIntensity(LOOP_ITERATION_COUNT);
    t.initializeTestRunner();
  }

  protected Class getApplicationClass() {
    return TransparentWaitNotifyApp.class;
  }

  protected boolean canRunCrash() {
    return true;
  }

}