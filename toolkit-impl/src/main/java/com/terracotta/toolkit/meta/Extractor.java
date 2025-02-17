/*
 * Copyright Terracotta, Inc.
 * Copyright IBM Corp. 2024, 2025
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
package com.terracotta.toolkit.meta;


import com.tc.object.metadata.MetaDataDescriptor;
import com.tc.platform.PlatformService;

import java.util.Map;
import java.util.Map.Entry;

/**
 * Utility class for extracting internal info from public interface {@link MetaData}
 */
public class Extractor {

  private Extractor() {
    //
  }

  /**
   * Extract internal {@link MetaDataDescriptor} from {@link MetaData}
   * 
   * @param platformService
   */
  public static MetaDataDescriptor extractInternalDescriptorFrom(PlatformService platformService, MetaData metaData) {
    if (metaData instanceof MetaDataImpl) {
      return ((MetaDataImpl) metaData).getInternalMetaDataDescriptor();
    } else {
      MetaDataDescriptor mdd = platformService.createMetaDataDescriptor(metaData.getCategory());
      Map<String, Object> map = metaData.getMetaDatas();
      for (Entry<String, Object> entry : map.entrySet()) {
        mdd.set(entry.getKey(), entry.getValue());
      }
      return mdd;
    }
  }

}
