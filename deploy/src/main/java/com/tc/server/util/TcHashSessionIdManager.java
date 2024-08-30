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
package com.tc.server.util;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionIdManager;
import org.eclipse.jetty.server.session.DefaultSessionIdManager;
import org.eclipse.jetty.server.session.HouseKeeper;
import org.eclipse.jetty.server.session.SessionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.EventListener;
import java.util.Set;

/**
 * Delegates to Jetty's HashSessionIdManager but initializes it lazily
 * with a background thread.
 */
public class TcHashSessionIdManager implements SessionIdManager {

  private volatile DefaultSessionIdManager delegate;

  @Override
  public String getId(String nodeId) {
    return getDelegate().getId(nodeId);
  }

  @Override
  public String getExtendedId(String clusterId, HttpServletRequest request) {
    return getDelegate().getExtendedId(clusterId, request);
  }

  @Override
  public String getWorkerName() {
    return getDelegate().getWorkerName();
  }


  @Override
  public String renewSessionId(String oldId, String oldExtendedId, HttpServletRequest request) {
    return getDelegate().renewSessionId(oldId, oldExtendedId, request);
  }

  @Override
  public Set<SessionHandler> getSessionHandlers() {
    return getDelegate().getSessionHandlers();
  }

  @Override
  public void setSessionHouseKeeper(HouseKeeper houseKeeper) {
    getDelegate().setSessionHouseKeeper(houseKeeper);
  }

  @Override
  public HouseKeeper getSessionHouseKeeper() {
    return getDelegate().getSessionHouseKeeper();
  }

  @Override
  public boolean isIdInUse(String id) {
    return getDelegate().isIdInUse(id);
  }


  @Override
  public void expireAll(String id) {
    getDelegate().expireAll(id);
  }

  @Override
  public void invalidateAll(String id) {
    getDelegate().invalidateAll(id);
  }

  @Override
  public boolean isFailed() {
    if (delegate == null) { return false; }
    return getDelegate().isFailed();
  }

  @Override
  public boolean addEventListener(EventListener eventListener) {
    if (delegate == null) { return false; }
    return getDelegate().addEventListener(eventListener);
  }

  @Override
  public boolean removeEventListener(EventListener eventListener) {
    if (delegate == null) { return false; }
    return getDelegate().removeEventListener(eventListener);
  }

  @Override
  public final void start() throws Exception {
    if (delegate != null) { return; }

    // Initialize delegate lazily to prevent the SecureRandom
    // it contains from blocking the jetty initialization.
    Thread thread = new Thread() {
      @Override
      public void run() {
        getDelegate();
      }
    };
    thread.setName("TcHashSessionIdManager initializer");
    thread.setDaemon(true);
    thread.start();
  }

  @Override
  public final void stop() throws Exception {
    if (delegate == null) { return; }
    getDelegate().stop();
    delegate = null;
  }

  @Override
  public boolean isRunning() {
    if (delegate == null) { return false; }
    return getDelegate().isRunning();
  }

  @Override
  public boolean isStarted() {
    if (delegate == null) { return false; }
    return getDelegate().isStarted();
  }

  @Override
  public boolean isStarting() {
    if (delegate == null) { return false; }
    return getDelegate().isStarting();
  }

  @Override
  public boolean isStopping() {
    if (delegate == null) { return false; }
    return getDelegate().isStopping();
  }

  @Override
  public boolean isStopped() {
    if (delegate == null) { return true; }
    return getDelegate().isStopped();
  }

  @Override
  public String newSessionId(HttpServletRequest request, long created) {
    return getDelegate().newSessionId(request, created);
  }

  private DefaultSessionIdManager getDelegate() {
    if (delegate != null) { return delegate; }
    synchronized (this) {
      if (delegate != null) { return delegate; }

      DefaultSessionIdManager realManager = new DefaultSessionIdManager(new Server());
      try {
        realManager.start();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

      delegate = realManager;

      return delegate;
    }
  }
}
