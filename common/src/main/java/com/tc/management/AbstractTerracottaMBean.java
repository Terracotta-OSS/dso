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
package com.tc.management;

import com.tc.exception.TCRuntimeException;
import com.tc.logging.TCLogger;
import com.tc.logging.TCLogging;
import com.tc.properties.TCPropertiesConsts;
import com.tc.properties.TCPropertiesImpl;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.StandardMBean;

public abstract class AbstractTerracottaMBean extends StandardMBean implements NotificationEmitter, TerracottaMBean {

  private static final ResourceBundle            DEFAULT_BUNDLE          = getBundleForMBean(TerracottaMBean.class,
                                                                                             TCLogging
                                                                                                 .getLogger(TerracottaMBean.class));

  private static final boolean                   ENABLED                 = TCPropertiesImpl
                                                                             .getProperties()
                                                                             .getBoolean(TCPropertiesConsts.TC_MANAGEMENT_MBEANS_ENABLED);

  private final TCLogger                         logger;
  private final ResourceBundle                   beanBundle;
  private final boolean                          isNotificationBroadcaster;

  // NOTE: The use of NotificationBroadcasterSupport has been removed and re-implemented internally
  // to avoid issues with JDK logging (DEV-421)
  private final List                             notificationListeners   = new CopyOnWriteArrayList();
  private boolean                                isActive;

  protected static final MBeanNotificationInfo[] EMPTY_NOTIFICATION_INFO = {};

  protected AbstractTerracottaMBean(final Class mBeanInterface, final boolean isNotificationBroadcaster)
      throws NotCompliantMBeanException {
    this(mBeanInterface, isNotificationBroadcaster, ENABLED);
  }

  protected AbstractTerracottaMBean(final Class mBeanInterface, final boolean isNotificationBroadcaster,
                                    final boolean isActive) throws NotCompliantMBeanException {
    super(mBeanInterface);
    this.logger = TCLogging.getLogger(mBeanInterface);
    this.beanBundle = getBundleForMBean(mBeanInterface, logger);
    this.isNotificationBroadcaster = isNotificationBroadcaster;
    this.isActive = isActive;
  }

  @Override
  public final String getInterfaceClassName() {
    return getMBeanInterface().getName();
  }

  public final boolean hasListeners() {
    return !notificationListeners.isEmpty();
  }

  @Override
  public void addNotificationListener(final NotificationListener listener, final NotificationFilter filter,
                                      final Object obj) {
    notificationListeners.add(new Listener(listener, filter, obj));
  }

  @Override
  public MBeanNotificationInfo[] getNotificationInfo() {
    if (isNotificationBroadcaster()) {
      final RuntimeException re = new TCRuntimeException("MBean error: this MBean[" + getClass().getName()
                                                         + "] must override getNotificationInfo() since"
                                                         + " it broadcasts notifications");
      throw re;
    }
    return new MBeanNotificationInfo[0];
  }

  @Override
  public void removeNotificationListener(final NotificationListener listener, final NotificationFilter filter,
                                         final Object obj) throws ListenerNotFoundException {
    boolean removed = false;

    for (Iterator i = notificationListeners.iterator(); i.hasNext();) {
      Listener lsnr = (Listener) i.next();
      if (lsnr.listener == listener && lsnr.filter == filter && lsnr.handback == obj) {
        removed = true;
        notificationListeners.remove(lsnr);
      }
    }

    if (!removed) { throw new ListenerNotFoundException(); }
  }

  @Override
  public void removeNotificationListener(final NotificationListener listener) throws ListenerNotFoundException {
    boolean removed = false;

    for (Iterator i = notificationListeners.iterator(); i.hasNext();) {
      Listener lsnr = (Listener) i.next();
      if (lsnr.listener == listener) {
        removed = true;
        notificationListeners.remove(lsnr);
      }
    }

    if (!removed) { throw new ListenerNotFoundException(); }
  }

  public final void sendNotification(final Notification notification) {
    if (isEnabled()) {
      for (Iterator i = notificationListeners.iterator(); i.hasNext();) {
        Listener lsnr = (Listener) i.next();

        if (lsnr.filter == null || lsnr.filter.isNotificationEnabled(notification)) {
          lsnr.listener.handleNotification(notification, lsnr.handback);
        }
      }
    }
  }

  @Override
  public final boolean isNotificationBroadcaster() {
    return isNotificationBroadcaster;
  }

  @Override
  public final void enable() {
    setState(true);
  }

  @Override
  public final void disable() {
    setState(false);
  }

  private synchronized void setState(final boolean isActive) {
    if (this.isActive && !isActive) {
      reset();
    }
    final boolean previous = this.isActive;
    this.isActive = isActive;
    if (previous != isActive) {
      enabledStateChanged();
    }
  }

  protected synchronized void enabledStateChanged() {
    //
  }

  @Override
  public final synchronized boolean isEnabled() {
    return isActive;
  }

  /**
   * As far as I can tell (at least with the Sun implementation), most if not all of the {@link StandardMBean}
   * customization hooks for descriptions come through this one method. Since we are using a {@link ResourceBundle} we
   * don't really need to worry about the exact type of the feature (only the name), so we should be able to get away
   * with overriding only this particular method to supply descriptions.
   */
  @Override
  protected String getDescription(final MBeanFeatureInfo featureInfo) {
    final String name = featureInfo.getName();
    String bundleDescription = null;
    if (beanBundle != null) {
      try {
        bundleDescription = beanBundle.getString(name);
      } catch (MissingResourceException mre) {
        if (DEFAULT_BUNDLE != null) {
          try {
            bundleDescription = DEFAULT_BUNDLE.getString(name);
          } catch (MissingResourceException defaultMre) {
            // We tried :)
          }
        }
      } catch (Throwable t) {
        // Not important enough to do anything about, but the log might reveal an operational problem
        logger.warn("Unexpected error while trying to retrieve feature description[" + name + "]", t);
      } finally {
        if (bundleDescription == null) {
          bundleDescription = super.getDescription(featureInfo);
        }
      }
    }
    return bundleDescription;
  }

  private static ResourceBundle getBundleForMBean(final Class mBeanInterface, final TCLogger logger) {
    ResourceBundle bundle = null;
    try {
      bundle = ResourceBundle.getBundle(mBeanInterface.getName(), Locale.getDefault(),
                                        AbstractTerracottaMBean.class.getClassLoader());
    } catch (MissingResourceException mre) {
      /* Caller must deal with null return value when missing */
    } catch (Throwable t) {
      logger.warn("Unexpected error loading resource bundle for MBean " + mBeanInterface.getName(), t);
    }
    return bundle;
  }

  private static class Listener {
    private final NotificationListener listener;
    private final NotificationFilter   filter;
    private final Object               handback;

    Listener(NotificationListener listener, NotificationFilter filter, Object obj) {
      this.listener = listener;
      this.filter = filter;
      this.handback = obj;
    }

  }

}
