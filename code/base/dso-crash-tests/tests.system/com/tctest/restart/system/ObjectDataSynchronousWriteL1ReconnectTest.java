/*
 * All content copyright (c) 2003-2007 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tctest.restart.system;

import com.tc.test.activepassive.ActivePassiveCrashMode;
import com.tc.test.activepassive.ActivePassivePersistenceMode;
import com.tc.test.activepassive.ActivePassiveSharedDataMode;
import com.tc.test.activepassive.ActivePassiveTestSetupManager;
import com.tctest.TestConfigurator;
import com.tctest.TransparentTestBase;
import com.tctest.TransparentTestIface;

import java.util.HashMap;
import java.util.Map;

public class ObjectDataSynchronousWriteL1ReconnectTest extends TransparentTestBase implements TestConfigurator {

  private int clientCount = 2;

  protected Class getApplicationClass() {
    return ObjectDataTestApp.class;
  }

  protected Map getOptionalAttributes() {
    Map attributes = new HashMap();
    attributes.put(ObjectDataTestApp.SYNCHRONOUS_WRITE, "true");
    return attributes;
  }

  public void doSetUp(TransparentTestIface t) throws Exception {
    t.getTransparentAppConfig().setClientCount(clientCount).setIntensity(1);
    t.initializeTestRunner();
  }

  protected boolean canRunCrash() {
    return true;
  }

  protected boolean canRunActivePassive() {
    return true;
  }
  
  protected boolean enableL1Reconnect() {
    return true;
  }

  public void setupActivePassiveTest(ActivePassiveTestSetupManager setupManager) {
    setupManager.setServerCount(2);
    setupManager.setServerCrashMode(ActivePassiveCrashMode.CONTINUOUS_ACTIVE_CRASH);
    setupManager.setServerCrashWaitTimeInSec(30);
    // leaving this as a disk-based active-passive test just so we have one
    setupManager.setServerShareDataMode(ActivePassiveSharedDataMode.DISK);
    setupManager.setServerPersistenceMode(ActivePassivePersistenceMode.PERMANENT_STORE);
  }

}
