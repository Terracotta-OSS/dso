/*
 * Copyright Terracotta, Inc.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
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
package com.terracotta.toolkit;

import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.cluster.ClusterEvent;
import org.terracotta.toolkit.cluster.ClusterInfo;
import org.terracotta.toolkit.cluster.ClusterListener;
import org.terracotta.toolkit.cluster.ClusterNode;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class NonStopClusterInfo implements ClusterInfo {
  private static final TCLogger         LOGGER         = TCLogging.getLogger(NonStopClusterInfo.class);
  private final ToolkitInitializer    toolkitInitializer;
  private volatile ClusterInfo          delegate;
  private final Thread                  initializer;
  private final List<ClusterListener>   listeners      = new ArrayList<ClusterListener>();
  private volatile ClusterEvent         nodeErrorEvent = null;

  public NonStopClusterInfo(ToolkitInitializer toolkitInitializer) {
    this.toolkitInitializer = toolkitInitializer;
    initializer = createThreadToInitDelegate();
    initializer.setDaemon(true);
    initializer.start();
  }

  private Thread createThreadToInitDelegate() {
    return new Thread("NonStopClusterInfo register") {
      @Override
      public void run() {
        try {
          ClusterInfo localClusterInfo = getToolkit().getClusterInfo();
          synchronized (NonStopClusterInfo.this) {
            for (ClusterListener clusterListener : listeners) {
              localClusterInfo.addClusterListener(clusterListener);
            }
            listeners.clear();
            delegate = localClusterInfo;
            if (LOGGER.isDebugEnabled()) {
              LOGGER.debug("NonStopClusterInfo Initialization completed.");
            }
          }
        } catch (final Throwable e) {
          LOGGER.error("Got Exception while initializing Toolkit :" + e);
          // Notify the listeners of the NODE_ERROR event.
          nodeErrorEvent = new ClusterEvent() {

            @Override
            public Type getType() {
              return Type.NODE_ERROR;
            }

            @Override
            public ClusterNode getNode() {
              // return null as toolkit is not yet initialized.
              return null;
            }

            @Override
            public String getDetailedMessage() {
              return e.getMessage();
            }
          };
          synchronized (NonStopClusterInfo.this) {
            for (ClusterListener listener : new ArrayList<ClusterListener>(listeners)) {
              listener.onClusterEvent(nodeErrorEvent);
            }
          }
        }
      }

      private Toolkit getToolkit() {
        return toolkitInitializer.getToolkit();
      }
    };
  }

  @Override
  public void addClusterListener(ClusterListener listener) {
    if (delegate != null) {
      delegate.addClusterListener(listener);
      return;
    }

    synchronized (NonStopClusterInfo.this) {
      if (delegate != null) {
        delegate.addClusterListener(listener);
        return;
      }

      listeners.add(listener);

      if (nodeErrorEvent != null) {
        listener.onClusterEvent(nodeErrorEvent);
      }
    }
  }

  @Override
  public void removeClusterListener(ClusterListener listener) {
    if (delegate != null) {
      delegate.removeClusterListener(listener);
      return;
    }

    synchronized (NonStopClusterInfo.this) {
      if (delegate != null) {
        delegate.removeClusterListener(listener);
        return;
      }

      listeners.remove(listener);
    }
  }

  private void waitForClusterInfoToGetInitialized() {
    boolean interrupted = false;
    while (delegate == null) {
      try {
        initializer.join();
      } catch (InterruptedException e) {
        interrupted = true;
      }
    }

    Util.selfInterruptIfNeeded(interrupted);
  }

  @Override
  public ClusterNode getCurrentNode() {
    if (delegate != null) { return delegate.getCurrentNode(); }

    waitForClusterInfoToGetInitialized();
    return delegate.getCurrentNode();
  }

  @Override
  public boolean areOperationsEnabled() {
    if (delegate == null) { return false; }
    return delegate.areOperationsEnabled();
  }

  @Override
  public Set<ClusterNode> getNodes() {
    if (delegate != null) { return delegate.getNodes(); }

    waitForClusterInfoToGetInitialized();
    return delegate.getNodes();
  }

}
