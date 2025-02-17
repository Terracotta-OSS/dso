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
package com.tc.test.config.builder;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

/**
 * @author Ludovic Orban
 */
@XStreamAlias("server")
public class TcServer implements TcMirrorGroupChild {
  
  @XStreamAsAttribute
  private String host;
  @XStreamAsAttribute
  private String name;
  @XStreamAsAttribute
  private String bind;

  private String data;
  private String logs;
  private String index;

  @XStreamAlias("tsa-port")
  private int tsaPort;
  @XStreamAlias("jmx-port")
  private int jmxPort;
  @XStreamAlias("tsa-group-port")
  private int tsaGroupPort;
  @XStreamAlias("management-port")
  private int managementPort;

  @XStreamAlias("offheap")
  private OffHeap offHeap;

  private Security security;

  public TcServer() {
  }

  public String getHost() {
    return host;
  }

  public void setHost(String host) {
    this.host = host;
  }
  
  public TcServer host(String host) {
    setHost(host);
    return this;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public TcServer name(String name) {
    setName(name);
    return this;
  }

  public String getBind() {
    return bind;
  }

  public void setBind(String bind) {
    this.bind = bind;
  }

  public TcServer bind(String bind) {
    setBind(bind);
    return this;
  }

  public String getData() {
    return data;
  }

  public void setData(String data) {
    this.data = data;
  }

  public TcServer data(String data) {
    setData(data);
    return this;
  }

  public String getLogs() {
    return logs;
  }

  public void setLogs(String logs) {
    this.logs = logs;
  }

  public TcServer logs(String logs) {
    setLogs(logs);
    return this;
  }

  public String getIndex() {
    return index;
  }

  public void setIndex(String index) {
    this.index = index;
  }

  public TcServer index(String index) {
    setIndex(index);
    return this;
  }

  public int getTsaPort() {
    return tsaPort;
  }

  public void setTsaPort(int tsaPort) {
    this.tsaPort = tsaPort;
  }

  public TcServer tsaPort(int tsaPort) {
    setTsaPort(tsaPort);
    return this;
  }

  public int getJmxPort() {
    return jmxPort;
  }

  public void setJmxPort(int jmxPort) {
    this.jmxPort = jmxPort;
  }

  public TcServer jmxPort(int jmxPort) {
    setJmxPort(jmxPort);
    return this;
  }

  public int getTsaGroupPort() {
    return tsaGroupPort;
  }

  public void setTsaGroupPort(int tsaGroupPort) {
    this.tsaGroupPort = tsaGroupPort;
  }

  public TcServer tsaGroupPort(int tsaGroupPort) {
    setTsaGroupPort(tsaGroupPort);
    return this;
  }

  public int getManagementPort() {
    return managementPort;
  }

  public void setManagementPort(int managementPort) {
    this.managementPort = managementPort;
  }

  public TcServer managementPort(int managementPort) {
    setManagementPort(managementPort);
    return this;
  }

  public OffHeap getOffHeap() {
    return offHeap;
  }

  public void setOffHeap(OffHeap offHeap) {
    this.offHeap = offHeap;
  }

  public TcServer offHeap(OffHeap offHeap) {
    setOffHeap(offHeap);
    return this;
  }

  public Security getSecurity() {
    return security;
  }

  public void setSecurity(Security security) {
    this.security = security;
  }

  public TcServer security(Security security) {
    setSecurity(security);
    return this;
  }

}
