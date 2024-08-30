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
package com.terracotta.toolkit.nonstop;

import com.tc.abortable.AbortableOperationManager;
import com.terracotta.toolkit.ToolkitInitializer;

// TODO: make a builder
public class NonStopContextImpl implements NonStopContext {

  private final NonStopManager                 nonStopManager;

  private final NonStopConfigRegistryImpl      nonStopConfigRegistryImpl;

  private final AbortableOperationManager      abortableOperationManager;

  private final NonstopTimeoutBehaviorResolver nonstopTimeoutBehaviorResolver;

  private final ToolkitInitializer             toolkitInitializer;

  private final NonStopClusterListener         nonStopClusterListener;

  private final ThreadLocal<Boolean>           enabledForCurrentThread = new ThreadLocal<Boolean>();

  public NonStopContextImpl(NonStopManager nonStopManager, NonStopConfigRegistryImpl nonStopConfigRegistryImpl,
                            AbortableOperationManager abortableOperationManager,
                            NonstopTimeoutBehaviorResolver nonstopTimeoutBehaviorResolver,
                            ToolkitInitializer toolkitInitializer,
                            NonStopClusterListener nonStopClusterListener) {
    this.nonStopManager = nonStopManager;
    this.nonStopConfigRegistryImpl = nonStopConfigRegistryImpl;
    this.abortableOperationManager = abortableOperationManager;
    this.nonstopTimeoutBehaviorResolver = nonstopTimeoutBehaviorResolver;
    this.toolkitInitializer = toolkitInitializer;
    this.nonStopClusterListener = nonStopClusterListener;
  }

  @Override
  public NonStopManager getNonStopManager() {
    return nonStopManager;
  }

  @Override
  public NonStopConfigRegistryImpl getNonStopConfigurationRegistry() {
    return nonStopConfigRegistryImpl;
  }

  @Override
  public AbortableOperationManager getAbortableOperationManager() {
    return abortableOperationManager;
  }

  @Override
  public NonstopTimeoutBehaviorResolver getNonstopTimeoutBehaviorResolver() {
    return nonstopTimeoutBehaviorResolver;
  }

  @Override
  public ToolkitInitializer getToolkitInitializer() {
    return toolkitInitializer;
  }

  @Override
  public NonStopClusterListener getNonStopClusterListener() {
    return nonStopClusterListener;
  }

  @Override
  public boolean isEnabledForCurrentThread() {
    return enabledForCurrentThread.get() == null;
  }

  public void enableForCurrentThread(boolean enable) {
    if (enable) {
      enabledForCurrentThread.remove();
    } else {
      enabledForCurrentThread.set(Boolean.FALSE);
    }
  }

}
