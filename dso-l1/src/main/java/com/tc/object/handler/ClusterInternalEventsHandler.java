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
package com.tc.object.handler;

import com.tc.async.api.AbstractEventHandler;
import com.tc.async.api.EventContext;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.object.DistributedObjectClient;
import com.tc.object.locks.ClientServerExchangeLockContext;
import com.tc.object.locks.ThreadID;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;
import com.tc.text.PrettyPrintable;
import com.tc.text.PrettyPrinter;
import com.tcclient.cluster.ClusterInternalEventsContext;
import com.tcclient.cluster.DsoClusterEventsNotifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Handler firing the dso cluster internal events to the listeners
 */
public class ClusterInternalEventsHandler extends AbstractEventHandler {

  private static final TCLogger          logger = TCLogging.getLogger(ClusterInternalEventsHandler.class);
  private static final int               EXECUTOR_MAX_THREADS = TCPropertiesImpl
                                                                  .getProperties()
                                                                  .getInt(TCPropertiesConsts.L1_CLUSTEREVENT_EXECUTOR_MAX_THREADS,
                                                                          20);
  private static final long              EXECUTOR_MAX_WAIT_SECONDS = TCPropertiesImpl
                                                                       .getProperties()
                                                                       .getLong(TCPropertiesConsts.L1_CLUSTEREVENT_EXECUTOR_MAX_WAIT_SECONDS,
                                                                               60);

  // TAB-7121 Post event lock scan diagnostic enablement -- UNDOCUMENTED
  private static final String L1_CLUSTEREVENT_LOCK_SCAN_ENABLED = "l1.clusterevent.lockScan.enabled";
  private static final boolean LOCK_SCAN_ENABLED =
      TCPropertiesImpl.getProperties().getBoolean(L1_CLUSTEREVENT_LOCK_SCAN_ENABLED, false);

  private final DsoClusterEventsNotifier dsoClusterEventsNotifier;
  private final ClusterEventExecutor     clusterEventExecutor      = new ClusterEventExecutor();

  private final DistributedObjectClient distributedObjectClient;

  private static class ClusterEventExecutor implements PrettyPrintable {

    private final DaemonThreadFactory daemonThreadFactory = new DaemonThreadFactory();
    private final ThreadPoolExecutor  eventExecutor       = new ThreadPoolExecutor(1, EXECUTOR_MAX_THREADS, 60L,
                                                                                   TimeUnit.SECONDS,
                                                                                   new SynchronousQueue<Runnable>(),
                                                                                   daemonThreadFactory,
                                                                                   new ThreadPoolExecutor.DiscardPolicy());

    public ThreadPoolExecutor getExecutorService() {
      return eventExecutor;
    }

    @Override
    public PrettyPrinter prettyPrint(PrettyPrinter out) {
      out.print("clusterEventExecutor active: " + eventExecutor.getActiveCount() + " queue: "
                    + eventExecutor.getQueue().size()).flush();
      return out;
    }

  }

  private static class DaemonThreadFactory implements ThreadFactory {
    private final AtomicInteger threadNumber = new AtomicInteger();
    private final String        threadName   = "cluster-events-processor-";

    @Override
    public Thread newThread(Runnable runnable) {
      Thread thread = new Thread(runnable, threadName + threadNumber.getAndIncrement());
      thread.setDaemon(true);
      return thread;
    }
  }

  public ClusterInternalEventsHandler(DistributedObjectClient distributedObjectClient, final DsoClusterEventsNotifier eventsNotifier) {
    /*
     * CAUTION: The DistributedObjectClient is not fully started when the ClusterInternalEventsHandler is created ...
     * use the distributedObjectClient with care.  In particular, the ThreadIDManager and ClientLockManager instances
     * are **not** available from the DistributedObjectClient when this instance is created.
     */
    this.distributedObjectClient = distributedObjectClient;
    this.dsoClusterEventsNotifier = eventsNotifier;
  }

  @Override
  public void handleEvent(final EventContext context) {
    ThreadPoolExecutor service = clusterEventExecutor.getExecutorService();
    Future eventFuture = service.submit(new Runnable() {
      @Override
      public void run() {
        if (context instanceof ClusterInternalEventsContext) {
          ClusterInternalEventsContext eventContext = (ClusterInternalEventsContext) context;
          dsoClusterEventsNotifier.notifyDsoClusterListener(eventContext.getEventType(), eventContext.getEvent(),
                                                            eventContext.getDsoClusterListener());
        } else {
          throw new AssertionError("Unknown Context " + context);
        }

        /*
         * TAB-7121 Log the existence of any locks held in this thread ... can lead to rejoin failures.
         * (Not all locks held after event processing are a problem -- client shutdown terminates the
         * lock manager so locks may not be released for events processed while shutdown is pending.)
         */
        if (LOCK_SCAN_ENABLED) {
          scanForThreadLocks(context);
        }
      }
    });

    try {
      eventFuture.get(EXECUTOR_MAX_WAIT_SECONDS, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      logger.warn("clusterEventExecutor interrupted while waiting for result context :" + context, e);
    } catch (ExecutionException e) {
      throw new RuntimeException(e.getCause());
    } catch (TimeoutException e) {
      logger.warn("clusterEventExecutor timedout while waiting for result context :" + context, e);
    }
  }

  /**
   * Scans for and logs any locks held by the current thread.
   *
   * @param context the {@code EventContext} used to identify current context for logging
   */
  private void scanForThreadLocks(EventContext context) {
    try {
      ThreadID threadID = distributedObjectClient.getThreadIDManager().getThreadID();
      List<ClientServerExchangeLockContext> heldLocks = new ArrayList<ClientServerExchangeLockContext>();
      for (ClientServerExchangeLockContext lockContext : distributedObjectClient.getLockManager().getAllLockContexts()) {
        if (lockContext.getThreadID().equals(threadID)) {
          heldLocks.add(lockContext);
        }
      }
      if (!heldLocks.isEmpty()) {
        boolean multipleLocks = heldLocks.size() > 1;
        StringBuilder sb = new StringBuilder(4096);
        sb.append("Held lock");
        if (multipleLocks) {
          sb.append('s');
        }
        sb.append(" remain");
        if (!multipleLocks) {
          sb.append('s');
        }
        sb.append(" in thread ").append(threadID).append(" after handling event ").append(context).append(':');
        for (ClientServerExchangeLockContext heldLock : heldLocks) {
          sb.append("\n--> ").append(heldLock);
        }
        logger.error(sb);
      }
    } catch (Exception e) {
      logger.warn("Scan/display of held locks failed", e);
    }
  }

  @Override
  public synchronized void destroy() {
    super.destroy();
    clusterEventExecutor.getExecutorService().shutdownNow();
  }
}
