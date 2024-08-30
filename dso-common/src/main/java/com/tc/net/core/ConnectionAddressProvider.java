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
package com.tc.net.core;

import com.tc.util.StringUtil;

public class ConnectionAddressProvider implements ClusterTopologyChangedListener {

  private volatile ConnectionInfo[] addresses;

  public ConnectionAddressProvider(ConnectionInfo[] addresses) {
    this.addresses = (addresses == null) ? ConnectionInfo.EMPTY_ARRAY : addresses;
  }

  @Override
  public synchronized String toString() {
    return "ConnectionAddressProvider(" + StringUtil.toString(addresses) + ")";
  }

  public synchronized ConnectionAddressIterator getIterator() {
    return new ConnectionAddressIterator(addresses);
  }
  
  @Override
  public synchronized void serversUpdated(ConnectionAddressProvider... addressProviders) {
    for(ConnectionAddressProvider cap: addressProviders) {
      if(cap.getGroupId() == this.getGroupId()) {
        this.addresses = cap.addresses;
      }
    }
  }

  public int getGroupId() {
    if (addresses == null || addresses[0] == null) {
      synchronized (this) {
        if (addresses == null || addresses[0] == null) { return -1; }
      }
    }
    return addresses[0].getGroupId();
  }

  public SecurityInfo getSecurityInfo() {
    SecurityInfo securityInfo = null;

    for (ConnectionInfo address : addresses) {
      if(securityInfo != null && !securityInfo.equals(address.getSecurityInfo())) {
        throw new IllegalStateException("Multiple SecurityInfo differ!");
      }
      securityInfo = address.getSecurityInfo();
    }

    return securityInfo;
  }
}