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
package com.terracotta.toolkit;

import org.terracotta.toolkit.feature.NonStopFeature;
import org.terracotta.toolkit.nonstop.NonStopConfiguration;
import org.terracotta.toolkit.nonstop.NonStopConfigurationRegistry;

import com.tc.abortable.AbortableOperationManager;
import com.terracotta.toolkit.feature.EnabledToolkitFeature;

public class NonStopFeatureImpl extends EnabledToolkitFeature implements NonStopFeature {
  private final NonStopToolkitImpl nonStopToolkitImpl;
  private final AbortableOperationManager abortableOperationManager;

  public NonStopFeatureImpl(NonStopToolkitImpl nonStopToolkitImpl, AbortableOperationManager abortableOperationManager) {
    this.nonStopToolkitImpl = nonStopToolkitImpl;
    this.abortableOperationManager = abortableOperationManager;
  }

  @Override
  public void start(NonStopConfiguration nonStopConfig) {
    if (nonStopConfig == null) { return; }

    nonStopToolkitImpl.start(nonStopConfig);
  }

  @Override
  public void finish() {
    nonStopToolkitImpl.stop();
  }

  @Override
  public NonStopConfigurationRegistry getNonStopConfigurationRegistry() {
    return nonStopToolkitImpl.getNonStopConfigurationToolkitRegistry();
  }

  @Override
  public boolean isTimedOut() {
    return abortableOperationManager.isAborted();
  }

}
