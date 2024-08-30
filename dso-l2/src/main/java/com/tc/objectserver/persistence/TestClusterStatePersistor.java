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
package com.tc.objectserver.persistence;

import org.terracotta.corestorage.KeyValueStorage;
import org.terracotta.corestorage.KeyValueStorageConfig;
import org.terracotta.corestorage.StorageManager;
import org.terracotta.corestorage.monitoring.MonitoredResource;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author tim
 */
public class TestClusterStatePersistor extends ClusterStatePersistor {
  public TestClusterStatePersistor(final Map<String, String> map) {
    super(new StorageManager() {
      @Override
      public Map<String, String> getProperties() {
        return map;
      }

      @Override
      public <K, V> KeyValueStorage<K, V> getKeyValueStorage(final String alias, final Class<K> keyClass, final Class<V> valueClass) {
        throw new UnsupportedOperationException("Implement me!");
      }

      @Override
      public void destroyKeyValueStorage(final String alias) {
        throw new UnsupportedOperationException("Implement me!");
      }

      @Override
      public <K, V> KeyValueStorage<K, V> createKeyValueStorage(final String alias, final KeyValueStorageConfig<K, V> config) {
        throw new UnsupportedOperationException("Implement me!");
      }

      @Override
      public void begin() {
        throw new UnsupportedOperationException("Implement me!");
      }

      @Override
      public void commit() {
        throw new UnsupportedOperationException("Implement me!");
      }

      @Override
      public Future<?> start() {
        throw new UnsupportedOperationException("Implement me!");
      }

      @Override
      public void close() {
        throw new UnsupportedOperationException("Implement me!");
      }

      @Override
      public Collection<MonitoredResource> getMonitoredResources() {
        throw new UnsupportedOperationException("Implement me!");
      }
    });
  }

  public TestClusterStatePersistor() {
    this(new HashMap<String, String>());
  }
}
