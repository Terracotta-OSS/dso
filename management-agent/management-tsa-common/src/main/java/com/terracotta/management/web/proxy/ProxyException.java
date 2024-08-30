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
package com.terracotta.management.web.proxy;

/**
 * Thrown when a request must be processed by another server, it will then be proxied by ProxyExceptionMapper.
 *
 * @author Ludovic Orban
 */
public class ProxyException extends RuntimeException {
  private final String activeL2WithMBeansUrl;

  public ProxyException(String activeL2WithMBeansUrl) {
    this.activeL2WithMBeansUrl = activeL2WithMBeansUrl;
  }

  public String getActiveL2WithMBeansUrl() {
    return activeL2WithMBeansUrl;
  }
}
