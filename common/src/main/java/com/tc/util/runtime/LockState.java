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
package com.tc.util.runtime;

public class LockState {
  public static final LockState HOLDING    = new LockState("HOLDING LOCK");
  public static final LockState WAITING_ON = new LockState("WAITING ON LOCK");
  public static final LockState WAITING_TO = new LockState("WAITING TO LOCK");

  private final String          state;

  private LockState(String state) {
    this.state = state;
  }

  @Override
  public String toString() {
    return this.state;
  }

}
