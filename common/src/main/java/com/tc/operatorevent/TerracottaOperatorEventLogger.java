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
package com.tc.operatorevent;

import java.util.concurrent.CopyOnWriteArrayList;

public class TerracottaOperatorEventLogger {

  private final CopyOnWriteArrayList<TerracottaOperatorEventCallback>        callbacks        = new CopyOnWriteArrayList<TerracottaOperatorEventCallback>();
  private final CopyOnWriteArrayList<TerracottaOperatorEventHistoryProvider> historyProviders = new CopyOnWriteArrayList<TerracottaOperatorEventHistoryProvider>();
  private final NodeNameProvider                                             nodeNameProvider;

  public TerracottaOperatorEventLogger(NodeNameProvider nodeIdProvider) {
    this.nodeNameProvider = nodeIdProvider;
  }

  public void registerEventCallback(TerracottaOperatorEventCallback callback) {
    this.callbacks.add(callback);
  }

  public void fireOperatorEvent(TerracottaOperatorEvent event) {
    event.addNodeName(this.nodeNameProvider.getNodeName());
    for (TerracottaOperatorEventHistoryProvider historyProvider : this.historyProviders) {
      historyProvider.push(event);
    }
    for (TerracottaOperatorEventCallback callback : this.callbacks) {
      callback.logOperatorEvent(event);
    }
  }

  public void registerForHistory(TerracottaOperatorEventHistoryProvider historyProvider) {
    this.historyProviders.add(historyProvider);
  }
}
