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
package com.tc.object.change.event;

import com.tc.object.LogicalOperation;
import com.tc.object.change.TCChangeBufferEvent;
import com.tc.object.dna.api.DNAWriter;
import com.tc.object.dna.api.LogicalChangeID;

/**
 * Nov 22, 2004: Event representing any logical actions that need to be logged
 */
public class LogicalChangeEvent implements TCChangeBufferEvent {
  private final LogicalOperation method;
  private final Object[] parameters;
  private final LogicalChangeID logicalChangeID;

  public LogicalChangeEvent(LogicalOperation method, Object[] parameters, LogicalChangeID id) {
    this.parameters = parameters;
    this.method = method;
    this.logicalChangeID = id;
  }

  @Override
  public void write(DNAWriter writer) {
    writer.addLogicalAction(method, parameters, logicalChangeID);
  }

  public LogicalOperation getLogicalOperation() {
    return method;
  }

  public Object[] getParameters() {
    return parameters;
  }

  public LogicalChangeID getLogicalChangeID(){
    return this.logicalChangeID;
  }

}