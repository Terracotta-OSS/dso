/*
 * All content copyright (c) 2003-2006 Terracotta, Inc., except as may otherwise be noted in a separate copyright notice.  All rights reserved.
 */
package com.tctest.performance.faulting;

import com.tctest.TransparentTestBase;
import com.tctest.TransparentTestIface;

public abstract class SingleOswegoQueueFaultBase extends TransparentTestBase {

  private static final int TIMEOUT    = 30 * 60 * 1000; // 30min;

  public void doSetUp(TransparentTestIface t) throws Exception {
    t.getTransparentAppConfig().setMutatorCount(nodeCount());
    t.getTransparentAppConfig().setIntensity(1);
    t.getRunnerConfig().setExecutionTimeout(TIMEOUT);
    t.initializeTestRunner();
  }

  protected Class getApplicationClass() {
    return SingleOswegoQueueFaultTestApp.class;
  }

  protected abstract int nodeCount();
}
