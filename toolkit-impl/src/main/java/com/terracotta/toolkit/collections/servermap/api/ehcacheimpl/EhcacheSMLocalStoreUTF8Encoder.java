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

import com.tc.object.dna.impl.UTF8ByteDataHolder;

class EhcacheSMLocalStoreUTF8Encoder {
  private final boolean useEncoding;

  public EhcacheSMLocalStoreUTF8Encoder(boolean useEncoding) {
    this.useEncoding = useEncoding;
  }

  public Object encodeKey(Object key) {

    if (useEncoding && key instanceof String) {
      return new UTF8ByteDataHolder((String) key);
    } else {
      return key;
    }

  }

  public Object decodeKey(Object key) {
    if (useEncoding && key instanceof UTF8ByteDataHolder) {
      return ((UTF8ByteDataHolder) key).asString();
    } else {
      return key;
    }
  }
}
