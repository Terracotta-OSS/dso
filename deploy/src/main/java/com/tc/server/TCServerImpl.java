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
package com.tc.server;

import com.tc.async.api.ConfigurationContext;
import com.tc.async.api.SEDA;
import com.tc.async.api.Sink;
import com.tc.async.api.Stage;
import com.tc.async.api.StageManager;
import com.tc.config.Directories;
import com.tc.config.schema.ActiveServerGroupConfig;
import com.tc.config.schema.CommonL2Config;
import com.tc.config.schema.L2Info;
import com.tc.config.schema.SecurityConfig;
import com.tc.config.schema.ServerGroupInfo;
import com.tc.config.schema.messaging.http.ConfigServlet;
import com.tc.config.schema.messaging.http.GroupIDMapServlet;
import com.tc.config.schema.messaging.http.GroupInfoServlet;
import com.tc.config.schema.messaging.http.ManagementNotListeningOnThatPortServlet;
import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.config.schema.setup.FailOverAction;
import com.tc.config.schema.setup.L2ConfigurationSetupManager;
import com.tc.exception.TCRuntimeException;
import com.tc.l2.state.StateManager;
import com.tc.l2.state.sbp.SBPResolver;
import com.tc.l2.state.sbp.SBPResolverImpl;
import com.tc.lang.StartupHelper;
import com.tc.lang.StartupHelper.StartupAction;
import com.tc.lang.TCThreadGroup;
import com.tc.lang.ThrowableHandlerImpl;
import com.tc.license.LicenseManager;
import com.tc.logging.CustomerLogging;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.management.beans.L2MBeanNames;
import com.tc.management.beans.L2State;
import com.tc.management.beans.TCServerInfo;
import com.tc.management.beans.TCServerInfoMBean.RestartMode;
import com.tc.net.GroupID;
import com.tc.net.OrderedGroupIDs;
import com.tc.net.TCSocketAddress;
import com.tc.net.core.BufferManagerFactoryProvider;
import com.tc.net.core.BufferManagerFactoryProviderImpl;
import com.tc.net.core.PipeSocket;
import com.tc.net.core.security.TCPrincipal;
import com.tc.net.core.security.TCSecurityManager;
import com.tc.net.protocol.transport.ConnectionPolicy;
import com.tc.net.protocol.transport.ConnectionPolicyImpl;
import com.tc.object.config.schema.L2DSOConfig;
import com.tc.objectserver.core.api.ServerConfigurationContext;
import com.tc.objectserver.core.impl.ServerManagementContext;
import com.tc.objectserver.dgc.impl.GCStatsEventPublisher;
import com.tc.objectserver.impl.DistributedObjectServer;
import com.tc.objectserver.impl.NullSafeMode;
import com.tc.objectserver.impl.SafeMode;
import com.tc.objectserver.mgmt.ObjectStatsRecorder;
import com.tc.operatorevent.TerracottaOperatorEventHistoryProvider;
import com.tc.properties.TCProperties;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;
import com.tc.server.util.TcHashSessionIdManager;
import com.tc.servlets.L1ReconnectPropertiesServlet;
import com.tc.stats.DSO;
import com.tc.stats.api.DSOMBean;
import com.tc.util.Assert;
import com.tc.util.Conversion;
import com.tc.util.Conversion.MetricsFormatException;
import com.tc.util.ProductInfo;
import com.terracottatech.config.DataStorage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.lang.ref.PhantomReference;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.security.auth.Subject;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.ee10.servlet.DefaultServlet;
import org.eclipse.jetty.ee10.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.ee10.servlet.security.ConstraintMapping;
import org.eclipse.jetty.ee10.servlet.security.ConstraintSecurityHandler;
import org.eclipse.jetty.ee10.webapp.WebAppContext;
import org.eclipse.jetty.http.HttpMethod;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.security.AbstractLoginService;
import org.eclipse.jetty.security.Constraint;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.RolePrincipal;
import org.eclipse.jetty.security.UserIdentity;
import org.eclipse.jetty.security.UserPrincipal;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.resource.PathResourceFactory;
import org.eclipse.jetty.util.ssl.SslContextFactory;

public class TCServerImpl extends SEDA implements TCServer {

  public static final String                VERSION_SERVLET_PATH                         = "/version";
  public static final String                CONFIG_SERVLET_PATH                          = "/config";
  public static final String                MANAGEMENT_NOT_LISTENING_ON_THAT_PORT_SERVLET_PATH                          = "/tc-management-api/*";
  public static final String                GROUP_INFO_SERVLET_PATH                      = "/groupinfo";
  public static final String                GROUPID_MAP_SERVLET_PATH                     = "/groupidmap";
  public static final String                L1_RECONNECT_PROPERTIES_FROML2_SERVELET_PATH = "/l1reconnectproperties";

  public static final String                HTTP_AUTHENTICATION_ROLE_STATISTICS          = "statistics";

  public static final String                HTTP_SECURITY_ROLE                           = "terracotta";

  public static final String                CONNECTOR_NAME_TERRACOTTA                    = "terracotta";
  public static final String                CONNECTOR_NAME_MANAGEMENT                    = "management";

  public static final File                  TC_MANAGEMENT_API_LOCKFILE                   = new File(
                                                                                                    System
                                                                                                        .getProperty("java.io.tmpdir"),
                                                                                                    ".tc-management-api.lock");

  private static final TCLogger             logger                                       = TCLogging
                                                                                             .getLogger(TCServer.class);
  private static final TCLogger             consoleLogger                                = CustomerLogging
                                                                                             .getConsoleLogger();

  private volatile long                     startTime                                    = -1;
  private volatile long                     activateTime                                 = -1;

  protected DistributedObjectServer         dsoServer;
  private Server                            httpServer;
  protected ContextHandlerCollection        contextHandlerCollection;
  private TerracottaConnector               terracottaConnector;

  private final Object                      stateLock                                    = new Object();
  private final L2State                     state                                        = new L2State();
  private final L2State                     initialState                                 = new L2State();

  private final L2ConfigurationSetupManager configurationSetupManager;
  protected final ConnectionPolicy          connectionPolicy;
  private boolean                           shutdown                                     = false;
  protected final TCSecurityManager         securityManager;
  protected SBPResolver                     sbpResolver;
  private final SafeMode                    safeMode;

  // leaked http socket reclaimer
  private final boolean enableReclaimer = TCPropertiesImpl.getProperties().getBoolean(TCPropertiesConsts.HTTP_ENABLE_SOCKET_RECLAIMER, false);
  private final ReferenceQueue<Socket> referenceQueue = new ReferenceQueue<>();
  private final Map<PhantomReference<Socket>, Socket> sockets = Collections.synchronizedMap(new IdentityHashMap<>());

  /**
   * This should only be used for tests.
   */
  public TCServerImpl(final L2ConfigurationSetupManager configurationSetupManager) {
    this(configurationSetupManager, new TCThreadGroup(new ThrowableHandlerImpl(TCLogging.getLogger(TCServer.class))));
  }

  public TCServerImpl(final L2ConfigurationSetupManager configurationSetupManager, final TCThreadGroup threadGroup) {
    this(configurationSetupManager, threadGroup, new ConnectionPolicyImpl(Integer.MAX_VALUE));
  }

  public TCServerImpl(final L2ConfigurationSetupManager manager, final TCThreadGroup group,
                      final ConnectionPolicy connectionPolicy) {
    super(group);

    this.connectionPolicy = connectionPolicy;
    Assert.assertNotNull(manager);
    validateEnterpriseFeatures(manager);
    this.configurationSetupManager = manager;

    if (configurationSetupManager.isSecure()) {
      this.securityManager = createSecurityManager(configurationSetupManager.getSecurity());
      verifySecurityManagerFindsCredentialsForAllL2Servers();
    } else {
      this.securityManager = null;
    }

    this.safeMode = configurationSetupManager.isSafeModeConfigured() ? getSafeMode() : new NullSafeMode();

    if (this.enableReclaimer) {
      Timer timer = new Timer("terracotta-http-socket-reclaimer", true);
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          List<Socket> reclaimed = new ArrayList<>();

          while (true) {
            Reference<? extends Socket> socketRef = referenceQueue.poll();
            if (socketRef == null) {
              break;
            }

            Socket socket = sockets.remove(socketRef);
            if (socket != null && !socket.isClosed()) {
              try {
                reclaimed.add(socket);
                socket.close();
              } catch (IOException ioe) {
                logger.warn("Error closing reclaimed http socket : " + socket, ioe);
              }
            }
          }

          if (!reclaimed.isEmpty()) {
            logger.info("Reclaimed " + reclaimed.size() + " http socket(s) : " + reclaimed);
          }
        }
      }, 5000, 5000);
    }
  }

  private void verifySecurityManagerFindsCredentialsForAllL2Servers() {
    if (logger.isDebugEnabled()) {
      logger.debug("Checking that keychain information is complete for cluster");
    }
    List<ActiveServerGroupConfig> activeServerGroups = configurationSetupManager.activeServerGroupsConfig()
        .getActiveServerGroups();
    for (ActiveServerGroupConfig activeServerGroup : activeServerGroups) {
      for (String server : activeServerGroup.getMembers()) {
        if (!configurationSetupManager.getL2Identifier().equals(server)) {
          try {
            L2DSOConfig dsoConfig = configurationSetupManager.dsoL2ConfigFor(server);
            try {
              // Following will throw NPE if password not found - which is what we want
              securityManager.getPasswordForTC(securityManager.getIntraL2Username(), dsoConfig.host(), dsoConfig
                  .tsaGroupPort().getIntValue());
            } catch (NullPointerException npex) {
              throw new IllegalStateException(
                                              "Invalid cluster security configuration. Unable to find connection credentials to server "
                                                  + dsoConfig.serverName(), npex);
            }
          } catch (ConfigurationSetupException e) {
            throw new IllegalStateException("Unexpected error browsing cluster configuration", e);
          }
        }
      }
    }
  }

  protected TCSecurityManager createSecurityManager(final SecurityConfig securityConfig) {
    throw new UnsupportedOperationException("Only Terracotta EE supports the security feature, "
                                            + "you're currently running an OS version");
  }

  protected SafeMode getSafeMode() {
    throw new UnsupportedOperationException("Only Terracotta EE supports the Safe Mode feature, "
                                            + "you're currently running an OS version");
  }

  private void validateEnterpriseFeatures(final L2ConfigurationSetupManager manager) {
    if (!LicenseManager.enterpriseEdition()) return;

    DataStorage dataStorage = manager.dsoL2Config().getDataStorage();
    if (!TCPropertiesImpl.getProperties().getBoolean(TCPropertiesConsts.L2_OFFHEAP_DISABLED, false)) {
      LicenseManager.verifyServerArrayOffheapCapability(dataStorage.getSize());
    }
    if (manager.commonl2Config().authentication()) {
      LicenseManager.verifyAuthenticationCapability();
    }
  }

  private static OrderedGroupIDs createOrderedGroupIds(List<ActiveServerGroupConfig> groups) {
    GroupID[] gids = new GroupID[groups.size()];
    for (int i = 0; i < groups.size(); i++) {
      gids[i] = groups.get(i).getGroupId();
    }
    return new OrderedGroupIDs(gids);
  }

  @Override
  public ServerGroupInfo[] serverGroups() {
    L2Info[] l2Infos = infoForAllL2s();
    List<ActiveServerGroupConfig> groups = this.configurationSetupManager.activeServerGroupsConfig()
        .getActiveServerGroups();
    OrderedGroupIDs orderedGroupsIds = createOrderedGroupIds(groups);
    GroupID coordinatorId = orderedGroupsIds.getActiveCoordinatorGroup();
    ServerGroupInfo[] result = new ServerGroupInfo[groups.size()];
    for (int i = 0; i < groups.size(); i++) {
      ActiveServerGroupConfig groupInfo = groups.get(i);
      GroupID groupId = groupInfo.getGroupId();
      List<L2Info> memberList = new ArrayList<L2Info>();
      for (L2Info l2Info : l2Infos) {
        if (groupInfo.isMember(l2Info.name())) {
          memberList.add(l2Info);
        }
      }
      result[i] = new ServerGroupInfo(memberList.toArray(new L2Info[0]), groupInfo.getGroupName(), groupId.toInt(),
                                      coordinatorId.equals(groupId));
    }
    return result;
  }

  @Override
  public L2Info[] infoForAllL2s() {
    synchronized (configurationSetupManager.commonl2Config().syncLockForBean()) {
      String[] allKnownL2s = this.configurationSetupManager.allCurrentlyKnownServers();
      L2Info[] out = new L2Info[allKnownL2s.length];

      for (int i = 0; i < out.length; ++i) {
        try {
          CommonL2Config config = this.configurationSetupManager.commonL2ConfigFor(allKnownL2s[i]);

          String name = allKnownL2s[i];
          if (name == null) {
            name = L2Info.IMPLICIT_L2_NAME;
          }

          String host = config.jmxPort().getBind();
          if (TCSocketAddress.WILDCARD_IP.equals(host)) {
            host = config.host();
          }
          if (StringUtils.isBlank(host)) {
            host = name;
          }

          out[i] = new L2Info(name,
                              host,
                              config.jmxPort().getIntValue(),
                              config.tsaPort().getIntValue(),
                              config.tsaGroupPort().getBind(),
                              config.tsaGroupPort().getIntValue(),
                              config.managementPort().getIntValue(),
                              getSecurityHostname());
        } catch (ConfigurationSetupException cse) {
          throw Assert.failure("This should be impossible here", cse);
        }
      }
      return out;
    }
  }

  @Override
  public String getL2Identifier() {
    return configurationSetupManager.getL2Identifier();
  }

  @Override
  public String getDescriptionOfCapabilities() {
    if (ProductInfo.getInstance().isEnterprise()) {
      return LicenseManager.licensedCapabilities();
    } else {
      return "Open source capabilities";
    }
  }

  /**
   * I realize this is wrong, since the server can still be starting but we'll have to deal with the whole stopping
   * issue later, and there's the TCStop feature which should be removed.
   */
  @Override
  public void stop() {
    synchronized (this.stateLock) {
      if (!this.state.isStartState()) {
        stopServer();
        logger.info("Server stopped.");
      } else {
        logger.warn("Server in incorrect state (" + this.state.getState() + ") to be stopped.");
      }
    }

  }

  @Override
  public void start() {
    synchronized (this.stateLock) {
      if (this.state.isStartState()) {
        try {
          startServer();
        } catch (Throwable t) {
          if (t instanceof RuntimeException) { throw (RuntimeException) t; }
          throw new RuntimeException(t);
        }
      } else {
        logger.warn("Server in incorrect state (" + this.state.getState() + ") to be started.");
      }
    }
  }

  @Override
  public boolean canShutdown() {
    return state.isPassiveStandby() || state.isActiveCoordinator() || state.isPassiveUninitialized() || state.isSafeModeState();
  }

  private final Object SHUTDOWN_LOCK = new Object();

  @Override
  public void shutdown() {
    shutdown(RestartMode.STOP_ONLY);
  }

  @Override
  public void shutdown(RestartMode restartMode) {
    boolean doShutdown = false;

    synchronized (SHUTDOWN_LOCK) {
      if (canShutdown()) {
        this.state.setState(StateManager.STOP_STATE);
        doShutdown = true;
      }
    }

    if (doShutdown) {
      consoleLogger.info("Server exiting...");
      notifyShutdown();
      Runtime.getRuntime().exit(restartMode.getExitStatus());
    } else {
      logger.warn("Server in incorrect state (" + this.state.getState() + ") to be shutdown.");
    }
  }

  @Override
  public long getStartTime() {
    return this.startTime;
  }

  @Override
  public void updateActivateTime() {
    if (this.activateTime == -1) {
      this.activateTime = System.currentTimeMillis();
    }
  }

  @Override
  public long getActivateTime() {
    return this.activateTime;
  }

  @Override
  public boolean isGarbageCollectionEnabled() {
    return this.configurationSetupManager.dsoL2Config().garbageCollection().getEnabled();
  }

  @Override
  public int getGarbageCollectionInterval() {
    return this.configurationSetupManager.dsoL2Config().garbageCollection().getInterval();
  }

  @Override
  public String getConfig() {
    try {
      InputStream is = this.configurationSetupManager.rawConfigFile();
      return IOUtils.toString(is);
    } catch (IOException ioe) {
      return ioe.getLocalizedMessage();
    }
  }

  @Override
  public boolean getRestartable() {
    return configurationSetupManager.dsoL2Config().getRestartable().getEnabled();
  }

  @Override
  public int getTSAListenPort() {
    if (this.dsoServer != null) { return this.dsoServer.getListenPort(); }
    throw new IllegalStateException("TSA Server not running");
  }

  @Override
  public int getTSAGroupPort() {
    if (this.dsoServer != null) { return this.dsoServer.getGroupPort(); }
    throw new IllegalStateException("TSA Server not running");
  }

  @Override
  public int getManagementPort() {
    if (this.dsoServer != null) { return this.dsoServer.getManagementPort(); }
    throw new IllegalStateException("TSA Server not running");
  }

  public DistributedObjectServer getDSOServer() {
    return this.dsoServer;
  }

  @Override
  public boolean isStarted() {
    return !this.state.isStartState();
  }

  @Override
  public boolean isActive() {
    return this.state.isActiveCoordinator();
  }

  @Override
  public boolean isStopped() {
    // XXX:: introduce a new state when stop is officially supported.
    return this.state.isStartState();
  }

  @Override
  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append("Server: ").append(super.toString()).append("\n");
    if (isActive()) {
      buf.append("Active since ").append(new Date(getStartTime())).append("\n");
    } else if (isStarted()) {
      buf.append("Started at ").append(new Date(getStartTime())).append("\n");
    } else {
      buf.append("Server is stopped").append("\n");
    }

    return buf.toString();
  }

  private void stopServer() {
    // XXX: I have no idea if order of operations is correct here?

    if (logger.isDebugEnabled()) {
      consoleLogger.debug("Stopping TC server...");
    }

    try {
      unregisterDSOMBeans(this.dsoServer.getMBeanServer());
    } catch (Exception e) {
      logger.error("Error unregistering mbeans", e);
    }

    if (this.terracottaConnector != null) {
      try {
        this.terracottaConnector.shutdown();
      } catch (Exception e) {
        logger.error("Error shutting down terracotta connector", e);
      } finally {
        this.terracottaConnector = null;
      }
    }

    try {
      getStageManager().stopAll();
    } catch (Exception e) {
      logger.error("Error shutting down stage manager", e);
    }

    if (this.httpServer != null) {
      if (logger.isDebugEnabled()) {
        logger.debug("Shutting down HTTP server...");
      }

      try {
        for (Handler handler : this.contextHandlerCollection.getHandlers()) {
          this.contextHandlerCollection.removeHandler(handler);
          handler.stop();
        }
        this.httpServer.stop();
        this.httpServer.destroy();
      } catch (Exception e) {
        logger.error("Error shutting down HTTP server", e);
      } finally {
        this.httpServer = null;
      }
    }

    // this stops the jmx server then dso server
    if (this.dsoServer != null) {
      try {
        this.dsoServer.quickStop();
      } catch (Exception e) {
        logger.error("Error shutting down TSA server", e);
      } finally {
        this.dsoServer = null;
      }
    }

  }

  private class StartAction implements StartupAction {
    @Override
    public void execute() throws Throwable {
      if (logger.isDebugEnabled()) {
        logger.debug("Starting Terracotta server instance...");
      }

      TCServerImpl.this.startTime = System.currentTimeMillis();

      CommonL2Config commonL2Config = TCServerImpl.this.configurationSetupManager.commonl2Config();

      if (Runtime.getRuntime().maxMemory() != Long.MAX_VALUE) {
        consoleLogger.info("Available Max Runtime Memory: " + (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB");
      }
      SslContextFactory.Server sslContextFactory = null;
      if (securityManager != null) {
        sslContextFactory = new SslContextFactory.Server();
        sslContextFactory.setSslContext(securityManager.getSslContext());
      }

      TCServerImpl.this.httpServer = new Server() {
        @Override
        public boolean handle(Request request, Response response, Callback callback) throws Exception {
          if (HttpMethod.TRACE.is(request.getMethod())) {
            response.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            callback.succeeded();
            return true;
          }
          return super.handle(request, response, callback);
        }
      };
      HttpConfiguration httpConfig = new HttpConfiguration();
      httpConfig.setSendServerVersion(false);
      Consumer<Socket> socketConsumer = enableReclaimer ? (socket) -> sockets.put(new PhantomReference<>(socket, referenceQueue), ((PipeSocket) socket).getDelegate()) : null;
      TCServerImpl.this.terracottaConnector = new TerracottaConnector(httpServer, new HttpConnectionFactory(httpConfig), socketConsumer);
      // connectors are named so that webapps can respond only on a specific one
      // see: http://wiki.eclipse.org/Jetty/Howto/WebappPerConnector
      TCServerImpl.this.terracottaConnector.setName(CONNECTOR_NAME_TERRACOTTA);

      startHTTPServer(commonL2Config, TCServerImpl.this.terracottaConnector);
      Stage stage = getStageManager().createStage("dso-http-bridge",
                                                  new HttpConnectionHandler(TCServerImpl.this.terracottaConnector), 1,
                                                  100);
      stage.start(new NullContext(getStageManager()));

      // the following code starts the jmx server as well
      startDSOServer(stage.getSink(), () -> {
        try {
          File warTempDir = new File(commonL2Config.dataPath(), "jetty");
          prepareJettyWarTempDir(warTempDir);
          addManagementWebApp(warTempDir, commonL2Config);
        } catch (Exception e) {
          throw new RuntimeException("Caught exception while starting management", e);
        }
      });

      String l2Identifier = TCServerImpl.this.configurationSetupManager.getL2Identifier();
      if (l2Identifier != null) {
        logger.info("Server started as " + l2Identifier);
      }

      if (isActive()) {
        updateActivateTime();
        if (TCServerImpl.this.activationListener != null) {
          TCServerImpl.this.activationListener.serverActivated();
        }
      }
    }

    private void prepareJettyWarTempDir(File warTempDir) throws IOException {
      if (warTempDir.isDirectory()) {
        FileUtils.deleteDirectory(warTempDir);
      }
      warTempDir.mkdirs();
      if (!warTempDir.isDirectory() || !warTempDir.canWrite()) { throw new IOException(
                                                                                       "Can't create jetty temp dir at "
                                                                                           + warTempDir); }
    }

  }

  private long getMaxDataSize() {
    long maxOffheap = 0L;
    DataStorage datastore = configurationSetupManager.dsoL2Config().getDataStorage();
    try {
      maxOffheap = Conversion.memorySizeAsLongBytes(datastore.getSize());
    } catch (MetricsFormatException e) {
      throw new TCRuntimeException("Problem converting max data size: ", e);
    }
    return maxOffheap;
  }

  protected void startServer() throws Exception {
    new StartupHelper(getThreadGroup(), new StartAction()).startUp();
  }

  private void startDSOServer(final Sink httpSink, final Runnable managementStartup) throws Exception {
    Assert.assertTrue(this.state.isStartState());
    TCProperties tcProps = TCPropertiesImpl.getProperties();
    ObjectStatsRecorder objectStatsRecorder = new ObjectStatsRecorder(
                                                                      tcProps
                                                                          .getBoolean(TCPropertiesConsts.L2_OBJECTMANAGER_REQUEST_LOGGING_ENABLED),
                                                                      tcProps
                                                                          .getBoolean(TCPropertiesConsts.L2_TRANSACTIONMANAGER_LOGGING_PRINT_BROADCAST_STATS),
                                                                      tcProps
                                                                          .getBoolean(TCPropertiesConsts.L2_OBJECTMANAGER_PERSISTOR_LOGGING_ENABLED));

    this.dsoServer = createDistributedObjectServer(this.configurationSetupManager, this.connectionPolicy, httpSink,
                                                   new TCServerInfo(this, this.state, this.initialState,
                                                                    objectStatsRecorder, this.safeMode),
                                                   objectStatsRecorder, this.state, this.initialState, this,
                                                   this.safeMode, () -> {
                                                     try {
                                                       registerDSOServer();
                                                     } catch (Exception e) {
                                                       throw new RuntimeException("Caught exception while registering DSO Server", e);
                                                     }
                                                     managementStartup.run();
                                                   });
    this.dsoServer.start();
  }

  protected DistributedObjectServer createDistributedObjectServer(L2ConfigurationSetupManager configSetupManager,
                                                                  ConnectionPolicy policy, Sink httpSink,
                                                                  TCServerInfo serverInfo,
                                                                  ObjectStatsRecorder objectStatsRecorder,
                                                                  L2State l2State,
                                                                  L2State initialState,
                                                                  TCServerImpl serverImpl,
                                                                  SafeMode safeMode,
                                                                  Runnable managementStartup) {
    BufferManagerFactoryProvider bufferManagerFactoryProvider = new BufferManagerFactoryProviderImpl(this.securityManager);
    this.sbpResolver = new SBPResolverImpl();
    return new DistributedObjectServer(configSetupManager, getThreadGroup(), policy, httpSink, serverInfo,
                                       objectStatsRecorder, l2State, initialState, this, this, securityManager,
                                       bufferManagerFactoryProvider, this.sbpResolver, safeMode, managementStartup);
  }

  private void bindManagementHttpPort(final CommonL2Config commonL2Config)
      throws Exception {

    ServerConnector managementConnector;
    HttpConfiguration httpConfig = new HttpConfiguration();
    httpConfig.setSecureScheme("https");
    httpConfig.setSendServerVersion(false);

    if (commonL2Config.isSecure()) {
      SslContextFactory.Server sslContextFactory = new SslContextFactory.Server();
      sslContextFactory.setSslContext(securityManager.getSslContext());

      // TAB-5271
      sslContextFactory.addExcludeProtocols(getVulnerableProtocols());
      // TAB-6658
      sslContextFactory.addExcludeCipherSuites((getVulnerableCipherSuites()));
      httpConfig.addCustomizer(new SecureRequestCustomizer(false));

      managementConnector = new ServerConnector(httpServer,
          new SslConnectionFactory(sslContextFactory, "http/1.1"),
          new HttpConnectionFactory(httpConfig));
    } else {
      managementConnector = new ServerConnector(httpServer, new HttpConnectionFactory(httpConfig));
    }
    // connectors are named so that webapps can respond only on a specific one
    // see: http://wiki.eclipse.org/Jetty/Howto/WebappPerConnector
    managementConnector.setName(CONNECTOR_NAME_MANAGEMENT);

    managementConnector.setPort(commonL2Config.managementPort().getIntValue());

    String connectorHost = commonL2Config.managementPort().getBind();
    if (connectorHost.contains(":")) {
      connectorHost = "[" + connectorHost + "]";
    }
    managementConnector.setHost(connectorHost);
    this.httpServer.addConnector(managementConnector);
    if (this.httpServer.isStarted()) {
      managementConnector.start();
    }

    consoleLogger.info("Management server started on " + connectorHost + ":" + commonL2Config.managementPort().getIntValue());
  }

  private void startHTTPServer(final CommonL2Config commonL2Config, final TerracottaConnector tcConnector)
      throws Exception {

    TCServerImpl.this.httpServer.addConnector(tcConnector);

    this.contextHandlerCollection = new ContextHandlerCollection();
    Handler rootHandler = getRootHandler();
    rootHandler.setServer(TCServerImpl.this.httpServer);

    ServletContextHandler context = new ServletContextHandler("/", false, true);

    if (commonL2Config.isSecure()) {
      final String pathSpec = "/*";
      final TCUserRealm userRealm = new TCUserRealm(securityManager);
      setupBasicAuth(context, pathSpec, userRealm, HTTP_SECURITY_ROLE);
      logger.info("HTTPS Authentication enabled for path '" + pathSpec + "'");
    }

    context.setAttribute(ConfigServlet.CONFIG_ATTRIBUTE, this.configurationSetupManager);
    context.setAttribute(GroupInfoServlet.GROUP_INFO_ATTRIBUTE, this.configurationSetupManager);
    context.setAttribute(GroupIDMapServlet.GROUPID_MAP_ATTRIBUTE, this.configurationSetupManager);

    ServletHandler servletHandler = new ServletHandler();

    /**
     * We usually don't serve up any files, just hook in a few servlets. The ResourceBase can't be null though.
     */
    File tcInstallDir;
    try {
      tcInstallDir = Directories.getInstallationRoot();
    } catch (FileNotFoundException e) {
      // if an error occurs during the retrieval of the installation root, just set it to null
      // so that the fallback mechanism can be used
      tcInstallDir = null;
    }
    File userDir = new File(System.getProperty("user.dir"));
    boolean tcInstallDirValid = false;
    File resourceBaseDir = userDir;
    if (tcInstallDir != null && tcInstallDir.exists() && tcInstallDir.isDirectory() && tcInstallDir.canRead()) {
      tcInstallDirValid = true;
      resourceBaseDir = tcInstallDir;
    }
    context.setBaseResource(new PathResourceFactory().newResource(resourceBaseDir.toURI()));

    createAndAddServlet(servletHandler, VersionServlet.class.getName(), VERSION_SERVLET_PATH);
    createAndAddServlet(servletHandler, ConfigServlet.class.getName(), CONFIG_SERVLET_PATH);
    createAndAddServlet(servletHandler, ManagementNotListeningOnThatPortServlet.class.getName(), MANAGEMENT_NOT_LISTENING_ON_THAT_PORT_SERVLET_PATH);
    createAndAddServlet(servletHandler, GroupInfoServlet.class.getName(), GROUP_INFO_SERVLET_PATH);
    createAndAddServlet(servletHandler, GroupIDMapServlet.class.getName(), GROUPID_MAP_SERVLET_PATH);
    createAndAddServlet(servletHandler, L1ReconnectPropertiesServlet.class.getName(),
                        L1_RECONNECT_PROPERTIES_FROML2_SERVELET_PATH);

    if (TCPropertiesImpl.getProperties().getBoolean(TCPropertiesConsts.HTTP_DEFAULT_SERVLET_ENABLED, false)) {
      if (!tcInstallDirValid) {
        String msg = "Default HTTP servlet with file serving NOT enabled because the '"
                     + Directories.TC_INSTALL_ROOT_PROPERTY_NAME + "' system property is invalid.";
        consoleLogger.warn(msg);
        logger.warn(msg);
      } else {
        boolean aliases = TCPropertiesImpl.getProperties()
            .getBoolean(TCPropertiesConsts.HTTP_DEFAULT_SERVLET_ATTRIBUTE_ALIASES, false);
        boolean dirallowed = TCPropertiesImpl.getProperties()
            .getBoolean(TCPropertiesConsts.HTTP_DEFAULT_SERVLET_ATTRIBUTE_DIR_ALLOWED, false);
        context.setAttribute("aliases", aliases);
        context.setAttribute("dirAllowed", dirallowed);
        createAndAddServlet(servletHandler, DefaultServlet.class.getName(), "/");
        String msg = "Default HTTP servlet with file serving enabled for '" + resourceBaseDir.getCanonicalPath()
                     + "' (aliases = '" + aliases + "', dirallowed = '" + dirallowed + "')";
        consoleLogger.info(msg);
        logger.info(msg);
      }
    }

    context.setServletHandler(servletHandler);
    context.setVirtualHosts(Collections.singletonList("@" + CONNECTOR_NAME_TERRACOTTA));
    contextHandlerCollection.addHandler(context);

    this.httpServer.setHandler(rootHandler);
    this.httpServer.addBean(new TcHashSessionIdManager(this.httpServer), true);

    MBeanContainer mbeanContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
    this.httpServer.addBean(mbeanContainer);

    this.httpServer.setDefaultHandler(new DefaultHandler(false, true));

    try {
      this.httpServer.start();
    } catch (Exception e) {
      consoleLogger.warn("Couldn't start HTTP server", e);
      throw e;
    }
  }
  
  protected Handler getRootHandler() {
    return this.contextHandlerCollection;
  }

  private void addManagementWebApp(File warTempDir, CommonL2Config commonL2Config) throws Exception {
    if (!TCPropertiesImpl.getProperties().getBoolean(TCPropertiesConsts.MANAGEMENT_REST_ENABLED, true)) {
      consoleLogger.warn("REST Management is disabled per configuration.");
      return;
    }
    // register REST webapp
    String warFile = System.getProperty("com.tc.management.war");
    String failureReason = null;
    if (warFile == null) {
      final List<String> webAppPrefixes = Arrays.asList("ent-management-tsa-war", "management-tsa-war");
      try {
        File managementDir = Directories.getServerLibFolder();

        String[] files = managementDir.list(new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {
            for (String webAppPrefix : webAppPrefixes) {
              if (name.startsWith(webAppPrefix) && name.endsWith(".war")) {
                return true;
              }
            }
            return false;
          }
        });

        if (files != null && files.length > 0) {
          warFile = managementDir.getPath() + File.separator + files[0];
        } else {
          failureReason = "Could not find one of the management web archives : " + webAppPrefixes;
        }
      } catch (FileNotFoundException e) {
        failureReason = "Could not find the management web archive " + e.getMessage();
      }
    }

    if (warFile != null) {
      // DEV-8583: use a lock file in java.io.tmpdir to serialize deployments of the management webapp on a single
      // server
      if (!fileLock()) {
        consoleLogger.warn("Unable to start management server. Try deleting " + TC_MANAGEMENT_API_LOCKFILE.getAbsolutePath() + " and restart");
        return;
      }
      try {
        logger.info("deploying management REST services from archive " + warFile);
        WebAppContext restContext = new WebAppContext();
        restContext.setTempDirectory(warTempDir);
        restContext.setVirtualHosts(Collections.singletonList("@" + CONNECTOR_NAME_MANAGEMENT));

        restContext.setContextPath("/tc-management-api");
        restContext.setWar(warFile);
        restContext.setErrorHandler(new ErrorPageErrorHandler());
        contextHandlerCollection.addHandler(restContext);

        // make sure the REST webapp is started before binding the port
        if (contextHandlerCollection.isStarted()) {
          restContext.start();
          // only bind the port if the agent deployed successfully
          if (restContext.isAvailable()) {
            bindManagementHttpPort(commonL2Config);
          } else {
            logger.warn("Cannot deploy REST management due to agent initialization error", restContext.getUnavailableException());
            consoleLogger.warn("Cannot deploy REST management due to agent initialization error : " + restContext.getUnavailableException());
          }
        }
      } finally {
        fileUnlock();
      }
    } else {
      // there is no more hope of deploying the web app
      consoleLogger.warn("Cannot deploy REST management due to invalid installation dir location");
      consoleLogger.warn(failureReason);
    }
  }

  private boolean fileLock() throws InterruptedException, IOException {
    while (true) {
      // check if the lock file is older than 60s, in that case assume another VM crashed and delete it
      if (TC_MANAGEMENT_API_LOCKFILE.lastModified() != 0L && System.currentTimeMillis() - TC_MANAGEMENT_API_LOCKFILE.lastModified() >= 20000) {
        consoleLogger.debug("removing Management Server lock file after waiting 20 seconds");
        boolean deleted = TC_MANAGEMENT_API_LOCKFILE.delete();
        if(!deleted) {
          return false;
        }
      }

      // check if the file exists and if not, create it. That's atomic across the entire FS.
      boolean created = TC_MANAGEMENT_API_LOCKFILE.createNewFile();
      if (created) {
        return true;
      }

      // if the file exists and is not older than 1 minute, sleep a bit and retry
      Thread.sleep(1000);
    }
  }

  private void fileUnlock() {
    // deleting the file unblocks all processes waiting to create it
    TC_MANAGEMENT_API_LOCKFILE.delete();
  }

  private void setupBasicAuth(final ServletContextHandler context, final String pathSpec,
                              final LoginService loginService, String... roles) {
    Constraint.Builder constaintBuilder = new Constraint.Builder();
    constaintBuilder.roles(roles).authorization(Constraint.Authorization.SPECIFIC_ROLE).name("BASIC");

    ConstraintMapping cm = new ConstraintMapping();
    cm.setConstraint(constaintBuilder.build());
    cm.setPathSpec(pathSpec);

    ConstraintSecurityHandler sh = new ConstraintSecurityHandler();
    sh.setAuthenticator(new BasicAuthenticator());
    sh.setRealmName(TCUserRealm.class.getSimpleName());
    sh.setLoginService(loginService);
    sh.setConstraintMappings(Collections.singletonList(cm));

    context.setSecurityHandler(sh);
  }

  private static void createAndAddServlet(final ServletHandler servletHandler, final String servletClassName,
                                          final String path) {
    ServletHolder holder = servletHandler.addServletWithMapping(servletClassName, path);
    holder.setInitParameter("scratchdir", "jsp"); // avoid jetty from creating a "jsp" directory
    servletHandler.addServlet(holder);
  }

  @Override
  public void dump() {
    if (this.dsoServer != null) {
      this.dsoServer.dump();
    }
  }

  private void registerDSOServer() throws InstanceAlreadyExistsException, MBeanRegistrationException,
      NotCompliantMBeanException, NullPointerException {

    ServerManagementContext mgmtContext = this.dsoServer.getManagementContext();
    ServerConfigurationContext configContext = this.dsoServer.getContext();
    MBeanServer mBeanServer = this.dsoServer.getMBeanServer();
    registerDSOMBeans(mgmtContext, configContext, mBeanServer);
  }

  protected void registerDSOMBeans(ServerManagementContext mgmtContext, ServerConfigurationContext configContext,
                                   MBeanServer mBeanServer) throws NotCompliantMBeanException,
      InstanceAlreadyExistsException, MBeanRegistrationException {
    GCStatsEventPublisher gcStatsPublisher = this.dsoServer.getGcStatsEventPublisher();
    TerracottaOperatorEventHistoryProvider operatorEventHistoryProvider = this.dsoServer
        .getOperatorEventsHistoryProvider();
    DSOMBean dso = new DSO(mgmtContext, configContext, mBeanServer, gcStatsPublisher, operatorEventHistoryProvider,
                           this.dsoServer.getOffheapStats(), this.dsoServer.getStorageStats());
    mBeanServer.registerMBean(dso, L2MBeanNames.DSO);
  }

  protected void unregisterDSOMBeans(MBeanServer mbs) throws MBeanRegistrationException, InstanceNotFoundException {
    mbs.unregisterMBean(L2MBeanNames.DSO);
  }

  // TODO: check that this is not needed then remove
  private TCServerActivationListener activationListener;

  public void setActivationListener(final TCServerActivationListener listener) {
    this.activationListener = listener;
  }

  private static class NullContext implements ConfigurationContext {

    private final StageManager manager;

    public NullContext(final StageManager manager) {
      this.manager = manager;
    }

    @Override
    public TCLogger getLogger(final Class clazz) {
      return TCLogging.getLogger(clazz);
    }

    @Override
    public Stage getStage(final String name) {
      return this.manager.getStage(name);
    }

  }

  private synchronized void notifyShutdown() {
    shutdown = true;
    notifyAll();
  }

  @Override
  public synchronized void waitUntilShutdown() {
    while (!shutdown) {
      try {
        wait();
      } catch (InterruptedException e) {
        throw new AssertionError(e);
      }
    }
  }

  @Override
  public void reloadConfiguration() throws ConfigurationSetupException {
    dsoServer.reloadConfiguration();
  }

  @Override
  public String[] processArguments() {
    return configurationSetupManager.processArguments();
  }

  @Override
  public void dumpClusterState() {
//    if (this.dsoServer != null) {
//      this.dsoServer.dumpClusterState();
//    }
  }

  @Override
  public String getRunningBackup() {
    return dsoServer.getBackupManager().getRunningBackup();
  }

  @Override
  public String getBackupStatus(final String name) throws IOException {
    return dsoServer.getBackupManager().getBackupStatus(name).toString();
  }

  @Override
  public String getBackupFailureReason(String name) throws IOException {
    return dsoServer.getBackupManager().getBackupFailureReason(name);
  }

  @Override
  public Map<String, String> getBackupStatuses() throws IOException {
    Map<String, String> result = new HashMap<String, String>();
    Map<String, ? extends Object> backups = dsoServer.getBackupManager().getBackupStatuses();
    for (String name : backups.keySet()) {
      result.put(name, backups.get(name).toString());
    }
    return result;
  }

  @Override
  public void backup(final String name) throws IOException {
    dsoServer.getBackupManager().backup(name);
  }

  @Override
  public String getResourceState() {
    return dsoServer.getResourceManager().getState().name();
  }

  @Override
  public boolean isWaitingForFailOverAction() {
    return this.sbpResolver.isWaitingForFailOverAction();
  }

  @Override
  public void performFailOverAction(FailOverAction action) {
    this.sbpResolver.performFailOverAction(action);
  }

  @Override
  public boolean isSecure() {
    return securityManager != null;
  }

  @Override
  public String getSecurityServiceLocation() {
    if (configurationSetupManager.getSecurity() == null) { return null; }
    return configurationSetupManager.getSecurity().getSecurityServiceLocation();
  }

  @Override
  public Integer getSecurityServiceTimeout() {
    if (configurationSetupManager.getSecurity() == null) { return null; }
    return configurationSetupManager.getSecurity().getSecurityServiceTimeout();
  }

  @Override
  public String getSecurityHostname() {
    String securityHostname = null;
    if (configurationSetupManager.getSecurity() != null) {
      securityHostname = configurationSetupManager.getSecurity().getSecurityHostname();
    }
    if (securityHostname == null) {
      securityHostname = configurationSetupManager.commonl2Config().host();
    }
    return securityHostname;
  }

  @Override
  public String getIntraL2Username() {
    if (!isSecure()) { return null; }
    return securityManager.getIntraL2Username();
  }

  private static final class TCUserRealm extends AbstractLoginService {
    private final TCSecurityManager securityManager;

    TCUserRealm(final TCSecurityManager securityManager) {
      this.securityManager = securityManager;
      setName(this.getClass().getSimpleName());
    }

    @Override
    public UserIdentity login(final String username, final Object credentials, Request request, Function<Boolean, Session> getOrCreateSession) {
      final TCPrincipal userPrincipal = (TCPrincipal) securityManager.authenticate(username, ((String) credentials).toCharArray());
      if (userPrincipal == null) {
        return null;
      }
      String[] roles = userPrincipal.getRoles().stream().map(Object::toString).collect(Collectors.toList()).toArray(new String[]{});

      Subject subject = new Subject();
      subject.getPrincipals().add(userPrincipal);
      if (roles.length > 0) {
        for (String role : roles) {
          subject.getPrincipals().add(new RolePrincipal(role));
        }
      }
      subject.setReadOnly();

      UserIdentity user = _identityService.newUserIdentity(subject, userPrincipal, roles);
      return user;
    }

    @Override
    protected UserPrincipal loadUserInfo(String username) {
      throw new UnsupportedOperationException("Should not be called!");
    }

    @Override
    protected List<RolePrincipal> loadRoleInfo(UserPrincipal user) {
      throw new UnsupportedOperationException("Should not be called!");
    }
  }

  private String[] getVulnerableProtocols() {
    String csProtocols = TCPropertiesImpl.getProperties().getProperty(TCPropertiesConsts.DISABLED_SECURE_PROTOCOLS);
    HashSet<String> protocolSet = new HashSet<String>();
    protocolSet.addAll(Arrays.asList(csProtocols.split(",")));
    return protocolSet.toArray(new String[protocolSet.size()]);
  }

  private String[] getVulnerableCipherSuites() {
    String exCiphers = TCPropertiesImpl.getProperties().getProperty(TCPropertiesConsts.DISABLED_CIPHER_SUITES);
    HashSet<String> cipherSet = new HashSet<String>();
    cipherSet.addAll(Arrays.asList(exCiphers.split(",")));
    return cipherSet.toArray(new String[cipherSet.size()]);
  }
}
