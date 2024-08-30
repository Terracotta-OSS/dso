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
package com.terracotta.toolkit.bulkload;

import java.util.Map;

/**
 * @author tim
 */
public interface BufferBackend<K, V> {

  /**
   * Dump out the local buffered changes into the target.
   *
   * @param buffer locally buffered up changes
   */
  void drain(Map<K, BufferedOperation<V>> buffer);

  /**
   * Ask this backend to create a buffered operation.
   *
   * @param type type of operation
   * @param key key that this operation happened on
   * @param value value (if any) associated with the operation.
   * @param version version of the value if applicable
   * @param createTimeInSecs when the entry was created
   * @param customMaxTTISeconds custom time to idle if it exists
   * @param customMaxTTLSeconds custom time to live if it exists
   * @return buffered operation
   */
  BufferedOperation<V> createBufferedOperation(BufferedOperation.Type type, final K key, V value, long version,
                                               int createTimeInSecs, int customMaxTTISeconds, int customMaxTTLSeconds);
}
