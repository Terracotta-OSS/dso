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
package com.terracotta.toolkit.events;

import org.terracotta.toolkit.events.ToolkitNotificationListener;
import org.terracotta.toolkit.events.ToolkitNotifier;
import org.terracotta.toolkit.internal.concurrent.locks.ToolkitLockTypeInternal;

import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.object.LogicalOperation;
import com.tc.platform.PlatformService;
import com.terracotta.toolkit.cluster.TerracottaNode;
import com.terracotta.toolkit.concurrent.locks.ToolkitLockingApi;
import com.terracotta.toolkit.factory.impl.ToolkitNotifierFactoryImpl;
import com.terracotta.toolkit.object.AbstractTCToolkitObject;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

public class ToolkitNotifierImpl<T> extends AbstractTCToolkitObject implements ToolkitNotifier<T> {

  private static final TCLogger                                                LOGGER    = TCLogging
                                                                                             .getLogger(ToolkitNotifierImpl.class);

  private final transient CopyOnWriteArrayList<ToolkitNotificationListener<T>> listeners = new CopyOnWriteArrayList<ToolkitNotificationListener<T>>();
  private final String                                                         currentNodeIdStringForm;
  private volatile String                                                      lockid;
  private final ExecutorService                                                notifierService;

  public ToolkitNotifierImpl(PlatformService platformService) {
    super(platformService);
    this.currentNodeIdStringForm = serStrategy.serializeToString(new TerracottaNode(platformService.getCurrentNode()));
    this.notifierService = platformService
        .lookupRegisteredObjectByName(ToolkitNotifierFactoryImpl.TOOLKIT_NOTIFIER_EXECUTOR_SERVICE,
                                      ExecutorService.class);
  }

  @Override
  public String getName() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addNotificationListener(ToolkitNotificationListener<T> listener) {
    listeners.addIfAbsent(listener);
  }

  @Override
  public void removeNotificationListener(ToolkitNotificationListener<T> listener) {
    listeners.remove(listener);
  }

  @Override
  public void notifyListeners(T msg) {
    begin();
    try {
      unlockedNotifyListeners(msg);
    } finally {
      commit();
    }
  }

  private void unlockedNotifyListeners(T msg) {
    String stringMsg = null;
    stringMsg = serStrategy.serializeToString(msg);
    platformService.logicalInvoke(this, LogicalOperation.CLUSTERED_NOTIFIER, new Object[] { stringMsg,
        currentNodeIdStringForm });
  }

  /**
   * Called by applicator on receiving a remote msg
   */
  protected void onNotification(final String remoteMsg, final String remoteNodeID) {
    try {
      notifierService.execute(new Runnable() {
        @Override
        public void run() {
          ToolkitNotificationEventImpl<T> event = new ToolkitNotificationEventImpl<T>(serStrategy, remoteNodeID, remoteMsg);
          for (ToolkitNotificationListener<T> listener : listeners) {
            try {
              listener.onNotification(event);
            } catch (Throwable t) {
              // ignore any exception happening on listeners
              LOGGER.warn("Exception while trying to notify listener ", t);
            }
          }

        }
      });
    } catch (RejectedExecutionException e) {
      if (notifierService.isShutdown()) {
        LOGGER.debug("Ignoring Notification as Notifier is shutdown" + e);
      } else {
        throw e;
      }
    }
  }

  private void begin() {
    ToolkitLockingApi.lock(getLockID(), ToolkitLockTypeInternal.CONCURRENT, platformService);
  }

  private void commit() {
    ToolkitLockingApi.unlock(getLockID(), ToolkitLockTypeInternal.CONCURRENT, platformService);
  }

  private String getLockID() {
    if (lockid != null) { return lockid; }

    lockid = "__tc_clusteredNotifier_" + tcObject.getObjectID();
    return lockid;
  }

  @Override
  public List<ToolkitNotificationListener<T>> getNotificationListeners() {
    return Collections.unmodifiableList(this.listeners);
  }

  @Override
  public void cleanupOnDestroy() {
    //
  }
}
