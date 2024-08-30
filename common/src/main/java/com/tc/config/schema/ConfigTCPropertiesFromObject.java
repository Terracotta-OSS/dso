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
package com.tc.config.schema;

import com.tc.config.TcProperty;
import com.terracottatech.config.Property;
import com.terracottatech.config.TcProperties;


public class ConfigTCPropertiesFromObject implements ConfigTCProperties {
  private TcProperty[] tcProperties = new TcProperty[0];

  public ConfigTCPropertiesFromObject(TcProperties tcProps) {
    if(tcProps == null)
      return;
    
    Property[] props = tcProps.getPropertyArray();
    int len = props.length;
    tcProperties = new TcProperty[len];
    
    for(int i = 0; i < len; i++){
      tcProperties[i] = new TcProperty(props[i].getName(), props[i].getValue());
    }
  }

  @Override
  public TcProperty[] getTcPropertiesArray() {
    return tcProperties;
  }

}
