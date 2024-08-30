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
package com.tc.object;

import com.tc.exception.ImplementMe;
import com.tc.object.dna.api.DNACursor;
import com.tc.object.dna.api.DNAEncoding;
import com.tc.object.dna.api.LiteralAction;
import com.tc.object.dna.api.LogicalAction;
import com.tc.object.dna.api.PhysicalAction;

import java.util.ArrayList;
import java.util.List;

public class TestDNACursor implements DNACursor {
  private final List actions;
  private int        current = -1;

  public TestDNACursor() {
    this(new ArrayList());
  }

  public TestDNACursor(final List actions) {
    this.actions = actions;
  }

  public void addEntireArray(final Object array) {
    this.actions.add(new PhysicalAction(array));
  }

  public void addPhysicalAction(final String addFieldName, final Object addObj, final boolean isref) {
    this.actions.add(new PhysicalAction(addFieldName, addObj, isref));
  }

  public void addLogicalAction(final LogicalOperation method, final Object params[]) {
    this.actions.add(new LogicalAction(method, params));
  }

  public void addArrayAction(final Object[] objects) {
    this.actions.add(new PhysicalAction(objects));
  }

  public void addLiteralAction(final Object value) {
    this.actions.add(new LiteralAction(value));
  }

  @Override
  public boolean next() {
    return this.actions.size() > ++this.current;
  }

  @Override
  public LogicalAction getLogicalAction() {
    return (LogicalAction) this.actions.get(this.current);
  }

  @Override
  public Object getAction() {
    return this.actions.get(this.current);
  }

  @Override
  public PhysicalAction getPhysicalAction() {
    return (PhysicalAction) this.actions.get(this.current);
  }

  @Override
  public boolean next(final DNAEncoding encoding) {
    throw new ImplementMe();
  }

  @Override
  public int getActionCount() {
    return this.actions.size();
  }

  @Override
  public void reset() throws UnsupportedOperationException {
    this.current = -1;
  }

  public void addLiteralAction(final String field, final Object value) {
    // actions.add(new PhysicalAction(field, value));
    this.actions.add(new LiteralAction(value));
  }
}
