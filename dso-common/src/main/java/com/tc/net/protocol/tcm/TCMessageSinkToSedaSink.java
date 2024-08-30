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
package com.tc.net.protocol.tcm;

import com.tc.async.api.Sink;

class TCMessageSinkToSedaSink implements TCMessageSink {
  private final Sink destSink;
  private final Sink hydrateSink;
  

  public TCMessageSinkToSedaSink(Sink destSink, Sink hydrateSink) {
    this.destSink = destSink;
    this.hydrateSink = hydrateSink;
  }

  @Override
  public void putMessage(TCMessage message) {    
    HydrateContext context = new HydrateContext(message, destSink);
    hydrateSink.add(context);
  }
  
    
  
}
