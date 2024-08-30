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
package org.terracotta.management.cli.rest;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ludovic Orban
 */
public class Context {
  private final String url;
  private final boolean failOnEmpty;
  private final List<String> jsonQueries = new ArrayList<String>();
  private final String data;
  private final String username;
  private final String password;

  public Context(String url, List<String> jsonQueries, String data, String username, String password, boolean failOnEmpty) {
    this.url = url;
    this.jsonQueries.addAll(jsonQueries);
    this.data = data;
    this.username = username;
    this.password = password;
    this.failOnEmpty = failOnEmpty;
  }

  public String getUrl() {
    return url;
  }

  public List<String> getJsonQueries() {
    return jsonQueries;
  }

  public String getData() {
    return data;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public boolean isFailOnEmpty() {
    return failOnEmpty;
  }
}
