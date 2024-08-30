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
package com.tc.config.schema.repository;

import com.tc.config.schema.validate.ConfigurationValidator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A mock {@link ApplicationsRepository}, for use in tests.
 */
public class MockApplicationsRepository implements ApplicationsRepository {

  private int    numRepositoryFors;
  private String lastApplicationName;

  private int    numApplicationNames;

  private Map    returnedRepositories;

  public MockApplicationsRepository() {
    this.returnedRepositories = new HashMap();

    reset();
  }

  public void reset() {
    this.numRepositoryFors = 0;
    this.lastApplicationName = null;

    this.numApplicationNames = 0;
  }
  
  @Override
  public void addRepositoryValidator(ConfigurationValidator validator) {
    // nothing here yet
  }

  @Override
  public MutableBeanRepository repositoryFor(String applicationName) {
    ++this.numRepositoryFors;
    this.lastApplicationName = applicationName;
    return (MutableBeanRepository) this.returnedRepositories.get(applicationName);
  }

  @Override
  public Iterator applicationNames() {
    ++this.numApplicationNames;
    return this.returnedRepositories.keySet().iterator();
  }

  public String getLastApplicationName() {
    return lastApplicationName;
  }

  public int getNumApplicationNames() {
    return numApplicationNames;
  }

  public int getNumRepositoryFors() {
    return numRepositoryFors;
  }

  public void setReturnedRepositories(Map returnedRepositories) {
    this.returnedRepositories = returnedRepositories;
  }

}
