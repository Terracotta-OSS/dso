/*
 * The contents of this file are subject to the Terracotta Public License Version
 * 2.0 (the "License"); You may not use this file except in compliance with the
 * License. You may obtain a copy of the License at
 *
 *      http://terracotta.org/legal/terracotta-public-license.
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific language governing rights and limitations under the License.
 *
 * The Covered Software is Terracotta Platform.
 *
 * The Initial Developer of the Covered Software is
 *      Terracotta, Inc., a Software AG company
 */
package com.tc.object;

import com.tc.cluster.DsoClusterEvent;
import com.tc.cluster.DsoClusterListener;

/**
 *
 * @author mscott
 */
public abstract class DsoClusterErrorListener implements DsoClusterListener {
//  nodeError needs to be implemented by subclass;

  @Override
  public void nodeRejoined(DsoClusterEvent event) {
    
  }

  @Override
  public void operationsDisabled(DsoClusterEvent event) {
    
  }

  @Override
  public void operationsEnabled(DsoClusterEvent event) {
    
  }

  @Override
  public void nodeLeft(DsoClusterEvent event) {
    
  }

  @Override
  public void nodeJoined(DsoClusterEvent event) {
    
  }
  
}
