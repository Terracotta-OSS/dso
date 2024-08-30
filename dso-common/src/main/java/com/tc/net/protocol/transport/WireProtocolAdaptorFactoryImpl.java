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
package com.tc.net.protocol.transport;

import com.tc.async.api.Sink;
import com.tc.net.protocol.ProtocolSwitch;
import com.tc.net.protocol.TCProtocolAdaptor;

public class WireProtocolAdaptorFactoryImpl implements WireProtocolAdaptorFactory {

  private final Sink httpSink;

  // This version is for the server and will use the HTTP protocol switcher thingy
  public WireProtocolAdaptorFactoryImpl(Sink httpSink) {
    this.httpSink = httpSink;
  }

  public WireProtocolAdaptorFactoryImpl() {
    this(null);
  }

  @Override
  public TCProtocolAdaptor newWireProtocolAdaptor(WireProtocolMessageSink sink) {
    if (httpSink != null) { return new ProtocolSwitch(new WireProtocolAdaptorImpl(sink), httpSink); }
    return new WireProtocolAdaptorImpl(sink);
  }
}
