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
package com.tc.stats.api;

import com.tc.management.TerracottaMBean;
import com.tc.management.beans.TerracottaOperatorEventsMBean;
import com.tc.management.beans.l1.L1InfoMBean;
import com.tc.net.protocol.tcm.ChannelID;
import com.tc.object.ObjectID;

import javax.management.ObjectName;

public interface DSOClientMBean extends TerracottaMBean {
  public static final String TUNNELED_BEANS_REGISTERED = "tunneled.beans.registered";

  long getClientID();

  String getNodeID();

  boolean isTunneledBeansRegistered();

  ObjectName getL1InfoBeanName();

  L1InfoMBean getL1InfoBean();

  ObjectName getL1DumperBeanName();

  ObjectName getL1OperatorEventsBeanName();

  ObjectName getEnterpriseTCClientBeanName();

  TerracottaOperatorEventsMBean getL1OperatorEventsBean();

  ChannelID getChannelID();

  String getRemoteAddress();

  long getTransactionRate();

  long getReadRate();

  long getWriteRate();

  long getPendingTransactionsCount();

  Number[] getStatistics(String[] names);

  int getLiveObjectCount();

  boolean isResident(ObjectID oid);

  void killClient();

  long getServerMapGetSizeRequestsCount();

  long getServerMapGetValueRequestsCount();

  long getServerMapGetSizeRequestsRate();

  long getServerMapGetValueRequestsRate();
}
