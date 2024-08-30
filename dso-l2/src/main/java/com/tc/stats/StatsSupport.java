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
package com.tc.stats;

import com.tc.stats.statistics.Statistic;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;

// TODO: remove me

public class StatsSupport extends NotificationBroadcasterSupport implements Serializable {
  private final Map        statMap        = new HashMap();
  private final AtomicLong sequenceNumber = new AtomicLong();

  public synchronized void addStatistic(String id, Statistic statistic) {
    statMap.put(id, statistic);
  }

  public synchronized Statistic getStatistic(String id) {
    return (Statistic) statMap.get(id);
  }

  public synchronized String[] getStatisticNames() {
    return (String[]) statMap.keySet().toArray(new String[statMap.size()]);
  }

  public synchronized Statistic[] getStatistics() {
    return (Statistic[]) statMap.values().toArray(new Statistic[statMap.size()]);
  }

  @Override
  public MBeanNotificationInfo[] getNotificationInfo() {
    String[] types = new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE };
    String name = AttributeChangeNotification.class.getName();
    String description = "An attribute of this MBean has changed";

    return new MBeanNotificationInfo[] { new MBeanNotificationInfo(types, name, description) };
  }

  protected void sendNotification(String msg, String attr, String type, Object oldVal, Object newVal) {
    sendNotification(new AttributeChangeNotification(this, sequenceNumber.getAndIncrement(),
                                                     System.currentTimeMillis(), msg, attr, type, oldVal, newVal));
  }
}
