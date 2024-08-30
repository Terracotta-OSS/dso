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
package com.tc.net.core;

import com.tc.net.TCSocketAddress;
import com.tc.net.core.event.TCListenerEventListener;

import java.net.InetAddress;

public class TestTCListener implements TCListener {

  @Override
  public void addEventListener(TCListenerEventListener lsnr) {
    throw new UnsupportedOperationException();
  }

  @Override
  public InetAddress getBindAddress() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getBindPort() {
    return 0;
  }

  @Override
  public TCSocketAddress getBindSocketAddress() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isStopped() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void removeEventListener(TCListenerEventListener lsnr) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void stop() {
    return;
  }

  @Override
  public void stop(long timeout) {
    return;
  }

}
