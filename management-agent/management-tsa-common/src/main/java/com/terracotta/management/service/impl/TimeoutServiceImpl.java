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
package com.terracotta.management.service.impl;

import com.terracotta.management.service.TimeoutService;

/**
 * @author Ludovic Orban
 */
public class TimeoutServiceImpl implements TimeoutService {

  private final ThreadLocal<Long> tlTimeout = new ThreadLocal<Long>();

  private final long defaultCallTimeout;
  private final long defaultConnectionTimeout;

  public TimeoutServiceImpl(long defaultCallTimeout, long defaultConnectionTimeout) {
    this.defaultCallTimeout = defaultCallTimeout;
    this.defaultConnectionTimeout = defaultConnectionTimeout;
  }

  @Override
  public void setCallTimeout(long readTimeout) {
    tlTimeout.set(readTimeout);
  }

  @Override
  public void clearCallTimeout() {
    tlTimeout.remove();
  }

  @Override
  public long getCallTimeout() {
    Long timeout = tlTimeout.get();
    return timeout == null ? defaultCallTimeout : timeout;
  }

  @Override
  public long getConnectionTimeout() {
    return defaultConnectionTimeout;
  }
}
