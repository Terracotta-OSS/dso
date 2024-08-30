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
package com.tc.net.protocol.delivery;

/**
 * 
 */
public class AbstractState implements State {

  private final String name;

  public AbstractState(String name) {
    this.name = name;
  }

  @Override
  public void enter() {
    // override me if you want
  }

  @Override
  public void execute(OOOProtocolMessage protocolMessage) {
    // override me if you want
  }

  @Override
  public String toString() {
    return name;
  }
}
