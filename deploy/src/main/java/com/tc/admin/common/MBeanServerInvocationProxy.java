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
package com.tc.admin.common;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.NotificationEmitter;
import javax.management.ObjectName;
import javax.swing.SwingUtilities;

/*
 * This should be used to get any MBean proxies used by a Swing client so we can get a report about JMX calls being
 * invoked on the Swing event loop. That report is off by default.
 */

public class MBeanServerInvocationProxy extends MBeanServerInvocationHandler {
  private static final boolean reportViolators = false;

  public MBeanServerInvocationProxy(MBeanServerConnection connection, ObjectName objectName) {
    super(connection, objectName);
  }

  public static <T> T newMBeanProxy(MBeanServerConnection connection, ObjectName objectName, Class<T> interfaceClass,
                                    boolean notificationBroadcaster) {
    final InvocationHandler handler = new MBeanServerInvocationProxy(connection, objectName);
    final Class[] interfaces;
    if (notificationBroadcaster) {
      interfaces = new Class[] { interfaceClass, NotificationEmitter.class };
    } else {
      interfaces = new Class[] { interfaceClass };
    }
    Object proxy = Proxy.newProxyInstance(interfaceClass.getClassLoader(), interfaces, handler);
    return interfaceClass.cast(proxy);
  }

  @Override
  public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    if (reportViolators && SwingUtilities.isEventDispatchThread()) {
      new Exception("MBean invoked in Swing event dispatch thread").printStackTrace();
    }
    return super.invoke(proxy, method, args);
  }
}
