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
package com.tc.operatorevent;

import java.util.List;
import java.util.Map;

public interface TerracottaOperatorEventHistoryProvider {
  void push(TerracottaOperatorEvent event);

  List<TerracottaOperatorEvent> getOperatorEvents();

  List<TerracottaOperatorEvent> getOperatorEvents(long sinceTimestamp);

  /**
   * Returns the unread event counts broken out by event type name.
   * 
   * @see TerracottaOperatorEvent.EventLevel
   */
  Map<String, Integer> getUnreadCounts();

  boolean markOperatorEvent(TerracottaOperatorEvent operatorEvent, boolean read);
}
