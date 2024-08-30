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

import org.terracotta.corestorage.AnonymousTransformerLookup;
import org.terracotta.corestorage.Transformer;

/**
 * @author tim
 */
public class SingletonTransformerLookup extends AnonymousTransformerLookup {
  private final Class<?> targetClass;
  private final Transformer<?, ?> transformer;

  public SingletonTransformerLookup(final Class<?> targetClass, final Transformer<?, ?> transformer) {
    this.targetClass = targetClass;
    this.transformer = transformer;
  }

  @Override
  public <T> Transformer<? super T, ?> lookup(final Class<T> klazz) {
    if (targetClass == klazz) {
      return (Transformer<? super T, ?>) transformer;
    } else {
      return null;
    }
  }
}
