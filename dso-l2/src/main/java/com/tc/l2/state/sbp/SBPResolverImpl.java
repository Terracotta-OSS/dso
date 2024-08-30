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
package com.tc.l2.state.sbp;

import com.tc.config.schema.setup.FailOverAction;
import com.tc.util.State;

public class SBPResolverImpl implements SBPResolver {

  @Override
  public boolean isEnabled() {
    return false;
  }

  @Override
  public boolean resolveTiedElection(final State state) {
    return true;
  }

  @Override
  public boolean isWaitingForFailOverAction() {
    return false;
  }

  @Override
  public void performFailOverAction(final FailOverAction failOverAction) {
    //no-op
  }
}
