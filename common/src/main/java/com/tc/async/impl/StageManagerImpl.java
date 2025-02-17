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
package com.tc.async.impl;

import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.EventHandler;
import com.tc.async.api.PostInit;
import com.tc.async.api.Stage;
import com.tc.async.api.StageManager;
import com.tc.async.api.StageMonitor;
import com.tc.logging.DefaultLoggerProvider;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLoggerProvider;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;
import com.tc.stats.Stats;
import com.tc.text.PrettyPrinter;
import com.tc.text.StringFormatter;
import com.tc.util.Assert;
import com.tc.util.concurrent.QueueFactory;
import com.tc.util.concurrent.ThreadUtil;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author steve
 */
public class StageManagerImpl implements StageManager {

  private static final boolean     MONITOR       = TCPropertiesImpl.getProperties()
                                                     .getBoolean(TCPropertiesConsts.TC_STAGE_MONITOR_ENABLED);
  private static final long        MONITOR_DELAY = TCPropertiesImpl.getProperties()
                                                     .getLong(TCPropertiesConsts.TC_STAGE_MONITOR_DELAY);

  private final Map<String, Stage> stages        = new ConcurrentHashMap<String, Stage>();
  private TCLoggerProvider         loggerProvider;
  private final ThreadGroup        group;
  private String[]                 stageNames    = new String[] {};
  private QueueFactory             queueFactory  = null;
  private volatile boolean         started;

  public StageManagerImpl(ThreadGroup threadGroup, QueueFactory queueFactory) {
    this.loggerProvider = new DefaultLoggerProvider();
    this.group = threadGroup;
    this.queueFactory = queueFactory;

    if (MONITOR) {
      startMonitor();
    }
  }

  private void startMonitor() {
    final TCLogger logger = loggerProvider.getLogger(getClass());
    Thread t = new Thread("SEDA Stage Monitor") {
      @Override
      public void run() {
        while (true) {
          printStats();
          ThreadUtil.reallySleep(MONITOR_DELAY);
        }
      }

      private void printStats() {
        try {
          Stats stats[] = StageManagerImpl.this.getStats();
          logger.info("Stage Depths");
          logger.info("=================================");
          for (Stats stat : stats) {
            stat.logDetails(logger);
          }
        } catch (Throwable th) {
          logger.error(th);
        }
      }
    };
    t.setDaemon(true);
    t.start();
  }

  @Override
  public void setLoggerProvider(TCLoggerProvider loggerProvider) {
    this.loggerProvider = loggerProvider;
  }

  @Override
  public synchronized Stage createStage(String name, EventHandler handler, int threads, int maxSize) {
    return createStage(name, handler, threads, threads, maxSize);
  }

  @Override
  public synchronized Stage createStage(String name, EventHandler handler, int threads, int threadsToQueueRatio,
                                        int maxSize) {
    if (started) {
      throw new IllegalStateException("A new stage cannot be created, because StageManager is already started.");
    }

    int capacity = maxSize > 0 ? maxSize : Integer.MAX_VALUE;
    Stage s = new StageImpl(loggerProvider, name, handler, threads, threadsToQueueRatio, group, this.queueFactory,
                            capacity);
    addStage(name, s);
    return s;
  }

  private synchronized void addStage(String name, Stage s) {
    Object prev = stages.put(name, s);
    Assert.assertNull(prev);
    s.getSink().enableStatsCollection(MONITOR);
    stageNames = stages.keySet().toArray(new String[stages.size()]);
    Arrays.sort(stageNames);
  }

  @Override
  public void startStage(Stage stage, ConfigurationContext context) {
    stage.start(context);
  }

  @Override
  public synchronized void startAll(ConfigurationContext context, List<PostInit> toInit) {
    for (Object element : toInit) {
      PostInit mgr = (PostInit) element;
      mgr.initializeContext(context);

    }
    for (Object element : stages.values()) {
      Stage s = (Stage) element;
      s.start(context);
    }
    started = true;
  }

  @Override
  public void stopStage(Stage stage) {
    stage.destroy();
  }

  @Override
  public void stopAll() {
    for (Object element : stages.values()) {
      Stage s = (Stage) element;
      s.destroy();
    }
    stages.clear();
    started = false;
  }

  @Override
  public void cleanup() {
    // TODO: ClientConfigurationContext is not visible so can't use ClientConfigurationContext.CLUSTER_EVENTS_STAGE
    Collection<String> skipStages = new HashSet<String>();
    skipStages.add("cluster_events_stage");
    for (Object element : stages.values()) {
      Stage s = (Stage) element;
      if (!skipStages.contains(s.getName())) {
        s.getSink().clear();
      }
    }
  }

  @Override
  public Stage getStage(String name) {
    return stages.get(name);
  }

  @Override
  public synchronized Stats[] getStats() {
    final String[] names = stageNames;
    final Stats[] stats = new Stats[names.length];

    for (int i = 0; i < names.length; i++) {
      stats[i] = getStage(names[i]).getSink().getStats(MONITOR_DELAY);
    }
    return stats;
  }

  @Override
  public Collection<Stage> getStages() {
    return stages.values();
  }

  static class StageMonitors {

    private final List            monitors  = Collections.synchronizedList(new LinkedList());
    private final StringFormatter formatter = new StringFormatter();

    StageMonitors(final TCLogger logger) {
      return;
    }

    public StageMonitor newStageMonitor(String name) {
      return new NullStageMonitor();
    }

    @Override
    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("StageMonitors").append(formatter.newline());
      for (Iterator i = Collections.unmodifiableList(monitors).iterator(); i.hasNext();) {
        buf.append(((StageMonitorImpl) i.next()).dumpAndFlush()).append(formatter.newline());
      }
      return buf.toString();
    }
  }

  @Override
  public PrettyPrinter prettyPrint(PrettyPrinter out) {
    out.print(this.getClass().getName()).flush();
    for (Stage stage : getStages()) {
      out.indent().visit(stage).flush();
    }
    return out;
  }
}
