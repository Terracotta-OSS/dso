/*
 * All content copyright (c) 2003-2007 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tctest;

public class LockUpgrade1Reads1UpgradesTest extends TransparentTestBase {

  private static final int NODE_COUNT   = 2;
  private static final int THREAD_COUNT = 2;
  
  public LockUpgrade1Reads1UpgradesTest() {
    disableAllUntil("2008-04-10");
  }

  public void doSetUp(TransparentTestIface t) throws Exception {
    t.getTransparentAppConfig().setMutatorCount(NODE_COUNT).setApplicationInstancePerClientCount(THREAD_COUNT);
    t.initializeTestRunner();
  }

  protected Class getApplicationClass() {
    return LockUpgrade1Reads1UpgradesTestApp.class;
  }

  protected boolean canRunCrash() {
    return true;
  }

}
