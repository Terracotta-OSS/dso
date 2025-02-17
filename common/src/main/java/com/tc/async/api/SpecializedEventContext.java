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
package com.tc.async.api;

/**
 * This type of context has a build in execute() method which is run instead of the handler's handleEvent() method.
 * These contexts are used to run some code inband to do stuff after all the other queued event contexts are executed.
 */
public interface SpecializedEventContext extends MultiThreadedEventContext {

  public void execute() throws EventHandlerException;
}
