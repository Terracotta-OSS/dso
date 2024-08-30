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
package com.bigmemory.samples.readwritethrough;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.loader.CacheLoader;

import java.util.Collection;
import java.util.Map;

public class MyCacheLoader implements CacheLoader {

  public Object load(final Object o) throws CacheException {
    return new Element(o, "somevalue");
  }

  public Map loadAll(final Collection collection) {
    return null;
  }

  public Object load(final Object o, final Object o1) {
    return null;
  }

  public Map loadAll(final Collection collection, final Object o) {
    return null;
  }

  public String getName() {
    return null;
  }

  public CacheLoader clone(final Ehcache ehcache) throws CloneNotSupportedException {
    return null;
  }

  public void init() {

  }

  public void dispose() throws CacheException {

  }

  public Status getStatus() {
    return null;
  }
}
