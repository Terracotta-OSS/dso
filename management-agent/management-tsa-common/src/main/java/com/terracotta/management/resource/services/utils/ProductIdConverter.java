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
package com.terracotta.management.resource.services.utils;

import com.tc.license.ProductID;

import java.util.HashSet;
import java.util.Set;

public class ProductIdConverter {
  
  public static Set<ProductID> stringsToProductsIds(Set<String> clientProductIds) {
    if (clientProductIds ==  null) {
      return null;
    }
    Set<ProductID> productIds = new HashSet<ProductID>();
    for (String clientProductID : clientProductIds) {
      productIds.add(ProductID.valueOf(clientProductID));
    }
    return productIds;
  }

  public static Set<String> productIdsToStrings(Set<ProductID> productIDs) {
    if (productIDs == null) {
      return null;
    }
    Set<String> strings = new HashSet<String>();
    for (ProductID productID : productIDs) {
      strings.add(productID.name());
    }
    return strings;
  }
  
}
