/*
 * All content copyright (c) 2003-2006 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package com.tctest.performance.timedtask;

import com.tctest.TransparentTestBase;
import com.tctest.TransparentTestIface;
import com.tctest.performance.timedtask.TimedObjectCreationTestApp;

public class TimedObjectCreation extends TransparentTestBase {

  private static final int NODE_COUNT = 1;
  private static final int TIMEOUT    = 30 * 60 * 1000; // 30min;

  public void doSetUp(TransparentTestIface t) throws Exception {
    t.getTransparentAppConfig().setMutatorCount(NODE_COUNT);
    t.getRunnerConfig().setExecutionTimeout(TIMEOUT);
    t.initializeTestRunner();
  }

  protected Class getApplicationClass() {
    return TimedObjectCreationTestApp.class;
  }
}
