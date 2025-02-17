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
package com.tc.objectserver.dgc.api;

import com.tc.object.ObjectID;
import com.tc.objectserver.context.DGCResultContext;
import com.tc.text.PrettyPrintable;
import com.tc.util.State;
import com.tc.util.concurrent.LifeCycleState;

public interface GarbageCollector extends PrettyPrintable {

  public static enum GCType {
    FULL_GC, INLINE_CLEANUP_GC
  }

  public static final State GC_DISABLED = new State("GC_DISABLED");
  public static final State GC_RUNNING  = new State("GC_RUNNING");
  public static final State GC_SLEEP    = new State("GC_SLEEP");
  public static final State GC_PAUSING  = new State("GC_PAUSING");
  public static final State GC_PAUSED   = new State("GC_PAUSED");
  public static final State GC_DELETE   = new State("GC_DELETE");

  public boolean requestGCStart();

  /**
   * Used by inline GC to delete objects.
   */
  public void waitToStartInlineGC();

  public void waitToStartGC();

  public void enableGC();

  public void waitToDisableGC();

  public boolean requestDisableGC();

  public boolean isDisabled();

  public boolean isPausingOrPaused();

  public boolean isPaused();

  public boolean isDelete();

  /**
   * Called by object manager. Notifies the garbage collector that it's ok to perform GC.
   */
  public void notifyReadyToGC();

  /**
   * Request to pause when the system state stabilizes
   */
  public void requestGCPause();

  public boolean requestGCDeleteStart();

  /**
   * Called by the GC thread. Notifies the garbage collector that GC is complete.
   */
  public void notifyGCComplete();

  public void changed(ObjectID changedObject, ObjectID oldReference, ObjectID newReference);

  public void doGC(GCType type);

  /**
   * This method is called when the server transitions from PASSIVE to ACTIVE
   */
  public void start();

  public void stop();

  public boolean isStarted();

  public void setPeriodicEnabled(final boolean periodicEnabled);

  public boolean isPeriodicEnabled();

  public void setState(LifeCycleState st);

  public void addListener(GarbageCollectorEventListener listener);

  public void deleteGarbage(DGCResultContext resultContext);
}