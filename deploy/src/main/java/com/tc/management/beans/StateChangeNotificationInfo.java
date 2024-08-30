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
package com.tc.management.beans;

import com.tc.l2.state.StateManager;
import com.tc.util.State;

import java.util.HashMap;
import java.util.Map;

public class StateChangeNotificationInfo {
  // order matters: msg, attribute name, attribute type
  private static final String[] ACTIVE_INFO          = new String[] { "TCServer active", "Active",
      "jmx.terracotta.L2.active"                    };
  private static final String[] PASSIVE_UNINIT_INFO  = new String[] { "TCServer passive-uninitialized",
      "Paasive-Uninitialized", "jmx.terracotta.L2.passive-uninitialized" };
  private static final String[] PASSIVE_STANDBY_INFO = new String[] { "TCServer passive-standby", "Passive-Standby",
      "jmx.terracotta.L2.passive-standby"           };
  private static final String[] START_INFO           = new String[] { "TCServer start-state", "Start-State",
      "jmx.terracotta.L2.start-state"               };
  private static final String[] STOP_INFO            = new String[] { "TCServer stop-state", "Stop-State",
      "jmx.terracotta.L2.stop-state"                };
  private static final String[] RECOVERING_INFO        = new String[] { "TCServer recovering", "Recovering",
      "jmx.terracotta.L2.recovering"                };
  private static final String[] SAFE_MODE_INFO        = new String[] { "TCServer safe-mode", "Safe-Mode",
      "jmx.terracotta.L2.safe-mode"                };


  private static final Map<String, String[]>             map;
  static {
    map = new HashMap<String, String[]>();
    map.put(StateManager.ACTIVE_COORDINATOR.getName(), ACTIVE_INFO);
    map.put(StateManager.PASSIVE_STANDBY.getName(), PASSIVE_STANDBY_INFO);
    map.put(StateManager.PASSIVE_UNINITIALIZED.getName(), PASSIVE_UNINIT_INFO);
    map.put(StateManager.START_STATE.getName(), START_INFO);
    map.put(StateManager.STOP_STATE.getName(), STOP_INFO);
    map.put(StateManager.RECOVERING.getName(), RECOVERING_INFO);
    map.put(StateManager.SAFE_MODE_STATE.getName(), SAFE_MODE_INFO);
  }

  public String getMsg(State state) {
    String[] info = map.get(state.getName());
    return info[0];
  }

  public String getAttributeName(State state) {
    String[] info = map.get(state.getName());
    return info[1];
  }

  public String getAttributeType(State state) {
    String[] info = map.get(state.getName());
    return info[2];
  }

}
