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
package com.terracotta.toolkit.express;

import org.terracotta.toolkit.ToolkitRuntimeException;

import com.terracotta.toolkit.express.loader.Util;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

class TerracottaInternalClientImpl implements TerracottaInternalClient {
  // TODO Break up config so that only EE code needs these props.
  public static final String TERRACOTTA_CUSTOM_SECRET_PROVIDER_PROP = "com.terracotta.SecretProvider";

  private static final String CLIENT_HANDLE_IMPL                                               = "com.terracotta.toolkit.express.ClientHandleImpl";
  public static final String  SECRET_PROVIDER                                                  = "com.terracotta.express.SecretProvider";

  private static final String EE_SECRET_DELEGATE                                               = "com.terracotta.toolkit.DelegatingSecretProvider";
  private static final String SECRET_PROVIDER_CLASS                                            = "org.terracotta.toolkit.SecretProvider";

  private static final String EHCACHE_EXPRESS_ENTERPRISE_TERRACOTTA_CLUSTERED_INSTANCE_FACTORY = "net.sf.ehcache.terracotta.ExpressEnterpriseTerracottaClusteredInstanceFactory";
  private static final String EHCACHE_EXPRESS_TERRACOTTA_CLUSTERED_INSTANCE_FACTORY            = "net.sf.ehcache.terracotta.StandaloneTerracottaClusteredInstanceFactory";

  static class ClientShutdownException extends Exception {
    //
  }

  private final ClusteredStateLoader  clusteredStateLoader;
  private final AppClassLoader        appClassLoader;
  private final ClientCreatorCallable clientCreator;
  private final Set<String>           tunneledMBeanDomains = new HashSet<String>();
  private volatile ClientHandle       clientHandle;
  private volatile boolean            shutdown             = false;
  private volatile boolean            isInitialized        = false;

  TerracottaInternalClientImpl(String tcConfig, boolean isUrlConfig, ClassLoader appLoader, boolean rejoinEnabled,
                               Set<String> tunneledMBeanDomains, final String productId, final String clientName, Map<String, Object> env) {
    if (tunneledMBeanDomains != null) {
      this.tunneledMBeanDomains.addAll(tunneledMBeanDomains);
    }
    try {
      this.appClassLoader = new AppClassLoader(appLoader);
      this.clusteredStateLoader = createClusteredStateLoader(appLoader);

      Class bootClass = clusteredStateLoader.loadClass(CreateClient.class.getName());
      Constructor<?> cstr = bootClass.getConstructor(String.class, Boolean.TYPE, ClassLoader.class, Boolean.TYPE,
                                                     String.class, String.class, Map.class);

      if (isUrlConfig && isRequestingSecuredEnv(tcConfig)) {
        if (env != null) {
          env.put(TERRACOTTA_CUSTOM_SECRET_PROVIDER_PROP,
                  newSecretProviderDelegate(clusteredStateLoader, env.get(TerracottaInternalClientImpl.SECRET_PROVIDER)));
        }
      }
      Callable<ClientCreatorCallable> boot = (Callable<ClientCreatorCallable>) cstr.newInstance(tcConfig, isUrlConfig,
                                                                                      clusteredStateLoader,
                                                                                      rejoinEnabled, productId, clientName, env);
      this.clientCreator = boot.call();
    } catch (Exception e) {
      throw new ToolkitRuntimeException(e);
    }
  }

  @Override
  public synchronized void init() {
    if (isInitialized) { return; }

    try {
      Class clientHandleImpl = clusteredStateLoader.loadClass(CLIENT_HANDLE_IMPL);
      clientHandle = (ClientHandle) clientHandleImpl.getConstructor(Object.class).newInstance(clientCreator.call());

      isInitialized = true;
      join(tunneledMBeanDomains);
    } catch (InvocationTargetException e) {
      throw new RuntimeException(e.getCause());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public Object getPlatformService() {
    return clientHandle.getPlatformService();
  }

  @Override
  public Object getAbortableOperationManager() {
    return clientCreator.getAbortableOperationManager();
  }

  @Override
  public String getUuid() {
    return clientCreator.getUuid();
  }

  private synchronized void join(Set<String> tunnelledMBeanDomainsParam) throws ClientShutdownException {
    if (shutdown) throw new ClientShutdownException();

    if (isInitialized) {
      clientHandle.activateTunnelledMBeanDomains(tunnelledMBeanDomainsParam);
    } else {
      if (tunnelledMBeanDomainsParam != null) {
        this.tunneledMBeanDomains.addAll(tunnelledMBeanDomainsParam);
      }
    }
  }

  @Override
  public <T> T instantiate(String className, Class[] cstrArgTypes, Object[] cstrArgs) throws Exception {
    try {
      Class clazz = clusteredStateLoader.loadClass(className);
      Constructor cstr = clazz.getConstructor(cstrArgTypes);
      return (T) cstr.newInstance(cstrArgs);
    } catch (InvocationTargetException e) {
      Throwable targetEx = e.getTargetException();
      throw (targetEx instanceof ToolkitRuntimeException) ? (ToolkitRuntimeException) targetEx
          : new ToolkitRuntimeException(targetEx);
    }
  }

  @Override
  public Class loadClass(String className) throws ClassNotFoundException {
    return clusteredStateLoader.loadClass(className);
  }

  @Override
  public synchronized boolean isShutdown() {
    return shutdown;
  }

  @Override
  public synchronized void shutdown() {
    shutdown = true;
    try {
      if (clientHandle != null) {
        clientHandle.shutdown();
      }
    } finally {
      clientHandle = null;
      appClassLoader.clear();
    }
  }

  private byte[] getClassBytes(Class klass) {
    ClassLoader loader = getClass().getClassLoader();
    String res = klass.getName().replace('.', '/').concat(".class");
    try {
      return Util.extract(loader.getResourceAsStream(res));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static boolean isEmbeddedEhcacheRequired() {
    // ehcache-core needs to be around
    try {
      Class.forName("net.sf.ehcache.CacheManager");
    } catch (ClassNotFoundException e) {
      return true;
    }
    // One of the ClusteredInstanceFactory classes need to be around (ehcache-terracotta)
    try {
      Class.forName(EHCACHE_EXPRESS_TERRACOTTA_CLUSTERED_INSTANCE_FACTORY);
      return false;
    } catch (ClassNotFoundException e) {
      try {
        Class.forName(EHCACHE_EXPRESS_ENTERPRISE_TERRACOTTA_CLUSTERED_INSTANCE_FACTORY);
        return false;
      } catch (ClassNotFoundException e2) {
        return true;
      }
    }
  }

  private ClusteredStateLoader createClusteredStateLoader(ClassLoader appLoader) {
    ClusteredStateLoader loader = null;
    URL devmodeUrl = DevmodeClusteredStateLoader.devModeResource();
    if (devmodeUrl != null) {
      loader = new DevmodeClusteredStateLoader(devmodeUrl, appClassLoader, isEmbeddedEhcacheRequired());
    } else {
      loader = new ClusteredStateLoaderImpl(appClassLoader, isEmbeddedEhcacheRequired());
    }
    loader.addExtraClass(ClientHandleImpl.class.getName(), getClassBytes(ClientHandleImpl.class));
    loader.addExtraClass(CreateClient.class.getName(), getClassBytes(CreateClient.class));
    loader.addExtraClass(CreateClient.ClientCreatorCallableImpl.class.getName(),
                         getClassBytes(CreateClient.ClientCreatorCallableImpl.class));
    return loader;
  }

  private static boolean isRequestingSecuredEnv(final String tcConfig) {
    return tcConfig.contains("@");
  }

  private static Object newSecretProviderDelegate(final ClassLoader loader, final Object backEnd) {
    try {
      Class<?> customClass = Class.forName(SECRET_PROVIDER_CLASS);
      Class<?> tkClass = loader.loadClass(EE_SECRET_DELEGATE);
      return tkClass.getConstructor(customClass).newInstance(backEnd);
    } catch (Exception e) {
      String message;
      if (backEnd != null) {
        message = "Couldn't wrap the custom impl. " + backEnd.getClass().getName() + " in an instance of "
                  + EE_SECRET_DELEGATE;
      } else {
        message = "Couldn't fetch keychain password from the console";
      }
      throw new RuntimeException(message, e);
    }
  }

  @Override
  public boolean isOnline() {
    ClientHandle handle = clientHandle;
    return handle == null ? false : handle.isOnline();
  }

  @Override
  public boolean isInitialized() {
    return isInitialized;
  }
}
