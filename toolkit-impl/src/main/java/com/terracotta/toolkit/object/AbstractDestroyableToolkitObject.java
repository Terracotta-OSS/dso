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
package com.terracotta.toolkit.object;

import org.terracotta.toolkit.object.ToolkitObject;
import org.terracotta.toolkit.rejoin.RejoinException;

import com.tc.platform.PlatformService;
import com.terracotta.toolkit.factory.ToolkitObjectFactory;
import com.terracotta.toolkit.factory.impl.AbstractPrimaryToolkitObjectFactory;
import com.terracotta.toolkit.rejoin.RejoinCallback;
import com.terracotta.toolkit.util.ToolkitObjectStatusImpl;

public abstract class AbstractDestroyableToolkitObject<T extends ToolkitObject> implements DestroyableToolkitObject,
    RejoinCallback {

  protected final AbstractPrimaryToolkitObjectFactory factory;
  protected final DestroyApplicator                   destroyApplicator;
  protected final ToolkitObjectStatusImpl             status;

  public AbstractDestroyableToolkitObject(ToolkitObjectFactory<T> factory, PlatformService platformService) {
    if (!(factory instanceof AbstractPrimaryToolkitObjectFactory)) { throw new IllegalStateException(); }

    this.factory = (AbstractPrimaryToolkitObjectFactory) factory;
    this.destroyApplicator = new DestroyApplicatorImpl(this);
    status = new ToolkitObjectStatusImpl(platformService);
  }

  public final DestroyApplicator getDestroyApplicator() {
    return destroyApplicator;
  }

  @Override
  public final boolean isDestroyed() {
    return status.isDestroyed();
  }

  @Override
  public final void destroy() {
    if (status.isRejoinInProgress()) { throw new RejoinException("Cannot destroy object with name: '" + getName()
                                                                 + "' as rejoin is in progress. (type: "
                                                                 + getClass().getName() + ")"); }
    factory.destroy(this);
  }

  @Override
  public final void rejoinStarted() {
    doRejoinStarted();
  }

  @Override
  public final void rejoinCompleted() {
    doRejoinCompleted();
  }

  protected abstract void doRejoinStarted();

  protected abstract void doRejoinCompleted();

  public void destroyFromCluster() {
    doDestroy();
  }

  /**
   * After destroy
   */
  public abstract void applyDestroy();

  private static class DestroyApplicatorImpl implements DestroyApplicator {

    private final AbstractDestroyableToolkitObject toolkitObject;

    public DestroyApplicatorImpl(AbstractDestroyableToolkitObject toolkitObject) {
      this.toolkitObject = toolkitObject;
    }

    @Override
    public void applyDestroy() {
      toolkitObject.status.setDestroyed();
      toolkitObject.factory.applyDestroy(toolkitObject);
      toolkitObject.applyDestroy();
    }

    @Override
    public void setApplyDestroyCallback(DestroyApplicator applyDestroyCallback) {
      // no-op
    }

  }

}
