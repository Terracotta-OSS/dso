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

import net.sf.ehcache.CacheEntry;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.writer.CacheWriter;
import net.sf.ehcache.writer.writebehind.operations.SingleOperationType;

import java.util.Collection;

public class MyCacheWriter implements CacheWriter {
  public CacheWriter clone(final Ehcache ehcache) throws CloneNotSupportedException {
    return null;
  }

  // This is where you would initialize the connection to your SOR
  public void init() {

  }

  // This is where you would dispose of the connection to your SOR
  public void dispose() throws CacheException {

  }

  public void write(final Element element) throws CacheException {
    System.out.println("*** CacheWriter : We wrote to the SOR, key = " + element.getObjectKey());
  }

  public void writeAll(final Collection<Element> collection) throws CacheException {

  }

  public void delete(final CacheEntry cacheEntry) throws CacheException {
    System.out.println("*** CacheWriter : We removed from the SOR, key = " + cacheEntry.getKey());
  }

  public void deleteAll(final Collection<CacheEntry> collection) throws CacheException {

  }

  public void throwAway(final Element element, final SingleOperationType singleOperationType, final RuntimeException e) {

  }
}
