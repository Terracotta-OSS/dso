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
package com.terracotta.management.security;

/**
 * Created by lorban on 22/04/14.
 */
public interface SecurityContextService {

  public static class SecurityContext {
    private final String requestTicket;
    private final String signature;
    private final String alias;
    private final String token;

    public SecurityContext(String requestTicket, String signature, String alias, String token) {
      this.requestTicket = requestTicket;
      this.signature = signature;
      this.alias = alias;
      this.token = token;
    }

    public String getRequestTicket() {
      return requestTicket;
    }

    public String getSignature() {
      return signature;
    }

    public String getAlias() {
      return alias;
    }

    public String getToken() {
      return token;
    }
  }

  void setSecurityContext(SecurityContext securityContext);
  SecurityContext getSecurityContext();
  void clearSecurityContext();
}
