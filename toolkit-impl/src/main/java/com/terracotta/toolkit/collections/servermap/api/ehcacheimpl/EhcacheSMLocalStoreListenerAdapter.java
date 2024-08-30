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
package com.terracotta.toolkit.collections.servermap.api.ehcacheimpl;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;

import com.terracotta.toolkit.collections.servermap.api.ServerMapLocalStoreListener;

public class EhcacheSMLocalStoreListenerAdapter implements CacheEventListener {

  private final ServerMapLocalStoreListener serverMapListener;
  private final EhcacheSMLocalStoreUTF8Encoder  encoder;

  public EhcacheSMLocalStoreListenerAdapter(ServerMapLocalStoreListener serverMapListener,
                                            EhcacheSMLocalStoreUTF8Encoder encoder) {
    this.serverMapListener = serverMapListener;
    this.encoder = encoder;
  }

  @Override
  public void notifyElementEvicted(Ehcache cache, Element element) {
    serverMapListener.notifyElementEvicted(encoder.decodeKey(element.getObjectKey()), element.getObjectValue());
  }

  @Override
  public void notifyElementExpired(Ehcache cache, Element element) {
    // no-op
  }

  @Override
  public void notifyElementRemoved(Ehcache cache, Element element) throws CacheException {
    // no-op
  }

  @Override
  public void notifyElementPut(Ehcache cache, Element element) throws CacheException {
    // no-op
  }

  @Override
  public void notifyElementUpdated(Ehcache cache, Element element) throws CacheException {
    // no-op
  }

  @Override
  public void notifyRemoveAll(Ehcache cache) {
    // no-op
  }

  @Override
  public void dispose() {
    // no-op
  }

  @Override
  public Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((serverMapListener == null) ? 0 : serverMapListener.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    EhcacheSMLocalStoreListenerAdapter other = (EhcacheSMLocalStoreListenerAdapter) obj;
    if (serverMapListener == null) {
      if (other.serverMapListener != null) return false;
    } else if (!serverMapListener.equals(other.serverMapListener)) return false;
    return true;
  }

}
