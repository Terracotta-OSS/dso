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
package com.terracotta.management.resource;

import java.io.Serializable;

/**
 * A {@link org.terracotta.management.resource.AbstractEntityV2} representing a server MBean
 * from the management API.
 *
 * @author Ludovic Orban
 */
public class MBeanEntityV2 extends AbstractTsaEntityV2 {

  private String sourceId;
  private String objectName;
  private AttributeEntityV2[] attributes;

  public String getSourceId() {
    return sourceId;
  }

  public void setSourceId(String sourceId) {
    this.sourceId = sourceId;
  }

  public String getObjectName() {
    return objectName;
  }

  public void setObjectName(String objectName) {
    this.objectName = objectName;
  }

  public AttributeEntityV2[] getAttributes() {
    return attributes;
  }

  public void setAttributes(AttributeEntityV2[] attributes) {
    this.attributes = attributes;
  }

  public static class AttributeEntityV2 implements Serializable {
    private String name;
    private String type;

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }

}
