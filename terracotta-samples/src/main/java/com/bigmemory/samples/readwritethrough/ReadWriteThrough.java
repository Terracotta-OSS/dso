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

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.io.IOException;

/**
 * Read and Write through
 */
public class ReadWriteThrough {
  public static void main(String[] args) throws IOException {
    System.out.println("**** Retrieve config from xml ****");
    CacheManager manager = CacheManager.newInstance(ReadWriteThrough.class.getResource("/xml/ehcache-readwritethrough.xml"));
    try {
      Cache readWriteThroughCache = manager.getCache("readWriteThroughCache");

      System.out.println("We want to read from the cache, it is going to miss and read from the CacheLoader (hitting the SOR)");
      readWriteThroughCache.get(1);

      System.out.println("We want to read again from the cache, now, it is going to hit and not read from the CacheLoader");
      readWriteThroughCache.get(1);

      System.out.println("We write into the cache, it is going to call the CacheWriter to write to the SOR");
      readWriteThroughCache.putWithWriter(new Element(1, "something"));

      System.out.println("We want to read again from the cache, it is still going to hit and read from the CacheLoader");
      readWriteThroughCache.get(1);

    } finally {
      if (manager != null) manager.shutdown();
    }
  }

}
