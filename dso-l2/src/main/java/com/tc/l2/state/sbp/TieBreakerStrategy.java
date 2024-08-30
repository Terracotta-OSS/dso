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

public interface TieBreakerStrategy {
  
  enum Result {
    
    /* The result if the requesting node has won the tie breaking attempt */
    WON,
    
    /**
     *  The result if the requesting node has NOT won the the tie breaking
     *  attempt AND this tie-breaker is aware that another node 
     *  has already WON */
    LOST,

    /**
     *  The result if the tie-breaker is not able to break the tie OR 
     *  if the requesting node has NOT won the the tie breaking
     *  attempt and this tie-breaker is not aware that another node 
     *  has already WON */
    DUBIOUS
  }
  
  Result breakTie(State state);

  /**
   * This method can be used as a hook for external intervention when
   * the tie-breaker is not able to break a tie on its own.
   * 
   * @param failOverAction
   */
  void performExternalTieBreaking(FailOverAction failOverAction);
  
  boolean isWaitingForExternalTieBreaking();
}
