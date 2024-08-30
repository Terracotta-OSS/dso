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
package com.tc.object.locks;

import com.tc.async.api.Sink;
import com.tc.async.api.Stage;
import com.tc.object.context.LocksToRecallContext;
import com.tc.object.handler.LockRecaller;

import java.util.Set;

public class LocksRecallServiceImpl implements LocksRecallService {

  private final LockRecaller lockRecaller;
  private final Sink         lockRecallSink;

  public LocksRecallServiceImpl(LockRecaller lockRecaller, Stage lockRecallStage) {
    this.lockRecaller = lockRecaller;
    this.lockRecallSink = lockRecallStage.getSink();
  }

  @Override
  public void recallLocks(Set<LockID> lockIds) {
    this.lockRecallSink.add(new LocksToRecallContext(lockIds));
  }

  @Override
  public void recallLocksInline(Set<LockID> lockIds) {
    lockRecaller.recallLocksInline(lockIds);
  }

}
