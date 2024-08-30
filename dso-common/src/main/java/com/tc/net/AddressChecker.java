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
package com.tc.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

public class AddressChecker {

  private final Set allLocalAddresses;

  public AddressChecker() {
    allLocalAddresses = findAllLocalAddresses();
  }

  public Set getAllLocalAddresses() {
    return allLocalAddresses;
  }

  public boolean isLegalBindAddress(InetAddress bindAddress) {
    if (bindAddress.isAnyLocalAddress()) { return true; }
    if (bindAddress.isLoopbackAddress()) { return true; }
    return allLocalAddresses.contains(bindAddress);
  }

  private Set findAllLocalAddresses() {
    Set rv = new HashSet();

    final Enumeration nics;
    try {
      nics = NetworkInterface.getNetworkInterfaces();
    } catch (SocketException e) {
      throw new RuntimeException(e);
    }

    while (nics.hasMoreElements()) {
      NetworkInterface nic = (NetworkInterface) nics.nextElement();
      Enumeration ips = nic.getInetAddresses();
      while (ips.hasMoreElements()) {
        rv.add(ips.nextElement());
      }
    }

    return Collections.unmodifiableSet(rv);
  }

}
