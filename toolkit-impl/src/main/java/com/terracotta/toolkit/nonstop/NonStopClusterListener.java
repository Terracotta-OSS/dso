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
package com.terracotta.toolkit.nonstop;

import org.terracotta.toolkit.cluster.ClusterEvent;
import org.terracotta.toolkit.cluster.ClusterListener;

import com.tc.abortable.AbortableOperationManager;
import com.terracotta.toolkit.NonStopClusterInfo;
import com.terracotta.toolkit.abortable.ToolkitAbortableOperationException;

public class NonStopClusterListener implements ClusterListener {
  private static final long               WAIT_TIMEOUT     = 1000;
  private final NonStopClusterInfo        nonStopClusterInfo;
  private final AbortableOperationManager abortableOperationManager;
  private volatile String                 nodeErrorMessage  = null;

  public NonStopClusterListener(AbortableOperationManager abortableOperationManager,
                                NonStopClusterInfo nonStopClusterInfo) {
    this.abortableOperationManager = abortableOperationManager;
    this.nonStopClusterInfo = nonStopClusterInfo;
    this.nonStopClusterInfo.addClusterListener(this);
  }

  @Override
  public void onClusterEvent(ClusterEvent event) {
    switch (event.getType()) {
      case OPERATIONS_ENABLED:
        synchronized (this) {
          this.notifyAll();
        }
        break;
      case NODE_ERROR:
        nodeErrorMessage = event.getDetailedMessage();
        synchronized (this) {
          this.notifyAll();
        }
        break;
      default:
        // no op
        break;
    }
  }

  /**
   * returns true If cluster Operations are enabled.
   * 
   */
  public boolean areOperationsEnabled() {
    return nonStopClusterInfo.areOperationsEnabled();
  }

  /**
   * waits until cluster Operations are enabled.
   * 
   */
  public void waitUntilOperationsEnabled() {
    if (nodeErrorMessage != null) { throw new ToolkitAbortableOperationException(); }
    
    if (!areOperationsEnabled()) {
      synchronized (this) {
        boolean interrupted = false;
        try {
          while (!areOperationsEnabled()) {
            if (nodeErrorMessage != null) { throw new ToolkitAbortableOperationException(); }
            try {
              this.wait(WAIT_TIMEOUT);
            } catch (InterruptedException e) {
              if (abortableOperationManager.isAborted()) { throw new ToolkitAbortableOperationException(); }
              interrupted = true;
            }
          }
        } finally {
          if (interrupted) {
            Thread.currentThread().interrupt();
          }
        }
      }
    }
  }

  /**
   * @return the error message incase the node is in unrecoverable error state or null if there is no node error.
   */
  public String getNodeErrorMessage() {
    return nodeErrorMessage;
  }

  /**
   * return true of the current node is in unrecoverable error state.
   */
  public boolean isNodeError() {
    return (nodeErrorMessage != null) ? true : false;
  }
}
