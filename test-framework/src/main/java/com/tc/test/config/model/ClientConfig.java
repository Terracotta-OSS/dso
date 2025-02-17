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
package com.tc.test.config.model;

import org.terracotta.tests.base.AbstractClientBase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Configuration for each client <br>
 * Default : <br>
 * run client parrallely : true <br>
 * 
 * @author rsingh
 */
public class ClientConfig {
  private boolean                               parallelClients      = true;
  private final List<String>                    extraClientJvmArgs;
  private Class<? extends AbstractClientBase>[] classes;
  private boolean                               shouldResolveLicense = true;
  private int                                   minHeap              = 64;
  private int                                   maxHeap              = 128;
  private int                                   directMemorySize     = -1;
  private final BytemanConfig                   bytemanConfig        = new BytemanConfig();
  private final Map<Integer, PauseConfig>       pauseConfigs;

  public ClientConfig() {
    extraClientJvmArgs = new ArrayList<String>();
    pauseConfigs = new HashMap<Integer, PauseConfig>();
  }

  public void addClientPauseConfig(PauseConfig pauseConfig, int... clientIndexes) {
    for (int clientIndex : clientIndexes) {
      pauseConfigs.put(clientIndex, pauseConfig);
    }
  }
  
  public PauseConfig getClientPauseConfig(int clientIndex) {
    return pauseConfigs.get(clientIndex);
  }

  /**
   * @return list of extra jvm args for each clients
   */
  public List<String> getExtraClientJvmArgs() {
    return extraClientJvmArgs;
  }

  /**
   * Adds a jvm argument for each client
   * 
   * @param extraClientJvmArg : jvm arg to be added for each client
   */
  public void addExtraClientJvmArg(String extraClientJvmArg) {
    extraClientJvmArgs.add(extraClientJvmArg);
  }

  /**
   * Sets the client classes for the test
   * 
   * @param classes an array of client classes to be run
   */
  public void setClientClasses(Class<? extends AbstractClientBase>[] classes) {
    this.classes = classes;
  }

  /**
   * Sets the classes for the test
   * 
   * @param clientClass the client class to be instantiated
   * @param count number of client class to be instantiated
   */
  public void setClientClasses(Class<? extends AbstractClientBase> clientClass, int count) {
    this.classes = new Class[count];
    for (int i = 0; i < count; i++) {
      classes[i] = clientClass;
    }
  }

  /**
   * @return the classes to be instantiated for the test
   */
  public Class<? extends AbstractClientBase>[] getClientClasses() {
    return classes;
  }

  /**
   * Enable/Disable running of clients parallely
   * 
   * @param parallelClients
   */
  public void setParallelClients(boolean parallelClients) {
    this.parallelClients = parallelClients;
  }

  /**
   * @return true if clients will run in parrallel, false otherwise
   */
  public boolean isParallelClients() {
    return parallelClients;
  }

  /**
   * @param shouldResolveLicense : enable/disable test framework to resolve license to start the client with
   */
  public void setShouldResolveLicense(boolean shouldResolveLicense) {
    this.shouldResolveLicense = shouldResolveLicense;
  }

  /**
   * @return true if test framework is supposed to resolve license for the client
   */
  public boolean shouldResolveLicense() {
    return shouldResolveLicense;
  }

  /**
   * Get the -Xms size to pass to client
   * 
   * @return Minimum heap size
   */
  public int getMinHeap() {
    return minHeap;
  }

  /**
   * Set the min heap size
   * 
   * @param minHeap minimum heap size
   */
  public void setMinHeap(int minHeap) {
    this.minHeap = minHeap;
  }

  /**
   * Get the -Xmx size to pass to Client
   * 
   * @return Maximum heap size
   */
  public int getMaxHeap() {
    return maxHeap;
  }

  /**
   * Set the max heap size
   * 
   * @param maxHeap maximum heap size in MB
   */
  public void setMaxHeap(int maxHeap) {
    this.maxHeap = maxHeap;
  }

  /**
   * Gets the "-XX:MaxDirectMemorySize" to pass to the client
   * 
   * @return -XX:MaxDirectMemorySize
   */
  public int getDirectMemorySize() {
    return directMemorySize;
  }

  /**
   * Sets "-XX:MaxDirectMemorySize"
   * 
   * @param directMemorySize MaxDirectMemorySize in MB
   */
  public void setDirectMemorySize(int directMemorySize) {
    this.directMemorySize = directMemorySize;
  }

  public BytemanConfig getBytemanConfig() {
    return bytemanConfig;
  }
}
