/*
 * All content copyright (c) 2003-2008 Terracotta, Inc., except as may otherwise be noted in a separate copyright
 * notice. All rights reserved.
 */
package com.tc.object.config.schema;

import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlInteger;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlString;

import com.tc.config.schema.ActiveServerGroupConfigObject;
import com.tc.config.schema.ActiveServerGroupsConfigObject;
import com.tc.config.schema.BaseNewConfigObject;
import com.tc.config.schema.NewHaConfigObject;
import com.tc.config.schema.UpdateCheckConfigObject;
import com.tc.config.schema.context.ConfigContext;
import com.tc.config.schema.defaults.DefaultValueProvider;
import com.tc.config.schema.dynamic.ParameterSubstituter;
import com.tc.config.schema.setup.ConfigurationSetupException;
import com.tc.util.Assert;
import com.terracottatech.config.BindPort;
import com.terracottatech.config.DsoServerData;
import com.terracottatech.config.GarbageCollection;
import com.terracottatech.config.MirrorGroup;
import com.terracottatech.config.Offheap;
import com.terracottatech.config.Persistence;
import com.terracottatech.config.PersistenceMode;
import com.terracottatech.config.Server;
import com.terracottatech.config.Servers;
import com.terracottatech.config.PersistenceMode.Enum;
import com.terracottatech.config.TcConfigDocument.TcConfig;

import java.io.File;

/**
 * The standard implementation of {@link NewL2DSOConfig}.
 */
public class NewL2DSOConfigObject extends BaseNewConfigObject implements NewL2DSOConfig {
  private static final String     WILDCARD_IP                           = "0.0.0.0";
  public static final short       DEFAULT_JMXPORT_OFFSET_FROM_DSOPORT   = 10;
  public static final short       DEFAULT_GROUPPORT_OFFSET_FROM_DSOPORT = 20;
  public static final int         MIN_PORTNUMBER                        = 0x0FFF;
  public static final int         MAX_PORTNUMBER                        = 0xFFFF;

  private final Persistence       persistence;
  private final Offheap           offHeapConfig;
  private final GarbageCollection garbageCollection;
  private final BindPort          dsoPort;
  private final BindPort          l2GroupPort;
  private final int               clientReconnectWindow;
  private final String            host;
  private final String            serverName;
  private final String            bind;

  public NewL2DSOConfigObject(ConfigContext context) {
    super(context);

    this.context.ensureRepositoryProvides(Server.class);
    Server server = (Server) this.context.bean();
    this.persistence = server.getDso().getPersistence();

    Assert.assertTrue((this.persistence.getMode() == PersistenceMode.PERMANENT_STORE)
                      || (this.persistence.getMode() == PersistenceMode.TEMPORARY_SWAP_ONLY));

    this.garbageCollection = server.getDso().getGarbageCollection();
    this.clientReconnectWindow = server.getDso().getClientReconnectWindow();

    this.bind = server.getBind();
    this.host = server.getHost();
    this.serverName = server.getName();

    this.dsoPort = server.getDsoPort();
    this.l2GroupPort = server.getL2GroupPort();
    this.offHeapConfig = server.getDso().getPersistence().getOffheap();
  }

  public Offheap offHeapConfig() {
    return this.offHeapConfig;
  }

  public BindPort dsoPort() {
    return this.dsoPort;
  }

  public BindPort l2GroupPort() {
    return this.l2GroupPort;
  }

  public String host() {
    return host;
  }

  public String serverName() {
    return this.serverName;
  }

  public Persistence getPersistence() {
    return this.persistence;
  }

  public GarbageCollection garbageCollection() {
    return this.garbageCollection;
  }

  public int clientReconnectWindow() {
    return this.clientReconnectWindow;
  }

  public String bind() {
    return this.bind;
  }

  public static void initializeServers(TcConfig config, DefaultValueProvider defaultValueProvider,
                                       File directoryLoadedFrom) throws XmlException, ConfigurationSetupException {
    if (!config.isSetServers()) {
      config.addNewServers();
    }
    Servers servers = config.getServers();
    if (servers.getServerArray().length == 0) {
      servers.addNewServer();
    }

    for (int i = 0; i < servers.sizeOfServerArray(); i++) {
      Server server = servers.getServerArray(i);
      initializeServerBind(server, defaultValueProvider);
      initializeDsoPort(server, defaultValueProvider);
      initializeJmxPort(server, defaultValueProvider);
      initializeL2GroupPort(server, defaultValueProvider);
      // CDV-1220: per our documentation in the schema itself, host is supposed to default to server name or '%i'
      // and name is supposed to default to 'host:dso-port'
      initializeNameAndHost(server, defaultValueProvider);
      initializeDataDirectory(server, defaultValueProvider, directoryLoadedFrom);
      initializeLogsDirectory(server, defaultValueProvider, directoryLoadedFrom);
      initializeDataBackupDirectory(server, defaultValueProvider, directoryLoadedFrom);
      initializeStatisticsDirectory(server, defaultValueProvider, directoryLoadedFrom);
      initializeDso(server, defaultValueProvider);
    }

    NewHaConfigObject.initializeHa(servers, defaultValueProvider);
    initializeMirrorGroups(servers, defaultValueProvider);
    UpdateCheckConfigObject.initializeUpdateCheck(servers, defaultValueProvider);
  }

  private static void initializeServerBind(Server server, DefaultValueProvider defaultValueProvider) {
    if (!server.isSetBind() || server.getBind().trim().length() == 0) {
      server.setBind(WILDCARD_IP);
    }
    server.setBind(ParameterSubstituter.substitute(server.getBind()));
  }

  private static void initializeDsoPort(Server server, DefaultValueProvider defaultValueProvider) throws XmlException {
    XmlObject[] dsoPorts = server.selectPath("dso-port");
    Assert.assertTrue(dsoPorts.length <= 1);
    if (!server.isSetDsoPort()) {
      final XmlInteger defaultValue = (XmlInteger) defaultValueProvider.defaultFor(server.schemaType(), "dso-port");
      int defaultDsoPort = defaultValue.getBigIntegerValue().intValue();
      BindPort dsoPort = server.addNewDsoPort();
      dsoPort.setIntValue(defaultDsoPort);
      dsoPort.setBind(server.getBind());
    } else if (!server.getDsoPort().isSetBind()) {
      server.getDsoPort().setBind(server.getBind());
    }
  }

  private static void initializeJmxPort(Server server, DefaultValueProvider defaultValueProvider) {
    XmlObject[] jmxPorts = server.selectPath("jmx-port");
    Assert.assertTrue(jmxPorts.length <= 1);
    if (!server.isSetJmxPort()) {
      BindPort jmxPort = server.addNewJmxPort();
      int tempJmxPort = server.getDsoPort().getIntValue() + DEFAULT_JMXPORT_OFFSET_FROM_DSOPORT;
      int defaultJmxPort = ((tempJmxPort <= MAX_PORTNUMBER) ? tempJmxPort : (tempJmxPort % MAX_PORTNUMBER)
                                                                            + MIN_PORTNUMBER);

      jmxPort.setIntValue(defaultJmxPort);
      jmxPort.setBind(server.getBind());
    } else if (!server.getJmxPort().isSetBind()) {
      server.getJmxPort().setBind(server.getBind());
    }
  }

  private static void initializeL2GroupPort(Server server, DefaultValueProvider defaultValueProvider) {
    XmlObject[] l2GroupPorts = server.selectPath("l2-group-port");
    Assert.assertTrue(l2GroupPorts.length <= 1);
    if (!server.isSetL2GroupPort()) {
      BindPort l2GrpPort = server.addNewL2GroupPort();
      int tempGroupPort = server.getDsoPort().getIntValue() + DEFAULT_GROUPPORT_OFFSET_FROM_DSOPORT;
      int defaultGroupPort = ((tempGroupPort <= MAX_PORTNUMBER) ? (tempGroupPort) : (tempGroupPort % MAX_PORTNUMBER)
                                                                                    + MIN_PORTNUMBER);
      l2GrpPort.setIntValue(defaultGroupPort);
      l2GrpPort.setBind(server.getBind());
    } else if (!server.getL2GroupPort().isSetBind()) {
      server.getL2GroupPort().setBind(server.getBind());
    }
  }

  private static void initializeNameAndHost(Server server, DefaultValueProvider defaultValueProvider) {
    if (!server.isSetHost() || server.getHost().trim().length() == 0) {
      if (!server.isSetName()) {
        server.setHost("%i");
      } else {
        server.setHost(server.getName());
      }
    }

    if (!server.isSetName() || server.getName().trim().length() == 0) {
      int dsoPort = server.getDsoPort().getIntValue();
      server.setName(server.getHost() + (dsoPort > 0 ? ":" + dsoPort : ""));
    }

    // CDV-77: add parameter expansion to the <server> attributes ('host' and 'name')
    server.setHost(ParameterSubstituter.substitute(server.getHost()));
    server.setName(ParameterSubstituter.substitute(server.getName()));
  }

  private static void initializeDataDirectory(Server server, DefaultValueProvider defaultValueProvider,
                                              File directoryLoadedFrom) throws XmlException {
    if (!server.isSetData()) {
      final XmlString defaultValue = (XmlString) defaultValueProvider.defaultFor(server.schemaType(), "data");
      String substitutedString = ParameterSubstituter.substitute(defaultValue.getStringValue());

      server.setData(new File(directoryLoadedFrom, substitutedString).getAbsolutePath());
    } else {
      server.setData(ParameterSubstituter.substitute(server.getData()));
    }
  }

  private static void initializeLogsDirectory(Server server, DefaultValueProvider defaultValueProvider,
                                              File directoryLoadedFrom) throws XmlException {
    if (!server.isSetLogs()) {
      final XmlString defaultValue = (XmlString) defaultValueProvider.defaultFor(server.schemaType(), "logs");
      String substitutedString = ParameterSubstituter.substitute(defaultValue.getStringValue());
      server.setLogs(new File(directoryLoadedFrom, substitutedString).getAbsolutePath());
    } else {
      server.setLogs(ParameterSubstituter.substitute(server.getLogs()));
    }
  }

  private static void initializeDataBackupDirectory(Server server, DefaultValueProvider defaultValueProvider,
                                                    File directoryLoadedFrom) throws XmlException {
    if (!server.isSetDataBackup()) {
      final XmlString defaultValue = (XmlString) defaultValueProvider.defaultFor(server.schemaType(), "data-backup");
      String substitutedString = ParameterSubstituter.substitute(defaultValue.getStringValue());
      server.setDataBackup(new File(directoryLoadedFrom, substitutedString).getAbsolutePath());
    } else {
      server.setDataBackup(ParameterSubstituter.substitute(server.getDataBackup()));
    }
  }

  private static void initializeStatisticsDirectory(Server server, DefaultValueProvider defaultValueProvider,
                                                    File directoryLoadedFrom) throws XmlException {
    if (!server.isSetStatistics()) {
      final XmlString defaultValue = (XmlString) defaultValueProvider.defaultFor(server.schemaType(), "statistics");
      String substitutedString = ParameterSubstituter.substitute(defaultValue.getStringValue());
      server.setStatistics(new File(directoryLoadedFrom, substitutedString).getAbsolutePath());
    } else {
      server.setStatistics(ParameterSubstituter.substitute(server.getStatistics()));
    }
  }

  private static void initializeDso(Server server, DefaultValueProvider defaultValueProvider) throws XmlException {
    if (!server.isSetDso()) {
      DsoServerData dso = server.addNewDso();
      initializeDefaultPersistence(server, defaultValueProvider);
      dso.setClientReconnectWindow(getDefaultReconnectWindow(server, defaultValueProvider));
      initializeDefaultGarbageCollection(server, defaultValueProvider);
    } else {
      DsoServerData dso = server.getDso();

      if (!dso.isSetPersistence()) {
        dso.addNewPersistence().setMode(getDefaultPersistence(server, defaultValueProvider));
        initializeDefaultOffHeap(server, defaultValueProvider);
      } else {
        Persistence persistence = dso.getPersistence();
        if (!persistence.isSetMode()) {
          persistence.setMode(getDefaultPersistence(server, defaultValueProvider));
        }

        if (!persistence.isSetOffheap()) {
          initializeDefaultOffHeap(server, defaultValueProvider);
        } else {
          Offheap offHeap = persistence.getOffheap();
          if (!offHeap.isSetEnabled()) {
            offHeap.setEnabled(getDefaultOffHeapEnabled(server, defaultValueProvider));
          }

          if (!offHeap.isSetMaxDataSize()) {
            offHeap.setMaxDataSize(getDefaultOffHeapMaxDataSize(server, defaultValueProvider));
          }
        }
      }

      if (!dso.isSetClientReconnectWindow()) {
        dso.setClientReconnectWindow(getDefaultReconnectWindow(server, defaultValueProvider));
      }

      if (!dso.isSetGarbageCollection()) {
        initializeDefaultGarbageCollection(server, defaultValueProvider);
      } else {
        GarbageCollection gc = dso.getGarbageCollection();
        if (!gc.isSetEnabled()) {
          gc.setEnabled(getDefaultGarbageCollectionEnabled(server, defaultValueProvider));
        }

        if (!gc.isSetVerbose()) {
          gc.setVerbose(getDefaultGarbageCollectionVerbose(server, defaultValueProvider));
        }

        if (!gc.isSetInterval()) {
          gc.setInterval(getDefaultGarbageCollectionInterval(server, defaultValueProvider));
        }
      }
    }
  }

  private static void initializeDefaultPersistence(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    Assert.assertTrue(server.isSetDso());
    Assert.assertFalse(server.getDso().isSetPersistence());
    Persistence persistence = server.getDso().addNewPersistence();

    persistence.setMode(getDefaultPersistence(server, defaultValueProvider));
    initializeDefaultOffHeap(server, defaultValueProvider);
  }

  private static void initializeDefaultGarbageCollection(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    Assert.assertTrue(server.isSetDso());
    Assert.assertFalse(server.getDso().isSetGarbageCollection());

    GarbageCollection gc = server.getDso().addNewGarbageCollection();
    gc.setEnabled(getDefaultGarbageCollectionEnabled(server, defaultValueProvider));
    gc.setVerbose(getDefaultGarbageCollectionVerbose(server, defaultValueProvider));
    gc.setInterval(getDefaultGarbageCollectionInterval(server, defaultValueProvider));
  }

  private static Enum getDefaultPersistence(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    XmlString xmlObject = (XmlString) defaultValueProvider.defaultFor(server.schemaType(), "dso/persistence/mode");
    Assert.assertNotNull(xmlObject);
    Assert.assertTrue(xmlObject.getStringValue().equals(PersistenceMode.PERMANENT_STORE.toString())
                      || xmlObject.getStringValue().equals(PersistenceMode.TEMPORARY_SWAP_ONLY.toString()));
    if (xmlObject.getStringValue().equals(PersistenceMode.PERMANENT_STORE.toString())) return PersistenceMode.PERMANENT_STORE;
    return PersistenceMode.TEMPORARY_SWAP_ONLY;
  }

  private static void initializeDefaultOffHeap(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    Assert.assertTrue(server.isSetDso());
    Assert.assertTrue(server.getDso().isSetPersistence());
    Offheap offHeap = server.getDso().getPersistence().addNewOffheap();
    offHeap.setEnabled(getDefaultOffHeapEnabled(server, defaultValueProvider));
    offHeap.setMaxDataSize(getDefaultOffHeapMaxDataSize(server, defaultValueProvider));
  }

  private static boolean getDefaultOffHeapEnabled(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    return ((XmlBoolean) defaultValueProvider.defaultFor(server.schemaType(), "dso/persistence/offheap/enabled"))
        .getBooleanValue();
  }

  private static String getDefaultOffHeapMaxDataSize(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    return ((XmlString) defaultValueProvider.defaultFor(server.schemaType(), "dso/persistence/offheap/maxDataSize"))
        .getStringValue();
  }

  private static int getDefaultReconnectWindow(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    return ((XmlInteger) defaultValueProvider.defaultFor(server.schemaType(), "dso/client-reconnect-window"))
        .getBigIntegerValue().intValue();
  }

  private static boolean getDefaultGarbageCollectionEnabled(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    return ((XmlBoolean) defaultValueProvider.defaultFor(server.schemaType(), "dso/garbage-collection/enabled"))
        .getBooleanValue();
  }

  private static boolean getDefaultGarbageCollectionVerbose(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    return ((XmlBoolean) defaultValueProvider.defaultFor(server.schemaType(), "dso/garbage-collection/verbose"))
        .getBooleanValue();
  }

  private static int getDefaultGarbageCollectionInterval(Server server, DefaultValueProvider defaultValueProvider)
      throws XmlException {
    return ((XmlInteger) defaultValueProvider.defaultFor(server.schemaType(), "dso/garbage-collection/interval"))
        .getBigIntegerValue().intValue();
  }

  private static void initializeMirrorGroups(Servers servers, DefaultValueProvider defaultValueProvider)
      throws ConfigurationSetupException {
    Assert.assertTrue(servers.isSetHa());
    if (!servers.isSetMirrorGroups()) {
      ActiveServerGroupsConfigObject.createDefaultServerMirrorGroups(servers, defaultValueProvider);
    } else {
      MirrorGroup[] mirrorGroups = servers.getMirrorGroups().getMirrorGroupArray();
      if (mirrorGroups.length == 0) {
        ActiveServerGroupConfigObject.createDefaultMirrorGroup(servers, servers.getHa());
      }

      for (MirrorGroup mirrorGroup : mirrorGroups) {
        if (!mirrorGroup.isSetHa()) {
          mirrorGroup.setHa(servers.getHa());
        } else {
          NewHaConfigObject.checkAndInitializeHa(mirrorGroup.getHa(), servers.getHa());
        }
      }
    }
  }

}
