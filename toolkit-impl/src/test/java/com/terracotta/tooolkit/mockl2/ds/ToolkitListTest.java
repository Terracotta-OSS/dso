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
package com.terracotta.tooolkit.mockl2.ds;

import com.tc.object.LogicalOperation;

import org.junit.Assert;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.collections.ToolkitList;
import org.terracotta.toolkit.collections.ToolkitMap;

import com.terracotta.toolkit.mockl2.test.MockPlatformListener;
import com.terracotta.toolkit.mockl2.test.ToolkitUnitTest;

import java.util.List;


public class ToolkitListTest extends ToolkitUnitTest {
  
  public void testToolkitList() {
    Toolkit toolkit = getToolKit();
    ToolkitList<String> toolkitList =  toolkit.getList("list-unitest", String.class);
    toolkitList.add("One");
    toolkitList.add("Two");
    toolkitList.add("Three");
    toolkitList.add("Four");
    toolkitList.add("Six");
    List<String> subList = toolkitList.subList(3, 4);
    for(String item :  subList) {
      System.out.println("----"+item);
    }
    subList.add("Five");
    for(String item :  toolkitList) {
      System.out.println(item);
    }
    Assert.assertTrue(toolkitList.get(4).equals("Five"));
    System.out.println("size of List : " + toolkitList.size());
    Assert.assertEquals(6, toolkitList.size());
  }
  
  
  public void testToolkitMap() {
    Toolkit toolkit = getToolKit();
    ToolkitMap<String, Object> toolkitMap =  toolkit.getMap("map-unitest", String.class, Object.class);
    addPlatformListener(new MockPlatformListener() {
      
      @Override
      public void logicalInvoke(Object object, LogicalOperation method, Object[] params) {
          System.out.println("method : " + method);
          if(params != null) {
            for(Object param : params) {
              System.out.println(param);
            }
          }
        
      }
    });
    toolkitMap.put("organization", "IBM");
    toolkitMap.put("location", "Noida");
    System.out.println("size of Map : " + toolkitMap.size());
    System.out.println("location : " + toolkitMap.get("location"));
    Assert.assertTrue(toolkitMap.get("location").equals("Noida"));
    Assert.assertEquals(2, toolkitMap.size());
  }

}
