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
package com.tc.security;

import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Alex Snaps
 */
public class PwProviderUtil {

  private static AtomicReference<PwProvider> backend = new AtomicReference<PwProvider>();

  public static char[] getPasswordTo(URI uri) {
    final PwProvider tcSecurityManager = backend.get();
    if(tcSecurityManager == null) {
      throw new IllegalStateException("We haven't had a BackEnd set yet!");
    }
    return tcSecurityManager.getPasswordFor(uri);
  }

  public static void setBackEnd(final PwProvider securityManager) {
    if(!PwProviderUtil.backend.compareAndSet(null, securityManager)) {
      throw new IllegalStateException("BackEnd was already set!");
    }
  }

  public static PwProvider getProvider() {
    return backend.get();
  }
}
