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
package com.tc.util.concurrent;

public interface TCQueue {
  /**
   * Adds the object in the queue
   * @throws InterruptedException 
   */
  public void put(Object obj) throws InterruptedException;
  
  /**
   * Place item in channel only if it can be accepted within msecs milliseconds
   */
  public boolean offer(Object obj, long timeout) throws InterruptedException;
  
  /**
   * Return and remove an item from channel, possibly waiting indefinitely until such an item exists
   * @throws InterruptedException 
   */
  public Object take() throws InterruptedException;
  
  /**
   * Return and remove an item from channel only if one is available within msecs milliseconds
   * @throws InterruptedException 
   */
  public Object poll(long timeout) throws InterruptedException;
  
  /**
   * Return, but do not remove object at head of Channel, or null if it is empty
   */
  public Object peek();

  /**
   * Returns the size of the queue
   */
  public int size();
  
  /**
   * Tells whether queue is empty or not
   */
  public boolean isEmpty();
}
