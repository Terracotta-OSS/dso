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
package com.tc.config.schema.dynamic;

import com.tc.util.Assert;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link ConfigItemListener} that simply delegates to others.
 */
public class CompoundConfigItemListener implements ConfigItemListener {

  private final Set listeners;

  public CompoundConfigItemListener() {
    this.listeners = new HashSet();
  }

  public synchronized void addListener(ConfigItemListener listener) {
    Assert.assertNotNull(listener);
    this.listeners.add(listener);
  }

  public synchronized void removeListener(ConfigItemListener listener) {
    Assert.assertNotNull(listener);
    this.listeners.remove(listener);
  }

  @Override
  public void valueChanged(Object oldValue, Object newValue) {
    ConfigItemListener[] duplicate;

    synchronized (this) {
      duplicate = (ConfigItemListener[]) this.listeners.toArray(new ConfigItemListener[this.listeners.size()]);
    }

    for (int i = 0; i < duplicate.length; ++i) {
      duplicate[i].valueChanged(oldValue, newValue);
    }
  }

}
