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

import com.tc.util.AbstractIdentifier;

import org.terracotta.corestorage.Transformer;

/**
 * @author tim
 */
public abstract class AbstractIdentifierTransformer<K extends AbstractIdentifier> implements Transformer<K, Long> {
  private final Class<K> c;

  protected AbstractIdentifierTransformer(final Class<K> c) {
    this.c = c;
  }

  protected abstract K createIdentifier(long id);

  @Override
  public K recover(final Long value) {
    return createIdentifier(value);
  }

  @Override
  public Long transform(final K k) {
    return k.toLong();
  }

  @Override
  public boolean equals(final K left, final Long right) {
    if (c.isInstance(left)) {
      return left.toLong() == right.longValue();
    } else {
      return false;
    }
  }

  @Override
  public Class<Long> getTargetClass() {
    return Long.class;
  }
}
